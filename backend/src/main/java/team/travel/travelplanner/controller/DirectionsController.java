package team.travel.travelplanner.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.service.GoogleMapsApiDirectionsService;

import java.io.IOException;

@RestController
@RequestMapping("api/directions")
public class DirectionsController {

    private final GoogleMapsApiDirectionsService directionsService;

    public DirectionsController(GoogleMapsApiDirectionsService directionsService) {
        this.directionsService = directionsService;
    }

    @GetMapping()
    public DirectionsResult getDirections(@RequestParam double originLat,
                                          @RequestParam double originLng,
                                          @RequestParam double arrivalLat,
                                          @RequestParam double arrivalLng) throws IOException, InterruptedException, ApiException {
        LatLng originLocation = new LatLng(originLat, originLng);
        LatLng arrivalLocation = new LatLng(arrivalLat, arrivalLng);
        return directionsService.getDirections(originLocation, arrivalLocation);
    }
}
