package team.travel.travelplanner.entity;

import com.google.maps.model.DirectionsResult;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
public class GasTrip extends Trip{

    @ManyToMany
    private List<GasStation> gasStationList;

    private String fuelType;

    private BigDecimal totalTripGasPrice;

    private double travelersMeterCapacity;

    private double tankSizeInGallons;

    private double milesPerGallon;

    public GasTrip(String origin, String destination, DirectionsResult directionsResult,
                   List<GasStation> gasStationList, String fuelType,
                   double tankSizeInGallons, double milesPerGallon, double travelersMeterCapacity) {
        super(origin, destination, directionsResult);
        this.gasStationList = gasStationList;
        this.fuelType = fuelType;
        this.tankSizeInGallons = tankSizeInGallons;
        this.milesPerGallon = milesPerGallon;
        this.travelersMeterCapacity = travelersMeterCapacity;
        this.totalTripGasPrice = calculateTotalGasTripCost();
    }

    public GasTrip() {

    }

    private BigDecimal calculateTotalGasTripCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (GasStation gasStation : gasStationList) {
            for (FuelOptions.FuelPrice fuelPrice : gasStation.getFuelOptions().getFuelPrices()) {
                if (fuelPrice.getType().equalsIgnoreCase(fuelType)) {
                    total = total.add(BigDecimal.valueOf(fuelPrice.getPrice().getDollarPrice()*calculateQuarterTank()));
                }
            }
        }
        total = total.setScale(2, RoundingMode.HALF_UP);
        return total;
    }

    private double calculateTravelersMeterCapacity(){
        return tankSizeInGallons * milesPerGallon * 1000;
    }

    private double calculateQuarterTank(){
        return tankSizeInGallons - tankSizeInGallons*.25;
    }


    public List<GasStation> getGasStationList() {
        return gasStationList;
    }

    public void setGasStationList(List<GasStation> gasStationList) {
        this.gasStationList = gasStationList;
    }

    public String getFuelType(){
        return fuelType;
    }

    public void setFuelType(String fuelType){
        this.fuelType = fuelType;
    }

    public BigDecimal getTotalTripGasPrice(){
        return totalTripGasPrice;
    }

    public void setTotalTripGasPrice(BigDecimal totalTripGasPrice){
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
