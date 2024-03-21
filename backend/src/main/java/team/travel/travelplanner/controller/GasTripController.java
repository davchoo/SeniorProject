package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.model.GasRequestModel;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.model.GasTripModel;
import team.travel.travelplanner.repository.GasTripRepository;
import team.travel.travelplanner.service.GasStationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/trip")
public class GasTripController {

    private final GasStationService gasStationService;

    private final GasTripRepository gasTripRepository;

    private final GeometryFactory geometryFactory;

    public GasTripController(GasStationService gasStationService,
                             GasTripRepository gasTripRepository) {
        this.gasStationService = gasStationService;
        this.gasTripRepository = gasTripRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }
    @PostMapping("/gas")
    public GasTripModel getGasTrip(@RequestBody GasRequestModel gasRequestModel) throws IOException {
        LineString lineString = gasRequestModel.geometry(geometryFactory);
        double travelersMeterCapacity = calculateMetersFromGallons(gasRequestModel.tankSizeInGallons(), gasRequestModel.milesPerGallon());

        List<GasStationModel> gasStationList = gasStationService.getGasStationsAlongRoute(lineString, travelersMeterCapacity,
                gasRequestModel.type());

        String origin = gasRequestModel.startAddress();
        String destination = gasRequestModel.endAddress();
        GasTrip gasTrip = new GasTrip(origin, destination, lineString, gasStationList, gasRequestModel.type(), gasRequestModel.tankSizeInGallons(), gasRequestModel.milesPerGallon(), travelersMeterCapacity);
        gasTrip = gasTripRepository.save(gasTrip);
        return GasTripModel.fromEntity(gasTrip);
    }

    private double calculateMetersFromGallons(double tankSizeInGallons, double milesPerGallon){
        return tankSizeInGallons*milesPerGallon*1000;
    }


}
