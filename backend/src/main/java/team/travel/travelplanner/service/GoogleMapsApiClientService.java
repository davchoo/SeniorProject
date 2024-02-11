package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import team.travel.travelplanner.model.FuelOptions;

import java.io.IOException;

public interface GoogleMapsApiClientService {


    void findPlaces(LatLng location, String type, int radius);

    void getPlaceDetails(String place);

}
