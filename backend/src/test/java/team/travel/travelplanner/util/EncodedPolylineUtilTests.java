package team.travel.travelplanner.util;

import org.jaitools.jts.CoordinateSequence2D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.CoordinateSequence;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncodedPolylineUtilTests {

    private static Stream<Arguments> provideCoordinatePolylinePairs() {
        return Stream.of(
                Arguments.of(new CoordinateSequence2D(), ""), // Empty coordinate sequence and encoded polyline
                Arguments.of(
                        new CoordinateSequence2D(
                                -75.11418, 39.70694,
                                -75.12149, 39.70494,
                                -75.12731, 39.7117,
                                -75.1206, 39.71588
                        ),
                        "kgjqFrvmiMnKtl@gi@jc@cY}h@"
                ), // Polyline around Rowan University
                Arguments.of(

                        new CoordinateSequence2D(
                                -111.88899, 40.75818,
                                -75.11418, 39.70694
                        ),
                        "sqwwFdi|iTfilEqqm_F"
                ) // Polyline from Salt Lake City to Rowan University
        );
    }

    @ParameterizedTest
    @MethodSource("provideCoordinatePolylinePairs")
    public void encoding(CoordinateSequence coordinateSequence, String encodedPolyline) {
        String encoded = EncodedPolylineUtils.encodePolyline(coordinateSequence);
        assertEquals(encodedPolyline, encoded);
    }

    @ParameterizedTest
    @MethodSource("provideCoordinatePolylinePairs")
    public void decoding(CoordinateSequence coordinateSequence, String encodedPolyline) {
        CoordinateSequence decoded = EncodedPolylineUtils.decodePolyline(encodedPolyline);
        assertEquals(coordinateSequence.size(), decoded.size());
        assertEquals(2, decoded.getDimension());
        assertEquals(0, decoded.getMeasures());

        for (int i = 0; i < decoded.size(); i++) {
            assertEquals(coordinateSequence.getX(i), decoded.getX(i));
            assertEquals(coordinateSequence.getY(i), decoded.getY(i));
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoordinatePolylinePairs")
    public void encodeDecode(CoordinateSequence coordinateSequence, String encodedPolyline) {
        String encoded = EncodedPolylineUtils.encodePolyline(coordinateSequence);
        CoordinateSequence decoded = EncodedPolylineUtils.decodePolyline(encoded);

        String encoded2 = EncodedPolylineUtils.encodePolyline(decoded);
        assertEquals(encodedPolyline, encoded2);
    }
}
