package team.travel.travelplanner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.travel.travelplanner.service.impl.GoogleMaps.GoogleMapsApiClientService;

@Configuration
@ConfigurationProperties(prefix = "googlemaps")
public class GoogleMapsApiConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleMapsApiConfig.class);
    private String googleMapsApiKey;

    @Bean
    public GoogleMapsApiClientService geocodingService() {
        if (googleMapsApiKey != null) {
            LOGGER.info("Google Maps Api key was supplied. Google geocoder will be used.");
            return new GoogleMapsApiClientService(googleMapsApiKey);
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
