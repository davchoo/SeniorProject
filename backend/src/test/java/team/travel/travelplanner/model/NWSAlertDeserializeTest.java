package team.travel.travelplanner.model;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import team.travel.travelplanner.model.weather.nws.FeatureCollection;
import team.travel.travelplanner.model.weather.nws.GeoJSONObject;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class NWSAlertDeserializeTest {

    @Test
    void testDeserializeAlert() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
        GeoJSONObject featureCollection = restClient.get()
                .uri(URI.create("https://api.weather.gov/alerts/active"))
                .retrieve()
                .body(GeoJSONObject.class);
        assertInstanceOf(FeatureCollection.class, featureCollection);
    }
}
