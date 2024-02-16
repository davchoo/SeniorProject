package team.travel.travelplanner.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.locationtech.jts.geom.*;

import java.io.IOException;

public class GeometryDeserializer extends StdNodeBasedDeserializer<Geometry> {
    private static final int WGS84_SRID = 4326;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), WGS84_SRID);

    public GeometryDeserializer() {
        super(Geometry.class);
    }

    @Override
    public Geometry convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        if (!root.has("type")) {
            return null;
        }
        String type = root.get("type").asText();
        if ("GeometryCollection".equals(type)) {
            return readGeometryCollection(root, ctxt);
        }
        return readGeometry(type, root);
    }

    private Geometry readGeometryCollection(JsonNode node, DeserializationContext ctxt) throws IOException {
        if (!(node.get("geometries") instanceof ArrayNode geometries) || geometries.isEmpty()) {
            return null;
        }
        Geometry[] collection = new Geometry[geometries.size()];
        for (int i = 0; i< collection.length; i++) {
            collection[i] = convert(geometries.get(i), ctxt);
        }
        return GEOMETRY_FACTORY.createGeometryCollection(collection);
    }

    private Geometry readGeometry(String type, JsonNode node) throws IOException {
        if (!(node.get("coordinates") instanceof ArrayNode coordinates) || coordinates.isEmpty()) {
            return null;
        }
        return switch (type) {
            case "Point" -> readPoint(coordinates);
            case "MultiPoint" -> readMultiPoint(coordinates);
            case "LineString" -> readLineString(coordinates);
            case "MultiLineString" -> readMultiLineString(coordinates);
            case "Polygon" -> readPolygon(coordinates);
            case "MultiPolygon" -> readMultiPolygon(coordinates);
            default -> null;
        };
    }

    private Coordinate readCoordinate(JsonNode coordinate) throws IOException {
        int size = coordinate.size();
        return switch (size) {
            case 0, 1 -> throw new IOException("Expected at least 2 elements for position");
            case 2 -> new Coordinate(coordinate.get(0).asDouble(), coordinate.get(1).asDouble());
            default -> new Coordinate(coordinate.get(0).asDouble(), coordinate.get(1).asDouble(), coordinate.get(2).asDouble());
        };
    }

    private Coordinate[] readCoordinates(JsonNode coordinates) throws IOException {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }
        Coordinate[] coords = new Coordinate[coordinates.size()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = readCoordinate(coordinates.get(i));
        }
        return coords;
    }

    private Point readPoint(JsonNode coordinates) throws IOException {
        return GEOMETRY_FACTORY.createPoint(readCoordinate(coordinates));
    }

    private Geometry readMultiPoint(JsonNode coordinates) throws IOException {
        return GEOMETRY_FACTORY.createMultiPointFromCoords(readCoordinates(coordinates));
    }

    private LineString readLineString(JsonNode coordinates) throws IOException {
        return GEOMETRY_FACTORY.createLineString(readCoordinates(coordinates));
    }

    private MultiLineString readMultiLineString(JsonNode coordinates) throws IOException {
        LineString[] lineStrings = new LineString[coordinates.size()];
        for (int i = 0; i < lineStrings.length; i++) {
            lineStrings[i] = readLineString(coordinates.get(i));
        }
        return GEOMETRY_FACTORY.createMultiLineString(lineStrings);
    }

    private Polygon readPolygon(JsonNode coordinates) throws IOException {
        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(readCoordinates(coordinates.get(0)));
        if (coordinates.size() == 1) {
            // No holes
            return GEOMETRY_FACTORY.createPolygon(shell);
        }
        LinearRing[] holes = new LinearRing[coordinates.size() - 1];
        for (int i = 1; i < coordinates.size(); i++) {
            holes[i - 1] = GEOMETRY_FACTORY.createLinearRing(readCoordinates(coordinates.get(i)));
        }
        return GEOMETRY_FACTORY.createPolygon(shell, holes);
    }

    private MultiPolygon readMultiPolygon(JsonNode coordinates) throws IOException {
        Polygon[] polygons = new Polygon[coordinates.size()];
        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = readPolygon(coordinates.get(i));
        }
        return GEOMETRY_FACTORY.createMultiPolygon(polygons);
    }
}
