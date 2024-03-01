package team.travel.travelplanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "travel-planner.weather.alert")
public class WeatherAlertConfig {
    private URI nwsAlertEndpoint = URI.create("https://api.weather.gov/alerts/active");

    private boolean pullingEnabled = true;

    private Duration pullPeriod = Duration.ofMinutes(5);

    private Duration historyLength = Duration.ofDays(7);

    public URI getNwsAlertEndpoint() {
        return nwsAlertEndpoint;
    }

    public void setNwsAlertEndpoint(URI nwsAlertEndpoint) {
        this.nwsAlertEndpoint = nwsAlertEndpoint;
    }

    public boolean isPullingEnabled() {
        return pullingEnabled;
    }

    public void setPullingEnabled(boolean pullingEnabled) {
        this.pullingEnabled = pullingEnabled;
    }

    public Duration getPullPeriod() {
        if (pullPeriod.toSeconds() < 120) {
            return Duration.ofMinutes(2); // Prevent pulling faster than every 2 minutes
        }
        return pullPeriod;
    }

    public void setPullPeriod(Duration pullPeriod) {
        this.pullPeriod = pullPeriod;
    }

    public Duration getHistoryLength() {
        return historyLength;
    }

    public void setHistoryLength(Duration historyLength) {
        this.historyLength = historyLength;
    }
}
