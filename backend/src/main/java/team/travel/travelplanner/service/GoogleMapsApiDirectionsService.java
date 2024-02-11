package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;

public interface GoogleMapsApiDirectionsService {
    DirectionsResult getDirections(String origin, String destination) throws IOException, InterruptedException, ApiException;
}
