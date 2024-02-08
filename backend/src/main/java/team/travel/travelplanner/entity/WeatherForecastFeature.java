package team.travel.travelplanner.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.model.RouteWeatherFeature;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@SqlResultSetMapping(name = "RouteWeatherFeature",
        classes = @ConstructorResult(
                targetClass = RouteWeatherFeature.class,
                columns = {
                        @ColumnResult(name = "i"),
                        @ColumnResult(name = "weather_feature_type"),
                        @ColumnResult(name = "forecast_day"),
                        @ColumnResult(name = "file_date"),
                        @ColumnResult(name = "start_timestamp"),
                        @ColumnResult(name = "end_timestamp"),
                }
        )
)
@NamedNativeQuery(
        name = "WeatherForecastFeature.checkRouteWeather",
        query = "select i, weather_feature_type, forecast_day, file_date, start_timestamp, end_timestamp from check_route_weather(:route, :durations, :startTime)",
        resultSetMapping = "RouteWeatherFeature"
)
public class WeatherForecastFeature {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "forecast_day") // H2 does not like day as an identifier
    private int day;

    private String popUpContent;

    @CreationTimestamp
    private Instant retrievalTimestamp;

    private ZonedDateTime fileDate;

    private ZonedDateTime validStart;

    private ZonedDateTime validEnd;

    @Enumerated(EnumType.STRING)
    private WeatherFeatureType weatherFeatureType;

    private Geometry geometry;

    public WeatherForecastFeature() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getPopUpContent() {
        return popUpContent;
    }

    public void setPopUpContent(String popUpContent) {
        this.popUpContent = popUpContent;
    }

    public Instant getRetrievalTimestamp() {
        return retrievalTimestamp;
    }

    public void setRetrievalTimestamp(Instant retrievalTimestamp) {
        this.retrievalTimestamp = retrievalTimestamp;
    }

    public ZonedDateTime getFileDate() {
        return fileDate;
    }

    public void setFileDate(ZonedDateTime fileDate) {
        this.fileDate = fileDate;
    }

    public ZonedDateTime getValidStart() {
        return validStart;
    }

    public void setValidStart(ZonedDateTime validStart) {
        this.validStart = validStart;
    }

    public ZonedDateTime getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(ZonedDateTime validEnd) {
        this.validEnd = validEnd;
    }

    public WeatherFeatureType getWeatherFeatureType() {
        return weatherFeatureType;
    }

    public void setWeatherFeatureType(WeatherFeatureType weatherFeatureType) {
        this.weatherFeatureType = weatherFeatureType;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
