package team.travel.travelplanner.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import org.springframework.web.bind.annotation.*;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.entity.Reviews;
import team.travel.travelplanner.excpetion.NoRouteFoundException;
import team.travel.travelplanner.model.GasRequestModel;
import team.travel.travelplanner.model.GasTripModel;
import team.travel.travelplanner.repository.GasStationRepository;
import team.travel.travelplanner.repository.GasTripRepository;
import team.travel.travelplanner.repository.ReviewsRepository;
import team.travel.travelplanner.service.GasStationService;
import team.travel.travelplanner.service.GoogleMapsApiDirectionsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/trip")
public class GasTripController {

    private final GasStationService gasStationService;

    private final GoogleMapsApiDirectionsService directionsService;

    private final GasStationRepository repository;

    private final GasTripRepository gasTripRepository;

    private final ReviewsRepository reviewsRepository;

    public GasTripController(GasStationService gasStationService,
                             GasStationRepository gasStationRepository,
                             ReviewsRepository reviewsRepository,
                             GoogleMapsApiDirectionsService directionsService,
                             GasTripRepository gasTripRepository) {
        this.gasStationService = gasStationService;
        this.repository = gasStationRepository;
        this.reviewsRepository = reviewsRepository;
        this.directionsService = directionsService;
        this.gasTripRepository = gasTripRepository;
    }
    @PostMapping("/gas")
    public GasTripModel getGasTrip(@RequestBody GasRequestModel gasRequestModel) throws IOException, InterruptedException, ApiException {
        try {
            DirectionsResult directionResult = directionsService.getDirections(new LatLng(gasRequestModel.originLat(), gasRequestModel.originLng()),
                    new LatLng(gasRequestModel.destinationLat(), gasRequestModel.destinationLng()));
            List<GasStation> gasStationList = gasStationService.getGasStationsAlongRoute(directionResult, gasRequestModel.travelersMeterCapacity(),
                    gasRequestModel.type());
            for (GasStation gasStation : gasStationList) {
                repository.save(gasStation);
                for (Reviews review : gasStation.getReviews()) {
                    Reviews reviewToSave = review;
                    reviewToSave.setGasStation(gasStation);
                    reviewsRepository.save(reviewToSave);
                }
            }
            String origin = directionResult.routes[0].legs[0].startAddress;
            String destination = directionResult.routes[0].legs[0].endAddress;
            GasTrip gasTrip = new GasTrip(origin, destination, directionResult, gasStationList);
            gasTripRepository.save(gasTrip);
            return GasTripModel.fromEntity(gasTrip);
        }
        catch (ApiException apiException){
            throw new NoRouteFoundException();
        }

    }
}
