package team.travel.travelplanner;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import org.jaitools.jts.CoordinateSequence2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import team.travel.travelplanner.entity.WeatherForecastFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.model.Route;
import team.travel.travelplanner.model.RouteWeatherFeature;
import team.travel.travelplanner.repository.WeatherForecastFeatureRepository;
import team.travel.travelplanner.service.WeatherDataService;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.time.*;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@Sql({"/schema-postgres.sql"})
public class WeatherDataTest {

    private static final ZoneId EST = ZoneId.of("-05:00");
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    private static final Geometry GRID_0_0 = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
        GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                0.0, 0.0,
                1.0, 0.0,
                1.0, 1.0,
                0.0, 1.0,
                0.0, 0.0
        ))
    });

    private static final Random RANDOM = new Random();

    @Autowired
    private WeatherForecastFeatureRepository weatherForecastFeatureRepository;

    @Autowired
    private WeatherDataService weatherDataService;

    @BeforeEach
    void setup() {
        RANDOM.setSeed(0x414d424552L);
    }

    private static WeatherForecastFeature setupFeature(int forecastDay, boolean afternoonIssuance, LocalDate fileDate, WeatherFeatureType type, Geometry geometry) {
        WeatherForecastFeature feature = new WeatherForecastFeature();
        feature.setDay(forecastDay);
        feature.setPopUpContent("Day " + forecastDay);
        feature.setRetrievalTimestamp(Instant.now());
        if (!afternoonIssuance) {
            feature.setFileDate(ZonedDateTime.of(fileDate, LocalTime.of(4, 18), EST));
        } else {
            feature.setFileDate(ZonedDateTime.of(fileDate, LocalTime.of(16, 18), EST));
        }
        if (!afternoonIssuance || forecastDay != 1) {
            LocalDate validDate = fileDate.plusDays(forecastDay - 1);
            feature.setValidStart(ZonedDateTime.of(validDate, LocalTime.of(7, 0), EST));
            feature.setValidEnd(feature.getValidStart().plusHours(24));
        } else {
            feature.setValidStart(ZonedDateTime.of(fileDate, LocalTime.of(19, 0), EST));
            feature.setValidEnd(feature.getValidStart().plusHours(12));
        }
        feature.setRetrievalTimestamp(Instant.now());
        feature.setWeatherFeatureType(type);
        feature.setGeometry(geometry);
        return feature;
    }

    static Stream<Arguments> duplicateFeatures() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                        -75.11418, 39.70694,
                        -75.12149, 39.70494,
                        -75.12731, 39.7117,
                        -75.1206, 39.71588,
                        -75.11418, 39.70694
                ))
        });
        WeatherForecastFeature template1 = setupFeature(1, false, today, WeatherFeatureType.RAIN, geometry);

        Geometry geometry2 = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                        -75, 39,
                        -76, 39,
                        -76, 40,
                        -75, 40,
                        -75, 39
                ))
        });
        WeatherForecastFeature template2 = setupFeature(2, true, tomorrow, WeatherFeatureType.CRITICAL_FIRE_WEATHER_POSSIBLE, geometry2);

        List<Consumer<WeatherForecastFeature>> propertyCopiers = List.of(
                feature -> feature.setDay(template2.getDay()),
                feature -> feature.setPopUpContent(template2.getPopUpContent()),
                feature -> feature.setFileDate(template2.getFileDate()),
                feature -> {
                    feature.setValidStart(template2.getValidStart());
                    feature.setValidEnd(template2.getValidEnd());
                },
                feature -> feature.setWeatherFeatureType(template2.getWeatherFeatureType()),
                feature -> feature.setGeometry(template2.getGeometry())
        );
        // Generate features with all permutations of differing properties
        return IntStream.range(0, (1 << propertyCopiers.size()))
                .mapToObj(i -> {
                    WeatherForecastFeature older = new WeatherForecastFeature(template1);
                    WeatherForecastFeature newer = new WeatherForecastFeature(template1);
                    for (int copierI = 0; copierI < propertyCopiers.size(); copierI++) {
                        if (((i >> copierI) & 1) == 1) {
                            propertyCopiers.get(copierI).accept(newer);
                        }
                    }
                    newer.setRetrievalTimestamp(Instant.now());
                    return Arguments.of(i, older, newer);
                });
    }

    @ParameterizedTest
    @MethodSource("duplicateFeatures")
    @Transactional
    public void testDeduplicate(int i, WeatherForecastFeature older, WeatherForecastFeature newer) {
        older = weatherForecastFeatureRepository.save(older);
        newer.setRetrievalTimestamp(Instant.now());
        weatherForecastFeatureRepository.save(newer);

        assertEquals(2, weatherForecastFeatureRepository.count());
        weatherForecastFeatureRepository.deduplicate();
        if ((i & 0b110101) != 0) { // Day, file date, weather feature type, or geometry was changed
            // Both features should be kept
            assertEquals(2, weatherForecastFeatureRepository.count());
        } else {
            // Features should be deduplicated
            List<WeatherForecastFeature> features = weatherForecastFeatureRepository.findAll();
            assertEquals(1, features.size());
            WeatherForecastFeature feature = features.getFirst();
            // Older feature should be kept
            assertEquals(older.getId(), feature.getId());
        }
    }

    private Route generateRoute(int segments, double minLat, double minLng, double maxLat, double maxLng, Instant startTime, Instant endLimit) {
        final long MIN_DURATION = 1000;
        long totalDurationMillis = Duration.between(startTime, endLimit).toMillis();
        long availableMillis = totalDurationMillis - MIN_DURATION * segments;

        double[] portionUsed = new double[segments];
        double total = 0;
        for (int i = 0; i < segments; i++) {
            portionUsed[i] = RANDOM.nextDouble();
            total += portionUsed[i];
        }

        int[] durations = new int[segments];
        for (int i = 0; i < segments; i++) {
            durations[i] = (int) (MIN_DURATION + (availableMillis * portionUsed[i] / total));
        }

        int numVertices = segments + 1;
        double[] coordinates = new double[numVertices * 2];
        for (int i = 0; i < numVertices; i++) {
            coordinates[2 * i] = RANDOM.nextDouble(minLng, maxLng);
            coordinates[2 * i + 1] = RANDOM.nextDouble(minLat, maxLat);
        }
        CoordinateSequence sequence = new PackedCoordinateSequence.Double(coordinates, 2, 0);
        return new Route(EncodedPolylineUtils.encodePolyline(sequence), durations, startTime);
    }

    @ParameterizedTest
    @EnumSource(WeatherFeatureType.class)
    void testCheckRouteAllInside(WeatherFeatureType type) {
        LocalDate today = LocalDate.now();
        // Create a rain feature today at grid 0,0
        WeatherForecastFeature feature = setupFeature(1, false, today, type, GRID_0_0);
        feature = weatherForecastFeatureRepository.save(feature);

        // An hour after the feature is valid and an hour before the feature is invalid
        Instant startTime = feature.getValidStart().plusHours(1).toInstant();
        Instant limitTime = feature.getValidEnd().minusHours(1).toInstant();
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route within the 0.1, 0.1 to 0.9, 0.9 "square"
        Route route = generateRoute(NUM_SEGMENTS, 0.1, 0.1, 0.9, 0.9, startTime, limitTime);
        List<RouteWeatherFeature> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // All segments should be covered with rain
        assertEquals(NUM_SEGMENTS, result.size());
        BitSet bitSet = new BitSet();
        bitSet.set(0, NUM_SEGMENTS);
        for (RouteWeatherFeature routeFeature : result) {
            // i corresponds to a segment
            assertTrue(routeFeature.i() >= 0);
            assertTrue(routeFeature.i() < NUM_SEGMENTS);
            bitSet.clear(routeFeature.i());
            // Metadata matches feature
            assertEquals(feature.getDay(), routeFeature.forecastDay());
            assertEquals(feature.getFileDate().toInstant(), routeFeature.fileDate());
            assertEquals(feature.getWeatherFeatureType(), routeFeature.weatherFeatureType());
            // startTimestamp -> endTimestamp duration matches provided duration
            Duration duration = Duration.between(routeFeature.startTimestamp(), routeFeature.endTimestamp());
            assertEquals(route.durations()[routeFeature.i()], duration.toMillis());
            // startTimestamp of the segment is correct
            int millisOffset = IntStream.of(route.durations())
                    .limit(routeFeature.i())
                    .sum();
            duration = Duration.between(route.startTime(), routeFeature.startTimestamp());
            assertEquals(millisOffset, duration.toMillis());
        }
        // All segments were accounted for
        assertTrue(bitSet.isEmpty());
    }

    @Test
    void testCheckRouteOutsideValidPeriod() {
        LocalDate today = LocalDate.now();
        // Create a rain feature today at grid 0,0
        WeatherForecastFeature feature = setupFeature(1, false, today, WeatherFeatureType.RAIN, GRID_0_0);
        feature = weatherForecastFeatureRepository.save(feature);

        // Start time and limit time is after the feature expires
        Instant startTime = feature.getValidEnd().plusHours(1).toInstant();
        Instant limitTime = startTime.plus(Duration.ofHours(2));
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route within the 0.1, 0.1 to 0.9, 0.9 "square" but outside the valid period
        Route route = generateRoute(NUM_SEGMENTS, 0.1, 0.1, 0.9, 0.9, startTime, limitTime);
        // Check the weather along the route
        List<RouteWeatherFeature> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // No segments should be returned
        assertTrue(result.isEmpty());
    }

    @Test
    void testCheckRouteOutsideArea() {
        LocalDate today = LocalDate.now();
        // Create a rain feature today at grid 0,0
        WeatherForecastFeature feature = setupFeature(1, false, today, WeatherFeatureType.RAIN, GRID_0_0);
        feature = weatherForecastFeatureRepository.save(feature);

        // An hour after the feature is valid and an hour before the feature is invalid
        Instant startTime = feature.getValidStart().plusHours(1).toInstant();
        Instant limitTime = feature.getValidEnd().minusHours(1).toInstant();
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route outside the 0,0 grid square but inside the valid period
        Route route = generateRoute(NUM_SEGMENTS, 1.1, 1.1, 1.9, 1.9, startTime, limitTime);
        // Check the weather along the route
        List<RouteWeatherFeature> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // No segments should be returned
        assertTrue(result.isEmpty());
    }
}
