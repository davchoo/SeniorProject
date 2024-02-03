package team.travel.travelplanner;

import org.geolatte.geom.crs.CrsRegistry;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.logging.LogbackLoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) {
        // Initialize GeoTools and set up its logger before it is used in the application
        GeoTools.setLoggerFactory(LogbackLoggerFactory.getInstance());
        GeoTools.init();
        // Preload CRS to make debugging less atrocious
        CRS.getAuthorityFactory(false);
        // Same with Geolatte because apparently we need TWO spatial libraries
        CrsRegistry.getCrsIdForEPSG(4326); // WGS 84

        SpringApplication.run(TravelPlannerApplication.class, args);
    }

}
