package team.travel.travelplanner.model.weather;

import java.util.List;

public record RasterWeatherModel(
        float[] data,
        List<String> labels
) {
}
