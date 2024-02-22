package team.travel.travelplanner.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "c_05mr24")
public class County {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogc_fid")
    private int ogcFID;

    @Column(length = 2)
    private String state;

    @Column(length = 9)
    private String cwa;

    @Column(name = "countyname", length = 24)
    private String countyName;

    @Column(length = 5)
    private String fips;

    @Column(name = "time_zone", length = 2)
    private String timeZone;

    @Column(name = "fe_area", length = 2)
    private String feArea;

    @Column(columnDefinition = "numeric(19, 11)")
    private double lon;

    @Column(columnDefinition = "numeric(19, 11)")
    private double lat;

    @Column(name = "wkb_geometry")
    private Geometry geometry;

    public int getOgcFID() {
        return ogcFID;
    }

    public void setOgcFID(int ogcFID) {
        this.ogcFID = ogcFID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCwa() {
        return cwa;
    }

    public void setCwa(String cwa) {
        this.cwa = cwa;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getFips() {
        return fips;
    }

    public void setFips(String fips) {
        this.fips = fips;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getFeArea() {
        return feArea;
    }

    public void setFeArea(String feArea) {
        this.feArea = feArea;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
