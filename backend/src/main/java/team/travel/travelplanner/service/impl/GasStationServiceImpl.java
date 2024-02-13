package team.travel.travelplanner.service.impl;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.model.FuelOptions;
import team.travel.travelplanner.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GasStationServiceImpl implements GasStationService {

    @Autowired
    private GoogleMapsApiFuelPriceService apiFuelClient;

    @Autowired
    private GoogleMapsApiDirectionsService apiDirectionsClient;

    @Autowired
    GoogleMapsApiDistanceService apiDistanceClient;

    @Autowired
    GoogleMapsApiPlacesClientService apiPlacesClient;

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
    public Map<String, FuelOptions> getGasStationsAlongRoute(LatLng departure, LatLng arrival, double travelersMeterCapacity, String type) throws IOException, InterruptedException {
        Map<String, FuelOptions> fuelOptionsMap = new HashMap<>();
        List<LatLng> stopsAlongRoute = findNeededStops(departure, arrival, travelersMeterCapacity);
        System.out.println("Needed stops:"+ stopsAlongRoute.size());

        for (LatLng location : stopsAlongRoute) {
            PlacesSearchResponse response = apiPlacesClient.findPlaces(location, "gas_station", 7500);
            System.out.println(response.results.length);
            Map<String, FuelOptions> placesPerLocation = new HashMap<>();

            for (PlacesSearchResult place : response.results) {
                String placeId = place.placeId;
                FuelOptions fuelOptionForPlace = apiFuelClient.getFuelPrices(placeId);
                if(fuelOptionForPlace.getFuelOptions() !=  null){
                    if(fuelOptionForPlace.getFuelOptions().getFuelPrices() != null) {
                        placesPerLocation.put(placeId, fuelOptionForPlace);
                    }
                }
            }

            // Find the lowest fuel prices and corresponding place ID for each location
            System.out.println("Completed step");
            if(!placesPerLocation.isEmpty()) {
                Map.Entry<String, FuelOptions> entryWithLowestPrices = findEntryWithLowestPrices(placesPerLocation, type);
                fuelOptionsMap.put(entryWithLowestPrices.getKey(), entryWithLowestPrices.getValue());
            }
        }

        System.out.println("Completed final");
        return fuelOptionsMap;
    }

    /**
     * Finds the gas station with the lowest fuel prices among the given set of gas stations.
     *
     * @param placesPerLocation A map containing place IDs of gas stations and their fuel options.
     * @return A map entry containing the place ID of the gas station with the lowest fuel prices and its fuel options.
     */
    private Map.Entry<String, FuelOptions> findEntryWithLowestPrices(Map<String, FuelOptions> placesPerLocation, String type) {
        Map.Entry<String, FuelOptions> entryWithLowestPrices = null;
        double lowestPrice = Double.MAX_VALUE;

        for (Map.Entry<String, FuelOptions> entry : placesPerLocation.entrySet()) {
            FuelOptions fuelOptions = entry.getValue();
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
        for (FuelOptions.FuelPrice price : fuelOptions.getFuelOptions().getFuelPrices()) {
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
                    double distance = apiDistanceClient.haversine(lastLocation, point);
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
            return apiDirectionsClient.getDirections(departureLatLng, arrivalLatLng);
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
