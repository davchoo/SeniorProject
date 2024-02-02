package team.travel.travelplanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "travel-planner.weather")
public class WeatherDataConfig {
    private URL nationalWeatherForecastWfcUrl;

    private Path schemaCachePath = Path.of("./schemaCache");

    public URL getNationalWeatherForecastWfcUrl() {
        return nationalWeatherForecastWfcUrl;
    }

    public void setNationalWeatherForecastWfcUrl(URL nationalWeatherForecastWfcUrl) {
        this.nationalWeatherForecastWfcUrl = nationalWeatherForecastWfcUrl;
    }

    public Path getSchemaCachePath() {
        return schemaCachePath;
    }

    public void setSchemaCachePath(Path schemaCachePath) {
        this.schemaCachePath = schemaCachePath;
    }
}
