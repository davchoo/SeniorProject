package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.CarModel;
import team.travel.travelplanner.model.GasRequestModel;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.model.GasTripModel;
import team.travel.travelplanner.repository.GasTripRepository;
import team.travel.travelplanner.repository.UserRepository;
import team.travel.travelplanner.service.GasStationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/trip")
public class GasTripController {

    private final GasStationService gasStationService;

    private final GasTripRepository gasTripRepository;

    private final GeometryFactory geometryFactory;

    private final UserRepository userRepository;

    public GasTripController(GasStationService gasStationService,
                             GasTripRepository gasTripRepository, UserRepository userRepository) {
        this.gasStationService = gasStationService;
        this.gasTripRepository = gasTripRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.userRepository = userRepository;
    }
    @PostMapping("/gas")
    public GasTripModel getGasTrip(@RequestBody GasRequestModel gasRequestModel, Authentication authentication) throws IOException {
        boolean save = authentication!=null;
        LineString lineString = gasRequestModel.geometry(geometryFactory);
        double travelersMeterCapacity = calculateMetersFromGallons(gasRequestModel.tankSizeInGallons(), gasRequestModel.milesPerGallon());

        List<GasStationModel> gasStationList = gasStationService.getGasStationsAlongRoute(lineString, travelersMeterCapacity,
                gasRequestModel.type());

        String origin = gasRequestModel.startAddress();
        String destination = gasRequestModel.endAddress();
        User user = save ? userRepository.findByUsername(authentication.getName()) : null;
        CarModel carModel = new CarModel(gasRequestModel.year(), gasRequestModel.make(), gasRequestModel.model(), gasRequestModel.type(), gasRequestModel.milesPerGallon(), gasRequestModel.tankSizeInGallons());
        GasTrip gasTrip = new GasTrip(origin, destination, lineString, gasStationList, travelersMeterCapacity, user, carModel, gasRequestModel.distance(), gasRequestModel.duration());
        if(save) {
            gasTrip = gasTripRepository.save(gasTrip);
        }
        return GasTripModel.fromEntity(gasTrip);
    }

    @GetMapping("/gas/myTrips")
    public List<GasTripModel> getSavedGasTrips(Authentication authentication){
        User user = userRepository.findByUsername(authentication.getName());
        List<GasTrip> gasTrips = gasTripRepository.findAllByUser(user);
        return gasTrips.stream()
                .map(GasTripModel::fromEntity)
                .toList();
    }

    private double calculateMetersFromGallons(double tankSizeInGallons, double milesPerGallon){
        return tankSizeInGallons*milesPerGallon*1000;
    }

}
