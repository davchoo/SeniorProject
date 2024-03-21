package team.travel.travelplanner.service;

import team.travel.travelplanner.model.google.GoogleGasStation;

import java.io.IOException;

public interface GoogleMapsApiFuelPriceService {
    GoogleGasStation getGasStation(String placeId) throws IOException;
}
