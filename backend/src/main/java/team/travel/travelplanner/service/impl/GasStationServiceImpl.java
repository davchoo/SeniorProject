package team.travel.travelplanner.service.impl;

import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.service.GasStationService;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClient;
import team.travel.travelplanner.type.LatLng;

import java.util.Collections;
import java.util.List;

public class GasStationServiceImpl implements GasStationService {

    private GoogleMapsApiClient apiClient;

    public List<GasStation> findCheapestGasStation(LatLng departure, LatLng arrival){


        return Collections.emptyList();
    }
}
