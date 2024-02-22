package team.travel.travelplanner.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import team.travel.travelplanner.repository.CountyRepository;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Configuration
public class CountiesDataSourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountiesDataSourceConfig.class);

    @Bean
    public DataSourceInitializer countiesDataSourceInitializer(DataSource dataSource, CountyRepository countyRepository) throws IOException {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(connection -> {
            if (countyRepository.count() > 0) {
                LOGGER.info("Counties table contains some data. Skipping population.");
                return;
            }
            LOGGER.info("Counties table is missing. Populating from c_05mr24.sql.gz");
            Resource resource = new ClassPathResource("c_05mr24.sql.gz");
            try (GZIPInputStream is = new GZIPInputStream(resource.getInputStream())) {
                ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
                Resource countiesSQL = new ByteArrayResource(is.readAllBytes());
                resourceDatabasePopulator.addScript(countiesSQL);
                resourceDatabasePopulator.populate(connection);
                LOGGER.info("Done populating Counties table");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        return dataSourceInitializer;
    }
}
