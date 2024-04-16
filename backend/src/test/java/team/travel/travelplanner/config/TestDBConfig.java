package team.travel.travelplanner.config;

import io.zonky.test.db.provider.postgres.PostgreSQLContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class TestDBConfig {
    @Bean
    public PostgreSQLContainerCustomizer containerCustomizer() {
        // Make sure PostGIS is initialized for the template database
        return container -> container.withDatabaseName("template1");
    }

//    @Bean
//    public DataSourceInitializer schemaDataSourceInitializer(DataSource dataSource) {
//        Resource schemaSQL = new ClassPathResource("schema-postgres.sql");
//
//        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
//        dataSourceInitializer.setDataSource(dataSource);
//        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(schemaSQL));
//        return dataSourceInitializer;
//    }
}