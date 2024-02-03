package team.travel.travelplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClient;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelPlannerApplication.class, args);
        GoogleMapsApiClient client = new GoogleMapsApiClient();

        double departureLat = 39.71899847525047;
        double departureLng = -75.09783609674565;
        double arrivalLat = 39.710785873747376;
        double arrivalLng = -75.119957824938;
        client.getDirections(departureLat, departureLng, arrivalLat, arrivalLng);
    }

}
