package team.travel.travelplanner;

import com.google.maps.model.LatLng;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.CarModel;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.model.type.FuelType;
import team.travel.travelplanner.repository.GasTripRepository;
import team.travel.travelplanner.repository.UserRepository;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
public class SaveTripTests {

    @Autowired
    GasTripRepository gasTripRepository;

    @Autowired
    UserRepository userRepository;

    User testUser;

    List<GasStationModel> gasStationModels = new ArrayList<>();

    @BeforeEach
    void setup() throws IOException {
        createUser();
        gasStationModels.add(createGasStation("Sunuco"));
        gasStationModels.add(createGasStation("Wawa"));
        createGasTrip(gasStationModels, FuelType.REGULAR_UNLEADED);
        createGasTrip(gasStationModels, FuelType.DIESEL);
    }

    public void createUser(){
        if(testUser==null) {
            testUser = new User();
            testUser.setFirstName("test");
            testUser.setLastName("user");
            testUser.setUsername("testuser");
            testUser.setPassword("testpassword");
            userRepository.save(testUser);
        }
    }

    public GasStationModel createGasStation(String name){
        HashMap<String, Double> gasPrices = new HashMap<>();
        gasPrices.put("REGULAR_UNLEADED", 2.5);
        gasPrices.put("DIESEL", 5.0);
        LatLng latLng = new LatLng(5,5);
        return new GasStationModel("1", name, gasPrices, latLng, "Some Location", 2.5, null, null);
    }

    public void createGasTrip(List<GasStationModel> gasStationModels, FuelType fuelType) throws IOException {
        Resource polylineResource = new ClassPathResource("gas_station_polyline.txt");
        String polyline = polylineResource.getContentAsString(StandardCharsets.US_ASCII);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        LineString lineString = geometryFactory.createLineString(EncodedPolylineUtils.decodePolyline(polyline));
        CarModel carModel = new CarModel(2024, "Honda", "Pilot", fuelType, 22.0, 16.0);
        User user = userRepository.findByUsername("testuser");
        GasTrip gasTrip = new GasTrip("origin", "destination", lineString, gasStationModels, 500, user, carModel, 50000, "21 minutes");
        gasTripRepository.save(gasTrip);
    }


    @Test
    void testSaveGasTrip() {
        List<GasTrip> savedTrips = gasTripRepository.findAll();
        assertEquals(2, savedTrips.size());
    }

    @Test
    void testGasTripRetrieval(){
        User user = userRepository.findByUsername("testuser");
        List<GasTrip> gasTripList = gasTripRepository.findAllByUser(user);
        assertEquals(2, gasTripList.size());
    }

    @Test
    void testGasTripFields(){
        User user = userRepository.findByUsername("testuser");
        List<GasTrip> gasTripList = gasTripRepository.findAllByUser(user);
        GasTrip retrievedGasTrip = gasTripList.getFirst();
        assertEquals(retrievedGasTrip.getOrigin(), "origin");
        assertEquals(retrievedGasTrip.getDestination(), "destination");
        assertEquals(retrievedGasTrip.getCarModel().make(), "Honda");
    }

    @Test
    void testCalculateTotalTripPrice(){
        User user = userRepository.findByUsername("testuser");
        List<GasTrip> gasTripList = gasTripRepository.findAllByUser(user);
        assertEquals(5.00, gasTripList.get(0).getTotalTripGasPrice());
        assertEquals(10.00, gasTripList.get(1).getTotalTripGasPrice());
    }

}
