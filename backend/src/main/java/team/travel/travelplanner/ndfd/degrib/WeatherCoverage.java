package team.travel.travelplanner.ndfd.degrib;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public enum WeatherCoverage {
    COV_NOCOV("<NoCov>", "No Coverage/Probability"),
    COV_ISO("Iso", "Isolated"),
    COV_SCT("Sct", "Scattered"),
    COV_NUM("Num", "Numerous"),
    COV_WIDE("Wide", "Widespread"),
    COV_OCNL("Ocnl", "Occasional"),
    COV_SCHC("SChc", "Slight Chance of"),
    COV_CHC("Chc", "Chance of"),
    COV_LKLY("Lkly", "Likely"),
    COV_DEF("Def", "Definite"),
    COV_PATCHY("Patchy", "Patchy"),
    COV_AREAS("Areas", "Areas of"),
    // Added 8/13/2004
    COV_PDS("Pds", "Periods of"),
    COV_FRQ("Frq", "Frequent"),
    COV_INTER("Inter", "Intermittent"),
    COV_BRIEF("Brf", "Brief"),
    COV_UNKNOWN("<unknown>", "Unknown Coverage");


    private final String abbreviation;
    private final String name;

    WeatherCoverage(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, WeatherCoverage> ABBREV_MAP = new HashMap<>();

    static {
        for (var x : values()) {
            ABBREV_MAP.put(x.getAbbreviation(), x);
        }
    }

    public static WeatherCoverage fromAbbreviation(String abbreviation) {
        return ABBREV_MAP.getOrDefault(abbreviation, COV_UNKNOWN);
    }
}
