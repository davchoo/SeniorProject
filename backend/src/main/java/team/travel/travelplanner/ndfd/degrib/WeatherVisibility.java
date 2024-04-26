package team.travel.travelplanner.ndfd.degrib;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public enum WeatherVisibility {
    VIS_NOVIS("<NoVis>", "255"),
    VIS_0("0SM", "0"),
    VIS_8("1/4SM", "8"),
    VIS_16("1/2SM", "16"),
    VIS_24("3/4SM", "24"),
    VIS_32("1SM", "32"),
    VIS_48("11/2SM", "48"),
    VIS_64("2SM", "64"),
    VIS_80("21/2SM", "80"),
    VIS_96("3SM", "96"),
    VIS_128("4SM", "128"),
    VIS_160("5SM", "160"),
    VIS_192("6SM", "192"),
    // Past 6 SM (encode as 7 SM).
    VIS_224("P6SM", "224"),
    VIS_UNKNOWN("<unknown>", "Unknown Visibility");

    private final String abbreviation;
    private final String name;

    WeatherVisibility(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, WeatherVisibility> ABBREV_MAP = new HashMap<>();

    static {
        for (var x : values()) {
            ABBREV_MAP.put(x.getAbbreviation(), x);
        }
    }

    public static WeatherVisibility fromAbbreviation(String abbreviation) {
        return ABBREV_MAP.getOrDefault(abbreviation, VIS_UNKNOWN);
    }
}
