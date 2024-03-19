package team.travel.travelplanner.ndfd.degrib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author David Choo
 */
public record WeatherWord(WeatherCoverage coverage, WeatherType type, WeatherIntensity intensity,
                          WeatherVisibility visibility, List<WeatherAttribute> attributes) {
    public String toEnglish() {
        StringBuilder b = new StringBuilder();
        if (coverage != WeatherCoverage.COV_NOCOV) {
            b.append(coverage.getName());
            b.append(' ');
        }
        if (intensity != WeatherIntensity.INT_NOINT) {
            b.append(intensity.getName());
            b.append(' ');
        }
        b.append(type.getName());
        return b.toString();
    }

    private static <T> T parse(String[] parts, int i, Function<String, T> parser, T defaultValue) {
        return parts.length > i ? parser.apply(parts[i]) : defaultValue;
    }

    public static WeatherWord parse(String wordStr) {
        String[] parts = wordStr.split(":");
        WeatherCoverage coverage = parse(parts, 0, WeatherCoverage::fromAbbreviation, WeatherCoverage.COV_NOCOV);
        WeatherType type = parse(parts, 1, WeatherType::fromAbbreviation, WeatherType.WX_NOWX);
        WeatherIntensity intensity = parse(parts, 2, WeatherIntensity::fromAbbreviation, WeatherIntensity.INT_NOINT);
        WeatherVisibility visibility = parse(parts, 3, WeatherVisibility::fromAbbreviation, WeatherVisibility.VIS_NOVIS);
        List<WeatherAttribute> attributes = new ArrayList<>();
        if (parts.length > 4) {
            for (String attributeAbbrev : parts[4].split(",")) {
                attributes.add(WeatherAttribute.fromAbbreviation(attributeAbbrev));
            }
        }
        return new WeatherWord(coverage, type, intensity, visibility, attributes);
    }
}
