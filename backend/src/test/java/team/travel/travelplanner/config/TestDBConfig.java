package team.travel.travelplanner.config;

import io.zonky.test.db.provider.postgres.PostgreSQLContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDBConfig {
    @Bean
    public PostgreSQLContainerCustomizer containerCustomizer() {
        // Make sure PostGIS is initialized for the template database
        return container -> container.withDatabaseName("template1");
    }
}
