package team.travel.travelplanner.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.CarModel;
import team.travel.travelplanner.model.GasStationModel;

import java.util.List;

@Entity
public class GasTrip extends Trip{
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<GasStationModel> gasStations;

    private double totalTripGasPrice;

    private double travelersMeterCapacity;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private CarModel carModel;

    private double distance;

    private String duration;

    public GasTrip(String origin, String destination, LineString lineString,
                   List<GasStationModel> gasStationList, double travelersMeterCapacity,
                   User user, CarModel carModel, double distance, String duration) {
        super(origin, destination, lineString, user);
        this.gasStations = gasStationList;
        this.travelersMeterCapacity = travelersMeterCapacity;
        this.carModel = carModel;
        this.totalTripGasPrice = calculateTotalGasTripCost();
        this.distance = distance;
        this.duration = duration;
    }

    public GasTrip() {

    }

    private double calculateTotalGasTripCost() {
        double total = 0;
        if(gasStations!=null && !gasStations.isEmpty()){
            for (GasStationModel gasStation : gasStations) {
                double price = gasStation.prices().getOrDefault(carModel.fuelType().toString(), 0.0);
                total += price;
            }
        }
        return total;
    }

    private double calculateTravelersMeterCapacity(){
        return carModel.tankSizeInGallons() * carModel.milesPerGallon() * 1000;
    }

    private double calculateQuarterTank(){
        return carModel.tankSizeInGallons()-carModel.tankSizeInGallons()*.25;
    }


    public List<GasStationModel> getGasStations() {
        return gasStations;
    }

    public void setGasStations(List<GasStationModel> gasStationList) {
        this.gasStations = gasStationList;
    }

    public double getTotalTripGasPrice(){
        return totalTripGasPrice;
    }

    public void setTotalTripGasPrice(double totalTripGasPrice){
        this.totalTripGasPrice = totalTripGasPrice;
    }

    public double getTravelersMeterCapacity(){
        return travelersMeterCapacity;
    }

    public void setTravelersMeterCapacity(double travelersMeterCapacity){
        this.travelersMeterCapacity = travelersMeterCapacity;
    }

    public CarModel getCarModel(){
        return carModel;
    }

    public void setCarModel(CarModel carModel){
        this.carModel = carModel;
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public String getDuration(){
        return duration;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }
}
