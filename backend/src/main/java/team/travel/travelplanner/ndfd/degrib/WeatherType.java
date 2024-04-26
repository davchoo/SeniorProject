package team.travel.travelplanner.ndfd.degrib;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public enum WeatherType {
    WX_NOWX("<NoWx>", "No Weather"),
    // Dry Obstruction to visibility.
    WX_K("K", "Smoke"),
    WX_BD("BD", "Blowing Dust"),
    WX_BS("BS", "Blowing Snow"),
    // Moist Obstruction to visibility.
    WX_H("H", "Haze"),
    WX_F("F", "Fog"),
    WX_L("L", "Drizzle"),
    // Warm moisture.
    WX_R("R", "Rain"),
    WX_RW("RW", "Rain Showers"),
    // 'A' has have been dropped as of 8/12/2004
    WX_A("A", "Hail"),
    // Freezing / Mix moisture.
    WX_FR("FR", "Frost"),
    WX_ZL("ZL", "Freezing Drizzle"),
    WX_ZR("ZR", "Freezing Rain"),
    // Frozen moisture.
    WX_IP("IP", "Ice Pellets (sleet)"),
    WX_S("S", "Snow"),
    WX_SW("SW", "Snow Showers"),
    // Extra.
    WX_T("T", "Thunderstorms"),
    WX_BN("BN", "Blowing Sand"),
    WX_ZF("ZF", "Freezing Fog"),
    WX_IC("IC", "Ice Crystals"),
    WX_IF("IF", "Ice Fog"),
    WX_VA("VA", "Volcanic Ash"),
    WX_ZY("ZY", "Freezing Spray"),
    WX_WP("WP", "Water Spouts"),
    WX_UNKNOWN("<unknown>", "Unknown Weather");

    private final String abbreviation;
    private final String name;

    WeatherType(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, WeatherType> ABBREV_MAP = new HashMap<>();

    static {
        for (var x : values()) {
            ABBREV_MAP.put(x.getAbbreviation(), x);
        }
    }

    public static WeatherType fromAbbreviation(String abbreviation) {
        return ABBREV_MAP.getOrDefault(abbreviation, WX_UNKNOWN);
    }
}
