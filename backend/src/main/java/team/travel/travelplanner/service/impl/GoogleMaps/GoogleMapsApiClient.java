package team.travel.travelplanner.service.impl.GoogleMaps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.travel.travelplanner.config.GoogleMapsApiService;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Random;

@Component
public class GoogleMapsApiClient {

    private String googleMapsDirectionsApiUrl = "https://maps.googleapis.com/maps/api/directions/json";

    private String googleMapsPlacesNearbyApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    //Figure our how we want to store keys. Assuming in application properties?
    private String key;

    private String jsonResponseFilePath;

    public String getPlacesNearby(Optional<String> keyword, LatLng location, int radius,
                                  Optional<String> type) {
        try {
            // Build the URI using UriComponentsBuilder for better handling of URL components
            String apiUrl = UriComponentsBuilder.fromUriString(googleMapsPlacesNearbyApiUrl)
                    .queryParam("keyword", keyword)
                    .queryParam("location", String.format("%f,%f", location.latitude(), location.longitude()))
                    .queryParam("radius", radius)
                    .queryParam("type", type)
                    .queryParam("key", key)
                    .build().toUriString();

            System.out.println(apiUrl);

            // Make a GET request to the API
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            System.out.println(response);

            // Save the response to a JSON file for testing purposes
            saveResponseToFile(response);

            return parseNearbySearchInfo(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception according to your application's requirements
            return null;
        }
    }

    public String getDirections(LatLng departureLocation, LatLng arrivalLocation) {

        String apiUrl = String.format("%s?origin=%f,%f&destination=%f,%f&key=%s",
                googleMapsDirectionsApiUrl, departureLocation.latitude(),
               departureLocation.longitude(), arrivalLocation.latitude(), arrivalLocation.longitude(), key);
        System.out.println(apiUrl);

        // Make a GET request to the API
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        System.out.println(response);

        // Save the response to a JSON file so I dont have to requery it every tme for testing purposes
        saveResponseToFile(response);

        return parseDirectionsResponse(response);
    }

    public String getKey(GoogleMapsApiService config) {
        return config.getGoogleMapsApiKey();
    }

    private void saveResponseToFile(String response) {
        try {
            Path directoryPath = Path.of("/Users/lukasdeloach/Desktop/SeniorProject/backend/src/main/java/team/travel/travelplanner/service");
            // Just saving to a randomly named file for now. Testing purposes to figure out how to transform data.
            Path filePath = directoryPath.resolve("response" + +new Random().nextInt(5) + ".json");

            Files.write(filePath, response.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // Handle exception appropriately
            e.printStackTrace();
        }
    }

    private String parseNearbySearchInfo(String response){
        return "Parsed Places - will determine how this should look";
    }

    private String parseDirectionsResponse(String response) {

        return "Parsed directions from API response";
    }

}
