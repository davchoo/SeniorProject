package team.travel.travelplanner.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.GasStationModel;

import java.util.List;

@Entity
public class GasTrip extends Trip{
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<GasStationModel> gasStations;

    private String fuelType;

    private double totalTripGasPrice;

    private double travelersMeterCapacity;

    private double tankSizeInGallons;

    private double milesPerGallon;

    public GasTrip(String origin, String destination, LineString lineString,
                   List<GasStationModel> gasStationList, String fuelType,
                   double tankSizeInGallons, double milesPerGallon, double travelersMeterCapacity, User user) {
        super(origin, destination, lineString, user);
        this.gasStations = gasStationList;
        this.fuelType = fuelType;
        this.tankSizeInGallons = tankSizeInGallons;
        this.milesPerGallon = milesPerGallon;
        this.travelersMeterCapacity = travelersMeterCapacity;
        this.totalTripGasPrice = calculateTotalGasTripCost();
    }

    public GasTrip() {

    }

    private double calculateTotalGasTripCost() {
        double total = 0;
        for (GasStationModel gasStation : gasStations) {
            double price = gasStation.prices().getOrDefault(fuelType, 0.0);
            total += price;
        }
        return total;
    }

    private double calculateTravelersMeterCapacity(){
        return tankSizeInGallons * milesPerGallon * 1000;
    }

    private double calculateQuarterTank(){
        return tankSizeInGallons - tankSizeInGallons*.25;
    }


    public List<GasStationModel> getGasStations() {
        return gasStations;
    }

    public void setGasStations(List<GasStationModel> gasStationList) {
        this.gasStations = gasStationList;
    }

    public String getFuelType(){
        return fuelType;
    }

    public void setFuelType(String fuelType){
        this.fuelType = fuelType;
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

    public double getTankSizeInGallons(){
        return tankSizeInGallons;
    }

    public void setTankSizeInGallons(double tankSizeInGallons){
        this.tankSizeInGallons = tankSizeInGallons;
    }

    public double getMilesPerGallon(){
        return milesPerGallon;
    }

    public void setMilesPerGallon(double milesPerGallon){
        this.milesPerGallon = milesPerGallon;
    }
}
