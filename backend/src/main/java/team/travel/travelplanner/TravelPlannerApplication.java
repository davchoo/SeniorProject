package team.travel.travelplanner;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.service.impl.GasStationServiceImpl;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClientServiceImpl;

import java.io.IOException;
import java.util.Map;

/**
 * @Author Lukas DeLoach
 */
@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) throws IOException, InterruptedException, ApiException {
        LatLng departure = new LatLng(39.71899847525047, -75.09783609674565);
        LatLng arrival = new LatLng(29.138315, -80.995613);
        ApplicationContext context = SpringApplication.run(TravelPlannerApplication.class, args);

        GoogleMapsApiClientServiceImpl service = context.getBean(GoogleMapsApiClientServiceImpl.class);

        GasStationServiceImpl gasStationService = context.getBean(GasStationServiceImpl.class);
        long startTime = System.nanoTime();
        Map<String, GasStation> map = gasStationService.getGasStationsAlongRoute(departure, arrival, 482803, "REGULAR_UNLEADED");
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        double durationInMilli = (double) durationInNano / 1_000_000; // converting nanoseconds to milliseconds

        System.out.println("Time taken: " + durationInMilli + " milliseconds");
        map.forEach((key, value) -> System.out.println(value.toString() + "\n"));
    }
}
