package team.travel.travelplanner.service.impl.GoogleMaps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.travel.travelplanner.config.GoogleMapsApiConfig;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Random;

public class GoogleMapsApiClientService {

    private final GeoApiContext context;

    private String googleMapsDirectionsApiUrl = "https://maps.googleapis.com/maps/api/directions/json";

    private String googleMapsPlacesNearbyApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    //Figure our how we want to store keys. Assuming in application properties?
    private String key;

    private String jsonResponseFilePath;

    public GoogleMapsApiClientService(String googleMapsAPIKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(googleMapsAPIKey)
                .build();
    }

    public void test() throws IOException, InterruptedException, ApiException {
        GeocodingResult[] results =  GeocodingApi.geocode(context,
                "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));

// Invoke .shutdown() after your application is done making requests
        context.shutdown();
    }

}
