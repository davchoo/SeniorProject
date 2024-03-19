package team.travel.travelplanner.ndfd.degrib;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public enum WeatherIntensity {
    INT_NOINT("<NoInten>", "No Intensity"),
    INT_DD("--", "Very Light"),
    INT_D("-", "Light"),
    INT_M("m", "Moderate"),
    INT_P("+", "Heavy"),
    INT_UNKNOWN("<unknown>", "Unknown Intensity");

    private final String abbreviation;
    private final String name;

    WeatherIntensity(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, WeatherIntensity> ABBREV_MAP = new HashMap<>();

    static {
        for (var x : values()) {
            ABBREV_MAP.put(x.getAbbreviation(), x);
        }
    }

    public static WeatherIntensity fromAbbreviation(String abbreviation) {
        return ABBREV_MAP.getOrDefault(abbreviation, INT_UNKNOWN);
    }
}
