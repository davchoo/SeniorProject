package team.travel.travelplanner.model.error;

public record ValueErrorModel(String path, String value, String message) {
}
