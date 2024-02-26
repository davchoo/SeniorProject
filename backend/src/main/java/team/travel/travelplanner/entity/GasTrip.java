package team.travel.travelplanner.entity;

import com.google.maps.model.DirectionsResult;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class GasTrip extends Trip{

    @ManyToMany
    private List<GasStation> gasStationList;

    public GasTrip(String origin, String destination, DirectionsResult directionsResult, List<GasStation> gasStationList) {
        super(origin, destination, directionsResult);
        this.gasStationList = gasStationList;
    }

    public GasTrip() {

    }

    public List<GasStation> getGasStationList() {
        return gasStationList;
    }

    public void setGasStationList(List<GasStation> gasStationList) {
        this.gasStationList = gasStationList;
    }
}
