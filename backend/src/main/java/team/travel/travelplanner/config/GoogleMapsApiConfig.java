package team.travel.travelplanner.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClientServiceImpl;

@Configuration
@ConfigurationProperties(prefix = "googlemaps")
public class GoogleMapsApiConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleMapsApiConfig.class);
    private String googleMapsApiKey;

    @Bean
    public GoogleMapsApiClientServiceImpl geocodingService(RestClient.Builder restClientBuilder) {
        if (googleMapsApiKey != null) {
            LOGGER.info("Google Maps Api key was supplied. Google geocoder will be used.");
            return new GoogleMapsApiClientServiceImpl(googleMapsApiKey, restClientBuilder);
        }
        else{
            LOGGER.warn("No Google Maps Api key was supplied");
            return null;
        }
    }

    public String getGoogleMapsApiKey() {
        return googleMapsApiKey;
    }

    public void setGoogleMapsApiKey(String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
    }

}
