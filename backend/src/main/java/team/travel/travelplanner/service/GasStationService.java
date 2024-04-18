package team.travel.travelplanner.service;

import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.model.type.FuelType;

import java.io.IOException;
import java.util.List;


public interface GasStationService {
   List<GasStationModel> getGasStationsAlongRoute(LineString route,
                                                  double travelersMeterCapacity,
                                                  FuelType type) throws IOException;
}
