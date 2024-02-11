package team.travel.travelplanner.service.impl.GoogleMaps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import team.travel.travelplanner.model.FuelOptions;
import team.travel.travelplanner.service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.Math.*;

public class GoogleMapsApiClientServiceImpl implements GoogleMapsApiFuelPriceService,
        GoogleMapsApiDirectionsService, GoogleMapsApiDistanceService, GoogleMapsApiPlacesClientService {

    private final GeoApiContext context;

    private String apiKey;

    public GoogleMapsApiClientServiceImpl(String googleMapsAPIKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(googleMapsAPIKey)
                .build();
        setApiKey(googleMapsAPIKey);
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
    public PlacesSearchResponse findPlaces(LatLng location, String type, int radius) {
        try {

            // Perform a nearby search for places of the specified type around the location
            PlacesSearchResponse placesResponse = PlacesApi.nearbySearchQuery(context, location)
                    .type(PlaceType.valueOf(type.toUpperCase()))
                    .radius(radius)
                    .await();
            return placesResponse;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return null;
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


    /**
     * Calculates the distance between two points on the Earth's surface using the Haversine formula.
     *
     * @param origin      The origin point (latitude and longitude) in degrees.
     * @param destination The destination point (latitude and longitude) in degrees.
     * @return The distance between the origin and destination points in meters.
     */
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

        return RADIUS_OF_EARTH_KM * c * 1000;
    }

     // Don't want to continously call DistanceMatrix API so using haversine could introduce a little error.
    public DistanceMatrix calculateDistance(LatLng origin, LatLng destination) throws IOException, InterruptedException, ApiException {
        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                .origins(origin)
                .destinations(destination)
                .mode(TravelMode.DRIVING)
                .await();
        return distanceMatrix;
    }

    private void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }
}
