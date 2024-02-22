package team.travel.travelplanner.model;

public record CountyModel(
        String fips,
        String countyName,
        String stateAbbrev,
        String geometryWKT
) {
}
