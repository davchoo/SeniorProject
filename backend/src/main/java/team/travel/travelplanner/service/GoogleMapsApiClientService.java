package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import team.travel.travelplanner.model.FuelOptions;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;

public interface GoogleMapsApiClientService {

    DirectionsResult getDirections(String origin, String destination) throws IOException, InterruptedException, ApiException;

    void findPlaces(LatLng location, String type, int radius);

    void getPlaceDetails(String place);

    FuelOptions getFuelPrices(String placeId) throws IOException, InterruptedException;
}
