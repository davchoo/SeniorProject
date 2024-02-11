package team.travel.travelplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FuelOptions {
    private FuelOptionsData fuelOptions;

    public FuelOptionsData getFuelOptions() {
        return fuelOptions;
    }

    public void setFuelOptions(FuelOptionsData fuelOptions) {
        this.fuelOptions = fuelOptions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FuelOptionsData {
        private List<FuelPrice> fuelPrices;

        public List<FuelPrice> getFuelPrices() {
            return fuelPrices;
        }

        public void setFuelPrices(List<FuelPrice> fuelPrices) {
            this.fuelPrices = fuelPrices;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FuelPrice {
        private String type;
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
