package team.travel.travelplanner.service.impl.GoogleMaps;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import team.travel.travelplanner.model.google.GoogleGasStation;
import team.travel.travelplanner.service.GoogleMapsApiFuelPriceService;
import team.travel.travelplanner.service.GoogleMapsApiPlacesClientService;

import java.io.IOException;

public class GoogleMapsApiClientServiceImpl implements GoogleMapsApiFuelPriceService, GoogleMapsApiPlacesClientService {

    private final GeoApiContext context;

    private final String apiKey;

    private final RestClient restClient;

    public GoogleMapsApiClientServiceImpl(String googleMapsAPIKey, RestClient.Builder restClientBuilder) {
        this.context = new GeoApiContext.Builder()
                .apiKey(googleMapsAPIKey)
                .build();
        this.apiKey = googleMapsAPIKey;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public PlacesSearchResponse findPlaces(LatLng location, String type, int radius) {
        try {

            // Perform a nearby search for places of the specified type around the location
            PlacesSearchResponse placesResponse = PlacesApi.nearbySearchQuery(context, location)
                    .type(PlaceType.valueOf(type.toUpperCase()))
                    .rankby(RankBy.DISTANCE)
                    .await();
            return placesResponse;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return null;
    }

    /**
     * Google Maps Java Api Client does not have fuel prices so I have to query for fuel_options.
     * @param placeId
     * @return
     * @throws IOException
     */
    @Override
    public GoogleGasStation getGasStation(String placeId) throws IOException {
        ResponseEntity<GoogleGasStation> response = restClient.get()
                .uri("https://places.googleapis.com/v1/places/{place_id}?key={api_key}&fields=fuelOptions,displayName,id,formattedAddress,location", placeId, apiKey)
                .retrieve()
                .toEntity(GoogleGasStation.class);
        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            return response.getBody();
        } else {
            throw new IOException("Failed to fetch data. Response code: " + response.getStatusCode());
        }
    }
}
