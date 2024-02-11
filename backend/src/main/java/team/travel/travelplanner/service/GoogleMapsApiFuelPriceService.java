package team.travel.travelplanner.service;

import team.travel.travelplanner.model.FuelOptions;

import java.io.IOException;

public interface GoogleMapsApiFuelPriceService {
    FuelOptions getFuelPrices(String placeId) throws IOException, InterruptedException;
}
