package team.travel.travelplanner.service.impl;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.model.google.GoogleGasStation;
import team.travel.travelplanner.service.GasStationService;
import team.travel.travelplanner.service.GoogleMapsApiFuelPriceService;
import team.travel.travelplanner.service.GoogleMapsApiPlacesClientService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GasStationServiceImpl implements GasStationService {

    private final GoogleMapsApiFuelPriceService gasService;

    private final GoogleMapsApiPlacesClientService placesService;

    @Autowired
    public GasStationServiceImpl(GoogleMapsApiFuelPriceService apiGasClient,
                                 GoogleMapsApiPlacesClientService apiPlacesClient){
        this.gasService = apiGasClient;
        this.placesService = apiPlacesClient;
    }

    /**
     * Retrieves gas stations along a route and their fuel options.
     * @param rangeMeters The fuel capacity of the traveler in meters.
     * @return A map containing the place IDs of gas stations along the route and their corresponding fuel options.
     * @throws IOException          If there's an error communicating with the Google Maps API.
     */
    @Override
    public List<GasStationModel> getGasStationsAlongRoute(LineString route, double rangeMeters,
                                                          String type) throws IOException {
        List<GasStationModel> stops = new ArrayList<>();
        List<Coordinate> stopsAlongRoute = findNeededStops(route, rangeMeters);
        System.out.println("Needed stops:"+ stopsAlongRoute.size());
        if (stopsAlongRoute.size() > 12) {
            throw new RuntimeException("Too many stops!");
        }

        for (Coordinate coordinate : stopsAlongRoute) {
            LatLng location = new LatLng(coordinate.getY(), coordinate.getX());
            PlacesSearchResponse response;
            int radius = 5000;
            do {
                response = placesService.findPlaces(location, "gas_station", radius);
                radius += 2500;
            } while (response.results.length == 0);
            System.out.println(response.results.length);
            int range = Math.min(response.results.length, 5);

            List<GoogleGasStation> gasStations = new ArrayList<>();
            for (int i = 0; i < range; i++) {
                PlacesSearchResult place = response.results[i];
                GoogleGasStation station = gasService.getGasStation(place.placeId);
                if (station != null && station.getFuelPrices() != null) {
                    gasStations.add(station);
                }
            }
            GoogleGasStation lowestPriceGasStation = findLowestPriceGasStation(gasStations, type);
            if (lowestPriceGasStation != null) {
                stops.add(GasStationModel.from(lowestPriceGasStation));
            } else {
                stops.add(null);
            }
        }

        System.out.println("Completed final");
        return stops;
    }

    /**
     * Finds the gas station with the lowest fuel prices among the given List of GasStations.
     *
     * @param gasStations A map containing place IDs of gas stations and their fuel options.
     * @return A GasStation with the lowest fuel price or null.
     */
    private GoogleGasStation findLowestPriceGasStation(List<GoogleGasStation> gasStations, String type) {
        GoogleGasStation lowestPriceGasStation = null;
        double lowestPrice = Double.MAX_VALUE;

        for (GoogleGasStation gasStation : gasStations) {
            double price = gasStation.getFuelPrices().stream()
                    .filter(fuelPrice -> fuelPrice.type().equals(type))
                    .map(GoogleGasStation.FuelPrice::priceDouble)
                    .findFirst()
                    .orElse(Double.MAX_VALUE); // Gas type not available

            if (price < lowestPrice) {
                lowestPrice = price;
                lowestPriceGasStation = gasStation;
            }
        }
        return lowestPriceGasStation;
    }

    /**
     * Finds the necessary stops along a route based on the travelers' fuel capacity.
     *
     * @param rangeMeters The fuel capacity of the traveler in meters.
     * @return A List of Coordinates representing the stops along the route where the traveler needs to refuel.
     */
    private List<Coordinate> findNeededStops(LineString route, double rangeMeters)  {
        GeodeticCalculator geodeticCalc = new GeodeticCalculator();
        List<Coordinate> stops = new ArrayList<>();
        double quarterTankSize = rangeMeters - (rangeMeters * .25);
        double metersDrivenAfterLastStop = 0;

        for (int i = 0; i < route.getNumPoints() - 1; i++){
            Coordinate lastLocation = route.getCoordinateN(i);
            Coordinate nextLocation = route.getCoordinateN(i + 1);

            geodeticCalc.setStartingGeographicPoint(lastLocation.getX(), lastLocation.getY());
            geodeticCalc.setDestinationGeographicPoint(nextLocation.getX(), nextLocation.getY());
            double distance = geodeticCalc.getOrthodromicDistance();

            metersDrivenAfterLastStop += distance;
            if (metersDrivenAfterLastStop >= quarterTankSize) {
                // We need to stop for gas along this segment
                stops.add(lastLocation);
                metersDrivenAfterLastStop = distance;
            }
        }
        return stops;
    }
}
