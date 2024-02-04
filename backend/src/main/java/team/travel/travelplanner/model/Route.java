package team.travel.travelplanner.model;

import java.time.Instant;

public record Route(String polyline, int[] durations, Instant startTime) {
}
