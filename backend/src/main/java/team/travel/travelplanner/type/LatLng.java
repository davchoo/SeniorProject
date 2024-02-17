package team.travel.travelplanner.type;

public record LatLng(double latitude, double longitude) {

    public String toString(){
        return latitude + "," +longitude;
    }
}