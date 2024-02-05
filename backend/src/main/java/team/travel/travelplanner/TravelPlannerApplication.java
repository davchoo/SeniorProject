package team.travel.travelplanner;

import com.google.maps.errors.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClientService;

import java.io.IOException;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) throws IOException, InterruptedException, ApiException {
        ApplicationContext context = SpringApplication.run(TravelPlannerApplication.class, args);

        GoogleMapsApiClientService client = context.getBean(GoogleMapsApiClientService.class);
        client.test();
    }
}
