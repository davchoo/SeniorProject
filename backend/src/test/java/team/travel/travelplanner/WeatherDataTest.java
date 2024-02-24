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
import team.travel.travelplanner.entity.WeatherFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;
import team.travel.travelplanner.model.weather.WeatherFeatureModel;
import team.travel.travelplanner.repository.WeatherFeatureRepository;
import team.travel.travelplanner.service.WeatherDataService;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private WeatherFeatureRepository weatherFeatureRepository;

    @Autowired
    private WeatherDataService weatherDataService;

    @BeforeEach
    void setup() {
        RANDOM.setSeed(0x414d424552L);
    }

    private static WeatherFeature createFeature(int forecastDay, boolean afternoonIssuance, LocalDate fileDate, WeatherFeatureType type, Geometry geometry) {
        WeatherFeature feature = new WeatherFeature();
        feature.setDay(forecastDay);
        feature.setPopUpContent("Day " + forecastDay);
        feature.setRetrievalTimestamp(Instant.now());
        if (!afternoonIssuance) {
            feature.setFileDate(ZonedDateTime.of(fileDate, LocalTime.of(4, 0), EST).toInstant());
        } else {
            feature.setFileDate(ZonedDateTime.of(fileDate, LocalTime.of(16, 0), EST).toInstant());
        }
        if (!afternoonIssuance || forecastDay != 1) {
            LocalDate validDate = fileDate.plusDays(forecastDay - 1);
            feature.setValidStart(ZonedDateTime.of(validDate, LocalTime.of(7, 0), EST).toInstant());
            feature.setValidEnd(feature.getValidStart().plus(24, ChronoUnit.HOURS));
        } else {
            feature.setValidStart(ZonedDateTime.of(fileDate, LocalTime.of(19, 0), EST).toInstant());
            feature.setValidEnd(feature.getValidStart().plus(12, ChronoUnit.HOURS));
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
        WeatherFeature template1 = createFeature(1, false, today, WeatherFeatureType.RAIN, geometry);

        Geometry geometry2 = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                        -75, 39,
                        -76, 39,
                        -76, 40,
                        -75, 40,
                        -75, 39
                ))
        });
        WeatherFeature template2 = createFeature(2, true, tomorrow, WeatherFeatureType.CRITICAL_FIRE_WEATHER_POSSIBLE, geometry2);

        List<Consumer<WeatherFeature>> propertyCopiers = List.of(
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
                    WeatherFeature older = new WeatherFeature(template1);
                    WeatherFeature newer = new WeatherFeature(template1);
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
    public void testDeduplicate(int i, WeatherFeature older, WeatherFeature newer) {
        older = weatherFeatureRepository.save(older);
        newer.setRetrievalTimestamp(Instant.now());
        weatherFeatureRepository.save(newer);

        assertEquals(2, weatherFeatureRepository.count());
        weatherFeatureRepository.deduplicate();
        if ((i & 0b110101) != 0) { // Day, file date, weather feature type, or geometry was changed
            // Both features should be kept
            assertEquals(2, weatherFeatureRepository.count());
        } else {
            // Features should be deduplicated
            List<WeatherFeature> features = weatherFeatureRepository.findAll();
            assertEquals(1, features.size());
            WeatherFeature feature = features.getFirst();
            // Older feature should be kept
            assertEquals(older.getId(), feature.getId());
        }
    }

    private RouteModel generateRoute(int segments, double minLat, double minLng, double maxLat, double maxLng, Instant startTime, Instant endLimit) {
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
        return new RouteModel(EncodedPolylineUtils.encodePolyline(sequence), durations, startTime);
    }

    @ParameterizedTest
    @EnumSource(WeatherFeatureType.class)
    void testCheckRouteAllInside(WeatherFeatureType type) {
        LocalDate today = LocalDate.now();
        // Create a rain feature today at grid 0,0
        WeatherFeature feature = createFeature(1, false, today, type, GRID_0_0);
        feature = weatherFeatureRepository.save(feature);

        // An hour after the feature is valid and an hour before the feature is invalid
        Instant startTime = feature.getValidStart().plus(1, ChronoUnit.HOURS);
        Instant limitTime = feature.getValidEnd().minus(1, ChronoUnit.HOURS);
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route within the 0.1, 0.1 to 0.9, 0.9 "square"
        RouteModel route = generateRoute(NUM_SEGMENTS, 0.1, 0.1, 0.9, 0.9, startTime, limitTime);
        List<SegmentWeatherModel> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // All segments should be covered with rain
        assertEquals(NUM_SEGMENTS, result.size());
        BitSet bitSet = new BitSet();
        bitSet.set(0, NUM_SEGMENTS);
        for (SegmentWeatherModel routeFeature : result) {
            // segmentId corresponds to a segment
            assertTrue(routeFeature.segmentId() >= 0);
            assertTrue(routeFeature.segmentId() < NUM_SEGMENTS);
            bitSet.clear(routeFeature.segmentId());
            // Metadata matches feature
            assertEquals(feature.getDay(), routeFeature.forecastDay());
            assertEquals(feature.getFileDate(), routeFeature.fileDate());
            assertEquals(feature.getWeatherFeatureType(), routeFeature.weatherFeatureType());
            // startTimestamp -> endTimestamp duration matches provided duration
            Duration duration = Duration.between(routeFeature.startTimestamp(), routeFeature.endTimestamp());
            assertEquals(route.durations()[routeFeature.segmentId()], duration.toMillis());
            // startTimestamp of the segment is correct
            int millisOffset = IntStream.of(route.durations())
                    .limit(routeFeature.segmentId())
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
        WeatherFeature feature = createFeature(1, false, today, WeatherFeatureType.RAIN, GRID_0_0);
        feature = weatherFeatureRepository.save(feature);

        // Start time and limit time is after the feature expires
        Instant startTime = feature.getValidEnd().plus(1, ChronoUnit.HOURS);
        Instant limitTime = startTime.plus(2, ChronoUnit.HOURS);
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route within the 0.1, 0.1 to 0.9, 0.9 "square" but outside the valid period
        RouteModel route = generateRoute(NUM_SEGMENTS, 0.1, 0.1, 0.9, 0.9, startTime, limitTime);
        // Check the weather along the route
        List<SegmentWeatherModel> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // No segments should be returned
        assertTrue(result.isEmpty());
    }

    @Test
    void testCheckRouteOutsideArea() {
        LocalDate today = LocalDate.now();
        // Create a rain feature today at grid 0,0
        WeatherFeature feature = createFeature(1, false, today, WeatherFeatureType.RAIN, GRID_0_0);
        feature = weatherFeatureRepository.save(feature);

        // An hour after the feature is valid and an hour before the feature is invalid
        Instant startTime = feature.getValidStart().plus(1, ChronoUnit.HOURS);
        Instant limitTime = feature.getValidEnd().minus(1, ChronoUnit.HOURS);
        final int NUM_SEGMENTS = 20;
        // Check the weather along a random route outside the 0,0 grid square but inside the valid period
        RouteModel route = generateRoute(NUM_SEGMENTS, 1.1, 1.1, 1.9, 1.9, startTime, limitTime);
        // Check the weather along the route
        List<SegmentWeatherModel> result = weatherDataService.checkRouteWeather(route.geometry(GEOMETRY_FACTORY), route.durations(), route.startTime());
        // No segments should be returned
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFeatures() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        WeatherFeature feature1 = createFeature(1, false, today, WeatherFeatureType.RAIN, GRID_0_0);
        feature1 = weatherFeatureRepository.save(feature1);

        WeatherFeature feature2 = createFeature(1, false, today, WeatherFeatureType.FREEZING_RAIN_POSSIBLE, GRID_0_0);
        feature2 = weatherFeatureRepository.save(feature2);

        WeatherFeature day2feature = createFeature(2, false, today, WeatherFeatureType.FREEZING_RAIN_POSSIBLE, GRID_0_0);
        day2feature = weatherFeatureRepository.save(day2feature);

        WeatherFeature afternoonFeature = createFeature(1, true, today, WeatherFeatureType.SNOW, GRID_0_0);
        afternoonFeature = weatherFeatureRepository.save(afternoonFeature);

        WeatherFeature olderFeature = createFeature(1, false, yesterday, WeatherFeatureType.HEAVY_SNOW_POSSIBLE, GRID_0_0);
        olderFeature = weatherFeatureRepository.save(olderFeature);

        // Check available file dates
        List<Instant> availableFileDates = weatherDataService.getAvailableFileDates();
        assertEquals(3, availableFileDates.size());
        // dates are sorted in ascending order
        assertEquals(availableFileDates.stream().sorted().toList(), availableFileDates);
        // dates correspond to features saved in the repository
        assertEquals(olderFeature.getFileDate(), availableFileDates.getFirst());
        assertEquals(feature1.getFileDate(), availableFileDates.get(1));
        assertEquals(feature2.getFileDate(), availableFileDates.get(1));
        assertEquals(day2feature.getFileDate(), availableFileDates.get(1));
        assertEquals(afternoonFeature.getFileDate(), availableFileDates.get(2));

        // Get yesterday's day 1 features
        List<WeatherFeatureModel> results = weatherDataService.getFeatures(availableFileDates.getFirst(), 1);
        assertEquals(1, results.size());
        // Model is mapped from the entity correctly
        WeatherFeatureModel model = results.getFirst();
        assertEquals(WeatherFeatureModel.from(olderFeature), model);
        assertEquals(olderFeature.getDay(), model.day());
        assertEquals(olderFeature.getFileDate(), model.fileDate());
        assertEquals(olderFeature.getValidStart(), model.validStart());
        assertEquals(olderFeature.getValidEnd(), model.validEnd());
        assertEquals(olderFeature.getPopUpContent(), model.popUpContent());
        assertEquals(olderFeature.getWeatherFeatureType(), model.type());
        assertEquals(olderFeature.getGeometry(), model.geometry());

        // No features for yesterday day 2
        results = weatherDataService.getFeatures(availableFileDates.getFirst(), 2);
        assertTrue(results.isEmpty());

        // Get today's day 1 morning issuance features
        results = weatherDataService.getFeatures(availableFileDates.get(1), 1);
        results = results.stream().sorted(Comparator.comparing(WeatherFeatureModel::type)).toList(); // sort by type so order is consistent
        assertEquals(2, results.size());
        assertEquals(WeatherFeatureModel.from(feature2), results.getFirst());
        assertEquals(WeatherFeatureModel.from(feature1), results.get(1));

        // Get today's day 2 morning issuance features
        results = weatherDataService.getFeatures(availableFileDates.get(1), 2);
        assertEquals(1, results.size());
        assertEquals(WeatherFeatureModel.from(day2feature), results.getFirst());

        // Get today's day 1 afternoon issuance features
        results = weatherDataService.getFeatures(availableFileDates.get(2), 1);
        assertEquals(1, results.size());
        assertEquals(WeatherFeatureModel.from(afternoonFeature), results.getFirst());

        // No features for today day 2 afternoon
        results = weatherDataService.getFeatures(availableFileDates.get(2), 2);
        assertTrue(results.isEmpty());
    }
}
