package team.travel.travelplanner.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.geosolutions.jaiext.jts.CoordinateSequence2D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.*;
import team.travel.travelplanner.model.weather.nws.GeoJSONGeometry;
import team.travel.travelplanner.model.weather.nws.GeoJSONObject;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class GeoJSONDeserializeTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    // GeoJSON examples taken from https://datatracker.ietf.org/doc/html/rfc7946#autoid-41
    static Stream<Arguments> geometryTestCases() {
        return Stream.of(
                Arguments.of(
                        "{\"type\":\"Point\",\"coordinates\":[100.0,0.0]}",
                        GEOMETRY_FACTORY.createPoint(new Coordinate(100.0, 0.0))
                ),
                Arguments.of(
                        "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                        GEOMETRY_FACTORY.createLineString(new CoordinateSequence2D(100.0, 0.0, 101.0, 1.0))
                ),
                Arguments.of(
                        "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]]}",
                        GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(100.0, 0.0, 101.0, 0.0, 101.0, 1.0, 100.0, 1.0, 100.0, 0.0))
                ),
                Arguments.of(
                        "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]],[[100.8,0.8],[100.8,0.2],[100.2,0.2],[100.2,0.8],[100.8,0.8]]]}",
                        GEOMETRY_FACTORY.createPolygon(
                                GEOMETRY_FACTORY.createLinearRing(new CoordinateSequence2D(100.0, 0.0, 101.0, 0.0, 101.0, 1.0, 100.0, 1.0, 100.0, 0.0)),
                                new LinearRing[]{
                                        GEOMETRY_FACTORY.createLinearRing(new CoordinateSequence2D(100.8, 0.8, 100.8, 0.2, 100.2, 0.2, 100.2, 0.8, 100.8, 0.8)),
                                }
                        )
                ),
                Arguments.of(
                        "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                        GEOMETRY_FACTORY.createMultiPoint(new CoordinateSequence2D(100.0, 0.0, 101.0, 1.0))
                ),
                Arguments.of(
                        "{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[102.0,2.0],[103.0,3.0]]]}",
                        GEOMETRY_FACTORY.createMultiLineString(new LineString[]{
                                GEOMETRY_FACTORY.createLineString(new CoordinateSequence2D(100.0, 0.0, 101.0, 1.0)),
                                GEOMETRY_FACTORY.createLineString(new CoordinateSequence2D(102.0, 2.0, 103.0, 3.0))
                        })
                ),
                Arguments.of(
                        "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102.0,2.0],[103.0,2.0],[103.0,3.0],[102.0,3.0],[102.0,2.0]]],[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]],[[100.2,0.2],[100.2,0.8],[100.8,0.8],[100.8,0.2],[100.2,0.2]]]]}",
                        GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{
                            GEOMETRY_FACTORY.createPolygon(new CoordinateSequence2D(102.0, 2.0, 103.0, 2.0, 103.0, 3.0, 102.0, 3.0, 102.0, 2.0)),
                            GEOMETRY_FACTORY.createPolygon(
                                    GEOMETRY_FACTORY.createLinearRing(new CoordinateSequence2D(100.0, 0.0, 101.0, 0.0, 101.0, 1.0, 100.0, 1.0, 100.0, 0.0)),
                                    new LinearRing[]{
                                            GEOMETRY_FACTORY.createLinearRing(new CoordinateSequence2D(100.2, 0.2, 100.2, 0.8, 100.8, 0.8, 100.8, 0.2, 100.2, 0.2)),
                                    }
                            )
                        })
                ),
                Arguments.of(
                        "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0]},{\"type\":\"LineString\",\"coordinates\":[[101.0,0.0],[102.0,1.0]]}]}",
                        GEOMETRY_FACTORY.createGeometryCollection(new Geometry[]{
                                GEOMETRY_FACTORY.createPoint(new Coordinate(100.0, 0.0)),
                                GEOMETRY_FACTORY.createLineString(new CoordinateSequence2D(101.0, 0.0, 102.0, 1.0))
                        })
                ),
                // Null cases
                Arguments.of(
                        "{\"type\":\"Point\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"LineString\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"Polygon\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiPoint\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiLineString\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiPolygon\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"GeometryCollection\"}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"Point\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"LineString\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"Polygon\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiPoint\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiLineString\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"MultiPolygon\",\"coordinates\":[]}",
                        null
                ),
                Arguments.of(
                        "{\"type\":\"GeometryCollection\",\"geometries\":[]}",
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("geometryTestCases")
    void testDeserialize(String json, Geometry geometry) throws IOException {
        GeoJSONObject geoJSONObject = OBJECT_MAPPER.readValue(json, GeoJSONObject.class);
        assertInstanceOf(GeoJSONGeometry.class, geoJSONObject);

        Geometry deserializedGeometry = ((GeoJSONGeometry) geoJSONObject).geometry();
        assertEquals(geometry, deserializedGeometry);
    }
}
