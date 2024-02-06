package team.travel.travelplanner.service.impl;

import com.google.maps.DirectionsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.service.GasStationService;
import team.travel.travelplanner.service.GoogleMapsApiClientService;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClientServiceImpl;
import team.travel.travelplanner.type.LatLng;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GasStationServiceImpl implements GasStationService {

    @Autowired
    private GoogleMapsApiClientService apiClient;


    /**
     * Method that will be used to find cheapeast gas stations along route.
     * Will have to figure out how to refractor this an adhere to SRP because many different steps will have to happen
     * Skeleton example that will build on.
     *
     * @param departure
     * @param arrival
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ApiException
     */
    public List<GasStation> findCheapestGasStation(LatLng departure, LatLng arrival) throws IOException, InterruptedException, ApiException {
        // Get latitude and longitude strings from departure and arrival
        String departureLatLng = departure.toString();
        String arrivalLatLng = arrival.toString();

        // Retrieve directions from departure to arrival
        DirectionsResult directionsResult = apiClient.getDirections(departureLatLng, arrivalLatLng);
        apiClient.findPlaces(arrival, "gas_station", 1500);

        return Collections.emptyList();
    }
}
