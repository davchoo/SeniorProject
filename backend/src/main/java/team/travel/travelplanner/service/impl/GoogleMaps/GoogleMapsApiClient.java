package team.travel.travelplanner.service.impl.GoogleMaps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import team.travel.travelplanner.config.GoogleMapsApiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class GoogleMapsApiClient {

    private String googleMapsDirectionsApiUrl = "https://maps.googleapis.com/maps/api/directions/json";

    //Figure our how we want to store keys. Assuming in application properties?
    private String key;

    private String jsonResponseFilePath;

    public String getDirections(double departureLat, double departureLng, double arrivalLat, double arrivalLng) {

        String apiUrl = String.format("%s?origin=%f,%f&destination=%f,%f&key=%s",
                googleMapsDirectionsApiUrl, departureLat, departureLng, arrivalLat, arrivalLng, key);
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
            Path filePath = directoryPath.resolve("response.json");

            Files.write(filePath, response.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // Handle exception appropriately (e.g., log the error)
            e.printStackTrace();
        }
    }

    private String parseDirectionsResponse(String response) {

        return "Parsed directions from API response";
    }

}
