package team.travel.travelplanner.service.impl;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
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
    public List<GasStationModel> getGasStationsAlongRoute(LineString route, double rangeMeters, String type) throws IOException {
        List<GasStationModel> stops = new ArrayList<>();
        List<Coordinate> stopsAlongRoute = findNeededStops(route, rangeMeters);


        for (Coordinate coordinate : stopsAlongRoute) {
            LatLng location = new LatLng(coordinate.getY(), coordinate.getX());

            PlacesSearchResponse response = placesService.findPlaces(location, "gas_station", 50000);
            List<GoogleGasStation> gasStations = findGasStationsWithTypeAndPrice(response, type);

            GasStationModel lowestPriceGasStation = GasStationModel.from(findLowestPriceGasStation(gasStations, type));
            stops.add(lowestPriceGasStation);
        }

        return stops;
    }

    private List<GoogleGasStation> findGasStationsWithTypeAndPrice(PlacesSearchResponse response, String type) throws IOException {
        List<GoogleGasStation> gasStations = new ArrayList<>();
        int range = 5;
        boolean hasPrice = false;

        for (int i = 0; i < response.results.length; i++) {
            PlacesSearchResult place = response.results[i];
            if (i < range) {
                GoogleGasStation station = gasService.getGasStation(place.placeId);
                if (station != null && stationHasFuelPrice(station, type)) {
                    gasStations.add(station);
                    hasPrice = true;
                }
            } else {
                if (!hasPrice) {
                    GoogleGasStation station = gasService.getGasStation(place.placeId);
                    if (station != null && stationHasFuelPrice(station, type)) {
                        gasStations.add(station);
                        break;
                    }
                }
            }
        }
        return gasStations;
    }

    private boolean stationHasFuelPrice(GoogleGasStation station, String type) {
        if(station.getFuelPrices()!=null){
            for (GoogleGasStation.FuelPrice fuelPrice : station.getFuelPrices()) {
                if (fuelPrice.type().equalsIgnoreCase(type) && fuelPrice.price() != null) {
                    return true;
                }
            }
        }
        return false;
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
