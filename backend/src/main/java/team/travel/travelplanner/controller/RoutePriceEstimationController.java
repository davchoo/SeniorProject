package team.travel.travelplanner.controller;

import com.google.maps.model.LatLng;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.entity.Reviews;
import team.travel.travelplanner.repository.GasStationRepository;
import team.travel.travelplanner.repository.ReviewsRepository;
import team.travel.travelplanner.service.GasStationService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/route")
public class RoutePriceEstimationController {

    private final GasStationService gasStationService;

    private final GasStationRepository repository;

    private final ReviewsRepository reviewsRepository;

    public RoutePriceEstimationController(GasStationService gasStationService,
                                          GasStationRepository gasStationRepository,
                                          ReviewsRepository reviewsRepository) {
        this.gasStationService = gasStationService;
        this.repository = gasStationRepository;
        this.reviewsRepository = reviewsRepository;
    }

    @GetMapping("/gas")
    public ArrayList<GasStation> getGasStationsAlongRoute(
            @RequestParam("departureLat") double departureLat,
            @RequestParam("departureLng") double departureLng,
            @RequestParam("arrivalLat") double arrivalLat,
            @RequestParam("arrivalLng") double arrivalLng,
            @RequestParam("travelersMeterCapacity") double travelersMeterCapacity,
            @RequestParam("type") String type
    ) {
        LatLng departure = new LatLng(departureLat, departureLng);
        LatLng arrival = new LatLng(arrivalLat, arrivalLng);

        List<GasStation> gasStationList = gasStationService.getGasStationsAlongRoute(departure, arrival, travelersMeterCapacity, type);
        for(GasStation gasStation : gasStationList){
            repository.save(gasStation);
            for(Reviews review : gasStation.getReviews()){
                Reviews reviewToSave = review;
                reviewToSave.setGasStation(gasStation);
                reviewsRepository.save(reviewToSave);
            }
        }
        return (ArrayList<GasStation>) gasStationList;
    }

}
