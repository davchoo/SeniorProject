package team.travel.travelplanner;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static team.travel.travelplanner.util.SRIDConstants.WGS84;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
public class CountyTest {
    private static final String GLOUCESTER_COUNTY_NJ = "034015";
    private static final String CUMBERLAND_COUNTY_NJ = "034011";
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
        Map<String, CountyModel> models = countyService.getCounties(List.of(GLOUCESTER_COUNTY_NJ));
        assertEquals(1, models.size());
        assertTrue(models.containsKey(GLOUCESTER_COUNTY_NJ));

        CountyModel gloucesterCounty = models.get(GLOUCESTER_COUNTY_NJ);
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
        Map<String, CountyModel> models = countyService.getCounties(List.of(UNKNOWN_COUNTY));
        assertEquals(0, models.size());
    }

    @Test
    void getNoCounty() {
        // No errors or counties are returned for an empty list of FIPS codes
        Map<String, CountyModel> models = countyService.getCounties(List.of());
        assertEquals(0, models.size());
    }

    @Test
    void getMultipleCounties() {
        Map<String, CountyModel> models = countyService.getCounties(List.of(CUMBERLAND_COUNTY_NJ, GLOUCESTER_COUNTY_NJ));
        assertEquals(2, models.size());
        assertTrue(models.containsKey(CUMBERLAND_COUNTY_NJ));
        assertTrue(models.containsKey(GLOUCESTER_COUNTY_NJ));

        CountyModel cumberlandCounty = models.get(CUMBERLAND_COUNTY_NJ);
        assertNotNull(cumberlandCounty);
        assertEquals(CUMBERLAND_COUNTY_NJ, cumberlandCounty.fips());
        assertEquals("Cumberland", cumberlandCounty.countyName());
        assertEquals("NJ", cumberlandCounty.stateAbbrev());

        CountyModel gloucesterCounty = models.get(GLOUCESTER_COUNTY_NJ);
        assertNotNull(gloucesterCounty);
        assertEquals(GLOUCESTER_COUNTY_NJ, gloucesterCounty.fips());
        assertEquals("Gloucester", gloucesterCounty.countyName());
        assertEquals("NJ", gloucesterCounty.stateAbbrev());
    }
}
