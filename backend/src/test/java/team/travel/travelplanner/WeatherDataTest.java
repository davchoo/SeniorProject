package team.travel.travelplanner;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import org.jaitools.jts.CoordinateSequence2D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import team.travel.travelplanner.entity.WeatherForecastFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.repository.WeatherForecastFeatureRepository;

import java.time.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@Sql({"/schema-postgres.sql"})
public class WeatherDataTest {

    private static final ZoneId EST = ZoneId.of("-05:00");
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    @Autowired
    private WeatherForecastFeatureRepository weatherForecastFeatureRepository;

    static Stream<Arguments> duplicateFeatures() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        WeatherForecastFeature template1 = new WeatherForecastFeature();
        template1.setDay(1);
        template1.setPopUpContent("Day 1");
        template1.setRetrievalTimestamp(Instant.now());
        template1.setFileDate(ZonedDateTime.of(today, LocalTime.of(4, 18), EST));
        template1.setValidStart(ZonedDateTime.of(today, LocalTime.of(7, 0), EST));
        template1.setValidEnd(template1.getValidStart().plusHours(24));
        template1.setWeatherFeatureType(WeatherFeatureType.RAIN);

        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                        -75.11418, 39.70694,
                        -75.12149, 39.70494,
                        -75.12731, 39.7117,
                        -75.1206, 39.71588,
                        -75.11418, 39.70694
                ))
        });
        template1.setGeometry(geometry);

        WeatherForecastFeature template2 = new WeatherForecastFeature();
        template2.setDay(2);
        template2.setPopUpContent("Day 2");
        template2.setRetrievalTimestamp(Instant.now());
        template2.setFileDate(ZonedDateTime.of(tomorrow, LocalTime.of(4, 18), EST));
        template2.setValidStart(ZonedDateTime.of(tomorrow, LocalTime.of(7, 0), EST));
        template2.setValidEnd(template2.getValidStart().plusHours(24));
        template2.setWeatherFeatureType(WeatherFeatureType.CRITICAL_FIRE_WEATHER_POSSIBLE);

        Geometry geometry2 = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(
                        -75, 39,
                        -76, 39,
                        -76, 40,
                        -75, 40,
                        -75, 39
                ))
        });
        template2.setGeometry(geometry2);

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
        weatherForecastFeatureRepository.deleteAll();
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
}
