package team.travel.travelplanner.service;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import team.travel.travelplanner.entity.GasStation;
import java.util.List;


public interface GasStationService {
   List<GasStation> getGasStationsAlongRoute(DirectionsResult directionsResult,
                                             double travelersMeterCapacity,
                                             String type);
}
