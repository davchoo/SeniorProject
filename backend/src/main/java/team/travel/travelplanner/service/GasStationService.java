package team.travel.travelplanner.service;

import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.GasStationModel;

import java.io.IOException;
import java.util.List;


public interface GasStationService {
   List<GasStationModel> getGasStationsAlongRoute(LineString route,
                                                  double travelersMeterCapacity,
                                                  String type) throws IOException;
}
