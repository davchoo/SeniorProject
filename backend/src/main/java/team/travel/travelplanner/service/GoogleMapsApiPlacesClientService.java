package team.travel.travelplanner.service;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;

public interface GoogleMapsApiPlacesClientService {
    PlacesSearchResponse findPlaces(LatLng location, String type, int radius);
}
