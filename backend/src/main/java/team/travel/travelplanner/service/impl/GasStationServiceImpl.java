package team.travel.travelplanner.service.impl;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.service.GoogleMapsApiDirectionsService;
import team.travel.travelplanner.service.GoogleMapsApiDistanceService;
import team.travel.travelplanner.service.GoogleMapsApiFuelPriceService;
import team.travel.travelplanner.service.GasStationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GasStationServiceImpl implements GasStationService {

    @Autowired
    private GoogleMapsApiFuelPriceService apiFuelClient;

    @Autowired
    private GoogleMapsApiDirectionsService apiDirectionsClient;

    @Autowired
    GoogleMapsApiDistanceService apiDistanceClient;

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
    public List<LatLng> findNeededStops(LatLng departure, LatLng arrival) throws IOException, InterruptedException, ApiException {
        List<LatLng> stops = new ArrayList<>();
        double travelersMeterCapacity = 200;
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
            if(totalStepMetersToBeDriven >= travelersMeterCapacity){
                //How many times do we need to stop along this step?
                int neededStopsPerStep = (int)(totalStepMetersToBeDriven / travelersMeterCapacity);

                int numStops = 0;
                double drivenMetersAlongPoints = 0;
                double drivenMetersAlongStep = metersDrivenAfterLastStop;
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
                        metersDrivenAfterLastStop = (long) (totalDrivenDistance - drivenMetersAlongPoints);
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
