package team.travel.travelplanner.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import team.travel.travelplanner.model.weather.nws.GeoJSONGeometry;

import java.io.IOException;

/**
 * Jackson does not support JsonUnwrapped and polymorphic deserialization at the same time.
 * So we manually deserialize and wrap Geometry into GeoJSONGeometry.
 */
public class GeoJSONGeometryDeserializer extends StdNodeBasedDeserializer<GeoJSONGeometry> {
    private final GeometryDeserializer geometryDeserializer = new GeometryDeserializer();

    protected GeoJSONGeometryDeserializer() {
        super(GeoJSONGeometry.class);
    }

    @Override
    public GeoJSONGeometry convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        return new GeoJSONGeometry(geometryDeserializer.convert(root, ctxt));
    }
}
