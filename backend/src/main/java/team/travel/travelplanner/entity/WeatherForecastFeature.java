package team.travel.travelplanner.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.entity.type.WeatherFeatureType;

import java.time.Instant;

@Entity
public class WeatherForecastFeature {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "forecast_day") // H2 does not like day as an identifier
    private int day;

    private String popUpContent;

    @CreationTimestamp
    private Instant retrievalTimestamp;

    private String fileDate; // TODO use Date or some other type

    private Instant validStart;

    private Instant validEnd;

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

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public Instant getValidStart() {
        return validStart;
    }

    public void setValidStart(Instant validStart) {
        this.validStart = validStart;
    }

    public Instant getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Instant validEnd) {
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
