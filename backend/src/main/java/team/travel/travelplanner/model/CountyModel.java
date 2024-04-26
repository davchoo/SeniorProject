package team.travel.travelplanner.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.serializer.WKB64GeometrySerializer;

public record CountyModel(
        String fips,
        String countyName,
        String stateAbbrev,
        @JsonSerialize(using = WKB64GeometrySerializer.class)
        Geometry geometry
) {
}
