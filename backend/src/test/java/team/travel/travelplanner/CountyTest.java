package team.travel.travelplanner;

import com.google.common.collect.ListMultimap;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.travel.travelplanner.model.CountyModel;
import team.travel.travelplanner.repository.CountyRepository;
import team.travel.travelplanner.service.CountyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static team.travel.travelplanner.util.SRIDConstants.WGS84;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
public class CountyTest {
    private static final String GLOUCESTER_COUNTY_NJ = "034015";
    private static final String CUMBERLAND_COUNTY_NJ = "034011";
    private static final String HARFORD_COUNTY_NJ = "024025";
    private static final String UNKNOWN_COUNTY = "999999";

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private CountyService countyService;

    @Test
    void countyTablePopulated() {
        assertTrue(countyRepository.count() > 0);
    }

    @Test
    void getSingleCounty() {
        ListMultimap<String, CountyModel> models = countyService.getCounties(List.of(GLOUCESTER_COUNTY_NJ));
        assertEquals(1, models.size());
        assertTrue(models.containsKey(GLOUCESTER_COUNTY_NJ));

        // Only one feature for Gloucester county
        assertEquals(1, models.get(GLOUCESTER_COUNTY_NJ).size());
        CountyModel gloucesterCounty = models.get(GLOUCESTER_COUNTY_NJ).getFirst();
        assertNotNull(gloucesterCounty);
        assertEquals(GLOUCESTER_COUNTY_NJ, gloucesterCounty.fips());
        assertEquals("Gloucester", gloucesterCounty.countyName());
        assertEquals("NJ", gloucesterCounty.stateAbbrev());

        assertNotNull(gloucesterCounty.geometry());
        assertInstanceOf(MultiPolygon.class, gloucesterCounty.geometry());
        assertEquals(WGS84, gloucesterCounty.geometry().getSRID());

        Geometry envelop = gloucesterCounty.geometry().getEnvelope();
        assertInstanceOf(Polygon.class, envelop);
        Coordinate[] coordinates = envelop.getCoordinates(); // minX minY, minX maxY, maxX maxY, maxX minY, minX minY
        assertEquals(coordinates[0].getX(), -75.43929290799997);
        assertEquals(coordinates[0].getY(), 39.51481246900005);
        assertEquals(coordinates[2].getX(), -74.87809753399995);
        assertEquals(coordinates[2].getY(), 39.88831329300007);
    }

    @Test
    void getUnknownCounty() {
        // No errors or counties are returned for an unknown FIPS code
        ListMultimap<String, CountyModel> models = countyService.getCounties(List.of(UNKNOWN_COUNTY));
        assertTrue(models.isEmpty());
    }

    @Test
    void getNoCounty() {
        // No errors or counties are returned for an empty list of FIPS codes
        ListMultimap<String, CountyModel> models = countyService.getCounties(List.of());
        assertTrue(models.isEmpty());
    }

    @Test
    void getMultipleCounties() {
        ListMultimap<String, CountyModel> models = countyService.getCounties(List.of(CUMBERLAND_COUNTY_NJ, GLOUCESTER_COUNTY_NJ));
        assertEquals(2, models.size());
        assertTrue(models.containsKey(CUMBERLAND_COUNTY_NJ));
        assertTrue(models.containsKey(GLOUCESTER_COUNTY_NJ));

        // Only one feature for Cumberland county
        assertEquals(1, models.get(CUMBERLAND_COUNTY_NJ).size());
        CountyModel cumberlandCounty = models.get(CUMBERLAND_COUNTY_NJ).getFirst();
        assertNotNull(cumberlandCounty);
        assertEquals(CUMBERLAND_COUNTY_NJ, cumberlandCounty.fips());
        assertEquals("Cumberland", cumberlandCounty.countyName());
        assertEquals("NJ", cumberlandCounty.stateAbbrev());

        // Only one feature for Gloucester county
        assertEquals(1, models.get(GLOUCESTER_COUNTY_NJ).size());
        CountyModel gloucesterCounty = models.get(GLOUCESTER_COUNTY_NJ).getFirst();
        assertNotNull(gloucesterCounty);
        assertEquals(GLOUCESTER_COUNTY_NJ, gloucesterCounty.fips());
        assertEquals("Gloucester", gloucesterCounty.countyName());
        assertEquals("NJ", gloucesterCounty.stateAbbrev());
    }

    @Test
    void getCountyWithMultipleFeatures() {
        // Some counties can have multiple features
        ListMultimap<String, CountyModel> models = countyService.getCounties(List.of(HARFORD_COUNTY_NJ));
        assertEquals(1, models.keySet().size());
        assertTrue(models.containsKey(HARFORD_COUNTY_NJ));

        // Two features
        List<CountyModel> features = models.get(HARFORD_COUNTY_NJ);
        assertEquals(2, features.size());
        for (CountyModel harfordCounty : features) {
            assertNotNull(harfordCounty);
            assertEquals(HARFORD_COUNTY_NJ, harfordCounty.fips());
            assertEquals("Harford", harfordCounty.countyName());
            assertEquals("MD", harfordCounty.stateAbbrev());

            assertNotNull(harfordCounty.geometry());
            assertInstanceOf(MultiPolygon.class, harfordCounty.geometry());
            assertEquals(WGS84, harfordCounty.geometry().getSRID());

            Geometry envelop = harfordCounty.geometry().getEnvelope();
            assertInstanceOf(Polygon.class, envelop);
        }
        // Geometries differ
        assertNotEquals(features.getFirst().geometry(), features.get(1).geometry());
    }
}
