/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package team.travel.travelplanner.util;


import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

/**
 * Code which handles polyline encoding and decoding.
 * This code was originally written by Peter Karich and can be found at:
 * https://github.com/graphhopper/graphhopper/blob/e649aaed8d3f4378bf2d8889bbbc2318261eabb2/web-api/src/main/java/com/graphhopper/http/WebHelper.java
 * This was adapted for use in travel-planner by David Choo.
 * decodePolyline and encodePolyline was modified to use CoordinateSequence from JTS.
 * In addition, support for elevation was removed.
 */
public class EncodedPolylineUtils {
    private static final double PRECISION = 1e5;

    public static CoordinateSequence decodePolyline(String encoded) {
        // ~1.5 characters per coordinate, initial capacity has to be even as the capacity is only checked after each pair
        double[] coordinates = new double[Math.max(encoded.length() / 3, 2) * 2];
        int index = 0;
        int coordinateIndex = 0;
        int len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            if (coordinates.length <= coordinateIndex) {
                // Resize array to fit more coordinates
                double[] newCoordinates = new double[coordinates.length * 2];
                System.arraycopy(coordinates, 0, newCoordinates, 0, coordinates.length);
                coordinates = newCoordinates;
            }
            // latitude
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLatitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += deltaLatitude;

            // longitude
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLongitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += deltaLongitude;

            coordinates[coordinateIndex++] = ((double) lng) / PRECISION;
            coordinates[coordinateIndex++] = ((double) lat) / PRECISION;
        }
        if (coordinates.length > coordinateIndex) {
            // Shrink coordinates to the actual number of decoded coordinates
            double[] newCoordinates = new double[coordinateIndex];
            System.arraycopy(coordinates, 0, newCoordinates, 0, coordinateIndex);
            coordinates = newCoordinates;
        }
        return new PackedCoordinateSequence.Double(coordinates, 2, 0);
    }

    public static String encodePolyline(CoordinateSequence poly) {
        StringBuilder sb = new StringBuilder();
        int size = poly.size();
        int prevLat = 0;
        int prevLon = 0;
        for (int i = 0; i < size; i++) {
            int num = (int) Math.floor(poly.getY(i) * PRECISION);
            encodeNumber(sb, num - prevLat);
            prevLat = num;
            num = (int) Math.floor(poly.getX(i) * PRECISION);
            encodeNumber(sb, num - prevLon);
            prevLon = num;
        }
        return sb.toString();
    }

    private static void encodeNumber(StringBuilder sb, int num) {
        num = num << 1;
        if (num < 0) {
            num = ~num;
        }
        while (num >= 0x20) {
            int nextValue = (0x20 | (num & 0x1f)) + 63;
            sb.append((char) (nextValue));
            num >>= 5;
        }
        num += 63;
        sb.append((char) (num));
    }
}
