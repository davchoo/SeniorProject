package team.travel.travelplanner.service.impl;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.repository.FuelOptionsRepository;
import team.travel.travelplanner.repository.GasStationRepository;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.entity.FuelOptions;
import team.travel.travelplanner.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class GasStationServiceImpl implements GasStationService {

    private final GoogleMapsApiFuelPriceService gasService;

    private final GoogleMapsApiDirectionsService directionsService;

    private final GoogleMapsApiDistanceService distanceService;

    private final GoogleMapsApiPlacesClientService placesService;

    @Autowired
    public GasStationServiceImpl(GoogleMapsApiFuelPriceService apiGasClient,
                                 GoogleMapsApiDirectionsService apiDirectionsClient,
                                 GoogleMapsApiDistanceService apiDistanceClient,
                                 GoogleMapsApiPlacesClientService apiPlacesClient){
        this.gasService = apiGasClient;
        this.directionsService = apiDirectionsClient;
        this.distanceService = apiDistanceClient;
        this.placesService = apiPlacesClient;
    }

    /**
     * Retrieves gas stations along a route and their fuel options.
     *
     * @param departure              The departure location.
     * @param arrival                The arrival location.
     * @param travelersMeterCapacity The fuel capacity of the traveler in meters.
     * @return A map containing the place IDs of gas stations along the route and their corresponding fuel options.
     * @throws IOException          If there's an error communicating with the Google Maps API.
     * @throws InterruptedException If the thread is interrupted while waiting for the API response.
     */
    @Override
    public List<GasStation> getGasStationsAlongRoute(LatLng departure, LatLng arrival, double travelersMeterCapacity, String type) {
        List<GasStation> gasOptionsList = new ArrayList<>();
        List<LatLng> stopsAlongRoute = findNeededStops(departure, arrival, travelersMeterCapacity);
        System.out.println("Needed stops:"+ stopsAlongRoute.size());

        List<CompletableFuture<Map<String, GasStation>>> gasStationFutures = stopsAlongRoute.parallelStream()
                .map(location -> CompletableFuture.supplyAsync(() -> {
                    PlacesSearchResponse response = placesService.findPlaces(location, "gas_station", 6500);
                    System.out.println(response.results.length);
                    Map<String, GasStation> placesPerLocation = new HashMap<>();
                    int range = Math.min(response.results.length, 5);

                    for (int i = 0; i < range; i++) {
                        PlacesSearchResult place = response.results[i];
                        String placeId = place.placeId;
                        try {
                            GasStation gasStationPerPlace = gasService.getGasStations(placeId);
                            if (gasStationPerPlace != null && gasStationPerPlace.getFuelOptions() != null
                            && gasStationPerPlace.getFuelOptions().getFuelPrices() != null
                            ) {
                                placesPerLocation.put(placeId, gasStationPerPlace);
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace(); // Handle or log the exception
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return placesPerLocation;
                }))
                .toList();

        // Combine all completed futures into a single map
        CompletableFuture.allOf(gasStationFutures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    for (CompletableFuture<Map<String, GasStation>> future : gasStationFutures) {
                        try {
                            Map<String, GasStation> placesPerLocation = future.join();
                            if (!placesPerLocation.isEmpty()) {
                                Map.Entry<String, GasStation> entryWithLowestPrices = findEntryWithLowestPrices(placesPerLocation, type);

                                GasStation station = entryWithLowestPrices.getValue();
                                gasOptionsList.add(station);
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Handle or log the exception
                        }
                    }
                })
                .join(); // Wait for all futures to complete

        System.out.println("Completed final");
        return gasOptionsList;
    }

    /**
     * Finds the gas station with the lowest fuel prices among the given set of gas stations.
     *
     * @param placesPerLocation A map containing place IDs of gas stations and their fuel options.
     * @return A map entry containing the place ID of the gas station with the lowest fuel prices and its fuel options.
     */
    private Map.Entry<String, GasStation> findEntryWithLowestPrices(Map<String, GasStation> placesPerLocation, String type) {
        Map.Entry<String, GasStation> entryWithLowestPrices = null;
        double lowestPrice = Double.MAX_VALUE;

        for (Map.Entry<String, GasStation> entry : placesPerLocation.entrySet()) {
            FuelOptions fuelOptions = entry.getValue().getFuelOptions();
            double totalPrice = getTotalPrice(fuelOptions, type);

            if (totalPrice < lowestPrice) {
                lowestPrice = totalPrice;
                entryWithLowestPrices = entry;
            }
        }
        return entryWithLowestPrices;
    }

    /**
     * Calculates the total price of fuel options at a gas station.
     *
     * @param fuelOptions The fuel options available at a gas station.
     * @return The total price of fuel options at the gas station.
     */
    private double getTotalPrice(FuelOptions fuelOptions, String type) {
        double totalPrice = 0;
        for (FuelOptions.FuelPrice price : fuelOptions.getFuelPrices()) {
            if(price.getType().equals(type)){
                totalPrice += price.getPrice().getDollarPrice();
                break;
            }
        }
        return totalPrice;
    }


    /**
     * Finds the necessary stops along a route based on the travelers' fuel capacity.
     *
     * @param departure              The departure location.
     * @param arrival                The arrival location.
     * @param travelersMeterCapacity The fuel capacity of the traveler in meters.
     * @return A list of LatLng coordinates representing the stops along the route where the traveler needs to refuel.
     */
    private List<LatLng> findNeededStops(LatLng departure, LatLng arrival, double travelersMeterCapacity)  {
        List<LatLng> stops = new ArrayList<>();
        double quarterTankSize = travelersMeterCapacity - (travelersMeterCapacity * .25);
        long metersDrivenAfterLastStop = 0;

        String departureLatLng = departure.toString();
        String arrivalLatLng = arrival.toString();

        DirectionsResult directionsResult = getDirections(departureLatLng, arrivalLatLng);
        DirectionsRoute route = getRoute(directionsResult);
        DirectionsLeg leg = getLeg(route);
        DirectionsStep[] steps = leg.steps;

        for(DirectionsStep step : steps){
            double totalStepMetersToBeDriven = metersDrivenAfterLastStop+step.distance.inMeters;
            if(totalStepMetersToBeDriven >= quarterTankSize){
                //How many times do we need to stop along this step?
                int neededStopsPerStep = (int)(totalStepMetersToBeDriven / quarterTankSize);

                int numStops = 0;
                double drivenMetersAlongPoints = 0;
                double drivenMetersAlongStep = 0;
                List<LatLng> pointsAlongStep = step.polyline.decodePath();
                LatLng lastLocation = step.startLocation;
                boolean stopped = false;

                for(LatLng point : pointsAlongStep){
                    double distance = distanceService.haversine(lastLocation, point);
                    double totalDrivenDistance = drivenMetersAlongPoints+metersDrivenAfterLastStop+distance;
                    if(totalDrivenDistance >= quarterTankSize){
                        stops.add(lastLocation); // One of the locations to find a gas station around
                        if(++numStops==neededStopsPerStep){
                            // Calculates the remaining miles that someone will drive through that step after they stopped.
                            metersDrivenAfterLastStop = (long) (step.distance.inMeters - drivenMetersAlongStep);
                            break;
                        }
                        // I need to stop again along the step so now reset
                        stopped = true;
                        metersDrivenAfterLastStop = (long) distance;
                        drivenMetersAlongPoints = 0;
                    }
                    if(!stopped){
                        drivenMetersAlongPoints += distance;
                    }
                    else{
                        stopped = false;
                    }
                    drivenMetersAlongStep += distance;
                    lastLocation = point;
                }
            }
            else{
                metersDrivenAfterLastStop += step.distance.inMeters;
            }
        }
        return stops;
    }

    private DirectionsResult getDirections(String departureLatLng, String arrivalLatLng) {
        try {
            return directionsService.getDirections(departureLatLng, arrivalLatLng);
        } catch (IOException | InterruptedException | ApiException e) {
            // Handle exceptions?
            e.printStackTrace();
            return null;
        }
    }

    private DirectionsRoute getRoute(DirectionsResult directionsResult) {
        if (directionsResult != null && directionsResult.routes.length > 0) {
            return directionsResult.routes[0];
        } else {
            throw new IllegalArgumentException("No route found.");
        }
    }

    private DirectionsLeg getLeg(DirectionsRoute route) {
        return route.legs[0];
    }
}
