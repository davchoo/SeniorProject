package team.travel.travelplanner.service;

import com.google.maps.model.LatLng;
public interface GoogleMapsApiDistanceService {
    double haversine(LatLng origin, LatLng destination);
}
