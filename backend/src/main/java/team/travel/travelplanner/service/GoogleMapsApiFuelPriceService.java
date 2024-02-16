package team.travel.travelplanner.service;

import com.google.maps.errors.ApiException;
import team.travel.travelplanner.entity.GasStation;

import java.io.IOException;

public interface GoogleMapsApiFuelPriceService {
    GasStation getGasStations(String placeId) throws IOException, InterruptedException, ApiException;
}
