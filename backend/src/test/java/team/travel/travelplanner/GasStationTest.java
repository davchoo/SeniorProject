package team.travel.travelplanner;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.service.GoogleMapsApiDirectionsService;
import team.travel.travelplanner.service.impl.GasStationServiceImpl;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GasStationTest {


    @Autowired
    private GoogleMapsApiDirectionsService directionsService;


   @Autowired
    private GasStationServiceImpl gasStationService;

    @Test
    public void testGetGasStationsAlongRoute() throws Exception {
        LatLng departure = new LatLng(39.71899847525047, -75.09783609674565);
        LatLng arrival = new LatLng(29.138315, -80.995613);
        DirectionsResult directionsResult = directionsService.getDirections(departure, arrival);

        List<GasStation> gasStations = gasStationService.getGasStationsAlongRoute(directionsResult, 482803, "type");
        assertEquals(4, gasStations.size());
    }
}
