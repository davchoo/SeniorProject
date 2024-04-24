package team.travel.travelplanner.config;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.maps.model.LatLng;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.travel.travelplanner.databind.LatLngMixin;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperCustomizer() {
        return m -> {
            m.mixIn(LatLng.class, LatLngMixin.class);
            m.modules(new GuavaModule());
        };
    }
}
