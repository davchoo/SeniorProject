package team.travel.travelplanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "travel-planner.weather")
public class WeatherDataConfig {
    private URL nationalWeatherForecastWfcUrl;

    private Path schemaCachePath = Path.of("./schemaCache");

    private Duration timeout = Duration.ofSeconds(30);

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

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}
