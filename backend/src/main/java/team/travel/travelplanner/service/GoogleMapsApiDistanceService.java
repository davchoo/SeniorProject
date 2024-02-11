package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

import java.io.IOException;

public interface GoogleMapsApiDistanceService {
    double haversine(LatLng origin, LatLng destination);
}
