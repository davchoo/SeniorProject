package team.travel.travelplanner;

import com.google.maps.errors.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import team.travel.travelplanner.service.impl.GasStationServiceImpl;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) throws IOException, InterruptedException, ApiException {
        LatLng departure = new LatLng(39.71899847525047, -75.09783609674565);
        LatLng arrival = new LatLng(39.710785873747376, -75.119957824938);
        ApplicationContext context = SpringApplication.run(TravelPlannerApplication.class, args);

        GasStationServiceImpl gasStationService = context.getBean(GasStationServiceImpl.class);
        gasStationService.findCheapestGasStation(departure, arrival);
    }
}
