package team.travel.travelplanner.ndfd.degrib.simple;

/**
 * @author David Choo
 */
public enum SimpleWeatherType {
    NONE("None"),
    RAIN("Rain"),
    ICE("Ice"),
    SNOW("Snow"),
    MIX("Mix"),
    SEVERE("Severe"),
    FOG("Fog"),
    SMOKE("Smoke"),
    BLOWING("Blowing"),
    HAZE("Haze");

    private final String label;

    SimpleWeatherType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
