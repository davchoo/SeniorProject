package team.travel.travelplanner.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.WKBWriter;

import java.io.IOException;
import java.util.Base64;

public class WKB64GeometrySerializer extends JsonSerializer<Geometry> {
    @Override
    public void serialize(Geometry value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        WKBWriter wkbWriter = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN, false); // Can't be reused across threads
        String wkbGeometry = Base64.getEncoder().encodeToString(wkbWriter.write(value));
        gen.writeString(wkbGeometry);
    }
}
