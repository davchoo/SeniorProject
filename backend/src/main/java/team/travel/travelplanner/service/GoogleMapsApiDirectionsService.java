package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

public interface GoogleMapsApiDirectionsService {
    DirectionsResult getDirections(LatLng origin, LatLng destination) throws IOException, InterruptedException, ApiException;
}
