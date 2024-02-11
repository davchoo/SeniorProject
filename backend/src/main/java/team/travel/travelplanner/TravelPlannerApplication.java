package team.travel.travelplanner;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import team.travel.travelplanner.service.impl.GasStationServiceImpl;

import java.io.IOException;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) throws IOException, InterruptedException, ApiException {
        LatLng departure = new LatLng(39.71899847525047, -75.09783609674565);
        LatLng arrival = new LatLng(29.138315, -80.995613);
        ApplicationContext context = SpringApplication.run(TravelPlannerApplication.class, args);

        GasStationServiceImpl gasStationService = context.getBean(GasStationServiceImpl.class);
        gasStationService.findNeededStops(departure, arrival);
    }
}
