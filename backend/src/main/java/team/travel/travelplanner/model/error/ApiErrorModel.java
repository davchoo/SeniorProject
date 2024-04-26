package team.travel.travelplanner.model.error;

import java.time.Instant;
import java.util.Objects;

public class ApiErrorModel {
    private final Instant timestamp;
    private final String type;
    private final String message;

    public ApiErrorModel(String type, String message) {
        this.timestamp = Instant.now();
        this.type = type;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiErrorModel that = (ApiErrorModel) o;
        return timestamp == that.timestamp && Objects.equals(type, that.type) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, type, message);
    }

    @Override
    public String toString() {
        return "ApiErrorModel{" +
                "timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
