package team.travel.travelplanner.ndfd.degrib.simple;

/**
 * @author David Choo
 */
public enum SimpleWeatherProbability {
    LOW_PROBABILITY("LoProb"),
    HIGH_PROBABILITY("HiProb"),
    UNKNOWN_PROBABILITY("");


    private final String label;

    SimpleWeatherProbability(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
