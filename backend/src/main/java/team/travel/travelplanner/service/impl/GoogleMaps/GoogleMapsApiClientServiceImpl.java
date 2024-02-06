package team.travel.travelplanner.service.impl.GoogleMaps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import team.travel.travelplanner.service.GoogleMapsApiClientService;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;

public class GoogleMapsApiClientServiceImpl implements GoogleMapsApiClientService {

    private final GeoApiContext context;

    public GoogleMapsApiClientServiceImpl(String googleMapsAPIKey) {
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

    @Override
    public DirectionsResult getDirections(String origin, String destination) throws IOException, InterruptedException, ApiException {
        DirectionsResult result = DirectionsApi.newRequest(context)
                .origin(origin)
                .destination(destination)
                .await();
        return result;
    }

    @Override
    public void findPlaces(LatLng location, String type, int radius) {
        try {

            // Perform a nearby search for places of the specified type around the location
            PlacesSearchResponse placesResponse = PlacesApi.nearbySearchQuery(context,
                            new com.google.maps.model.LatLng(location.latitude(), location.longitude()))
                    .type(PlaceType.valueOf(type.toUpperCase()))
                    .radius(radius)
                    .await();

            // Process the search response
            for (int i = 0; i < placesResponse.results.length; i++) {
                System.out.println("Place " + (i + 1) + ": " + placesResponse.results[i].name);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            // Handle exception
        }

    }
}
