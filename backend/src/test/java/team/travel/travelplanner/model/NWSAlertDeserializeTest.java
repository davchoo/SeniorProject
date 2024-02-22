package team.travel.travelplanner.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import team.travel.travelplanner.model.geojson.Feature;
import team.travel.travelplanner.model.geojson.FeatureCollection;
import team.travel.travelplanner.model.geojson.GeoJSONObject;
import team.travel.travelplanner.model.weather.WeatherAlertModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class NWSAlertDeserializeTest {

    @Test
    void testDeserializeAlert() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Resource alertJson = new ClassPathResource("alerts.json");
        GeoJSONObject geoJSONObject = objectMapper.readValue(alertJson.getContentAsString(StandardCharsets.UTF_8), GeoJSONObject.class);
        assertInstanceOf(FeatureCollection.class, geoJSONObject);

        FeatureCollection featureCollection = (FeatureCollection) geoJSONObject;
        for (Feature feature : featureCollection.features()) {
            WeatherAlertModel alert = objectMapper.convertValue(feature.properties(), WeatherAlertModel.class);
            System.out.println(objectMapper.writeValueAsString(alert));
            // TODO additional validation?
        }
    }
}
