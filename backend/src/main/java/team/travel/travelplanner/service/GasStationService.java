package team.travel.travelplanner.service;

import com.google.maps.model.LatLng;
import team.travel.travelplanner.entity.GasStation;
import java.util.List;


public interface GasStationService {
   List<GasStation> getGasStationsAlongRoute(LatLng departure, LatLng arrival,
                                             double travelersMeterCapacity,
                                             String type);
}
