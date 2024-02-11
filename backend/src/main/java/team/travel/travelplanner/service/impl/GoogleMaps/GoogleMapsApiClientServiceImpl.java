package team.travel.travelplanner.service.impl.GoogleMaps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import team.travel.travelplanner.model.FuelOptions;
import team.travel.travelplanner.service.GoogleMapsApiDirectionsService;
import team.travel.travelplanner.service.GoogleMapsApiDistanceService;
import team.travel.travelplanner.service.GoogleMapsApiFuelPriceService;
import team.travel.travelplanner.service.GoogleMapsApiClientService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.Math.*;

public class GoogleMapsApiClientServiceImpl implements GoogleMapsApiClientService, GoogleMapsApiFuelPriceService,
        GoogleMapsApiDirectionsService, GoogleMapsApiDistanceService {

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
            PlacesSearchResponse placesResponse = PlacesApi.nearbySearchQuery(context, location)
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

    @Override
    public double haversine(LatLng origin, LatLng destination) {
        double RADIUS_OF_EARTH_KM = 6371.01;
        double lat1 = toRadians(origin.lat);
        double lon1 = toRadians(origin.lng);
        double lat2 = toRadians(destination.lat);
        double lon2 = toRadians(destination.lng);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = pow(sin(dlat / 2), 2) + cos(lat1) * cos(lat2) * pow(sin(dlon / 2), 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        return RADIUS_OF_EARTH_KM * c;
    }

    // Don't want to continously call DistanceMatrix API so using haversine could introduce a little error.
    //    @Override
//    public DistanceMatrix calculateDistance(LatLng origin, LatLng destination) throws IOException, InterruptedException, ApiException {
//        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
//                .origins(origin)
//                .destinations(destination)
//                .mode(TravelMode.DRIVING)
//                .await();
//        return distanceMatrix;
//    }

    private void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }
}
