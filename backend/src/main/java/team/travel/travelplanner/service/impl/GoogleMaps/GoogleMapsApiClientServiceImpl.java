package team.travel.travelplanner.service.impl.GoogleMaps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import team.travel.travelplanner.model.FuelOptions;
import team.travel.travelplanner.service.GoogleMapsApiClientService;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GoogleMapsApiClientServiceImpl implements GoogleMapsApiClientService {

    private final GeoApiContext context;

    private String apiKey;

    public GoogleMapsApiClientServiceImpl(String googleMapsAPIKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(googleMapsAPIKey)
                .build();
        setApiKey(googleMapsAPIKey);
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
                System.out.println("Place " + (i + 1) + ": " + placesResponse.results[i]);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            // Handle exception
        }

    }

    @Override
    public void getPlaceDetails(String place){
        try {

            PlaceDetailsRequest request = new PlaceDetailsRequest(context).placeId(place);
            PlaceDetails placeDetails = request.await();
            System.out.println(placeDetails);


        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    /**
     * Google Maps Java Api Client does not have fuel prices so I have to query for fuel_options.
     * @param placeId
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public FuelOptions getFuelPrices(String placeId) throws IOException, InterruptedException {
        String url = String.format("https://places.googleapis.com/v1/places/%s?key=%s&fields=fuel_options", placeId, apiKey);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        FuelOptions fuelOptions = mapper.readValue(response.body(), FuelOptions.class);
        return fuelOptions;
    }

    private void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }
}
