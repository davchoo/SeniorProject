package team.travel.travelplanner.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static team.travel.travelplanner.util.SRIDConstants.WGS84;

@Configuration
public class GeometryFactoryConfig {
    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory(new PrecisionModel(), WGS84);
    }
}
