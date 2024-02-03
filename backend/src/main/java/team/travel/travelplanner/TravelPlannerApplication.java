package team.travel.travelplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClient;
import team.travel.travelplanner.type.LatLng;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelPlannerApplication.class, args);
        GoogleMapsApiClient client = new GoogleMapsApiClient();

        LatLng departure = new LatLng(39.71899847525047, -75.09783609674565);
        LatLng arrival = new LatLng(39.710785873747376, -75.119957824938);

        //client.getDirections(departure, arrival);
        client.getPlacesNearby(null, arrival, 2000, null);
    }

}
