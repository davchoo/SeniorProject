package team.travel.travelplanner.ndfd.degrib;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public enum WeatherAttribute {
    HAZ_NOHAZ("", "None"),
    HAZ_FL("FL", "Frequent Lightning"),
    HAZ_GW("GW", "Gusty Winds"),
    HAZ_HVYRN("HvyRn", "Heavy Rain"),
    HAZ_DMGW("DmgW", "Damaging Wind"),
    HAZ_A("SmA", "Small Hail"),
    HAZ_LGA("LgA", "Large Hail"),
    HAZ_OLA("OLA", "Outlying Areas"),
    HAZ_OBO("OBO", "on Bridges and Overpasses"),
    // The following attributes were added 8/13/2004
    HAZ_OGA("OGA", "On Grassy Areas"),
    HAZ_DRY("Dry", "dry"),
    HAZ_TOR("TOR", "Tornado"),
    HAZ_PRI2("Primary", "Highest Ranking"),
    HAZ_PRI1("Mention", "Include Unconditionally"),
    HAZ_OR("OR", "or"),
    HAZ_MX("MX", "mixture"),
    HAZ_UNKNOWN("<unknown>", "Unknown Hazard");

    private final String abbreviation;
    private final String name;

    WeatherAttribute(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, WeatherAttribute> ABBREV_MAP = new HashMap<>();

    static {
        for (var x : values()) {
            ABBREV_MAP.put(x.getAbbreviation(), x);
        }
    }

    public static WeatherAttribute fromAbbreviation(String abbreviation) {
        return ABBREV_MAP.getOrDefault(abbreviation, HAZ_UNKNOWN);
    }
}
