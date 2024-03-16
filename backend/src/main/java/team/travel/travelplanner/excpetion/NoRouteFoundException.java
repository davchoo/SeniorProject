package team.travel.travelplanner.excpetion;

import com.google.maps.errors.ApiException;

public class NoRouteFoundException extends ApiException {
    public NoRouteFoundException(){
        super("There is no route between the specified origin and destination.");
    }

}
