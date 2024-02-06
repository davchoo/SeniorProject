package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;

public interface GoogleMapsApiClientService {

    public DirectionsResult getDirections(String origin, String destination) throws IOException, InterruptedException, ApiException;

    public void findPlaces(LatLng location, String type, int radius);
}
