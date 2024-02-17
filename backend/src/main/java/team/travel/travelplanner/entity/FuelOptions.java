package team.travel.travelplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuelOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    private List<FuelPrice> fuelPrices;

    public List<FuelPrice> getFuelPrices(){
        return fuelPrices;
    }

    public void setFuelPrices(List<FuelPrice> fuelPrices){
        this.fuelPrices = fuelPrices;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        for(FuelPrice price: fuelPrices){
            s.append(price.type).append(": ").append(price.price.getDollarPrice()).append("\n");
        }
        return s.toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Embeddable
    public static class FuelPrice {
        private String type;

        @Embedded
        private Price price;
        private String updateTime;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {
        private String currencyCode;
        private String units;
        private long nanos;

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public double getDollarPrice() {
            return Double.parseDouble(units) + (nanos / 1_000_000_000.0);
        }

        public long getNanos() {
            return nanos;
        }

        public void setNanos(long nanos) {
            this.nanos = nanos;
        }
    }
}
