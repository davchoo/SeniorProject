package team.travel.travelplanner.service;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;

import java.io.IOException;

public interface GoogleMapsApiPlacesClientService {
    PlacesSearchResponse findPlaces(LatLng location, String type) throws IOException;
}
