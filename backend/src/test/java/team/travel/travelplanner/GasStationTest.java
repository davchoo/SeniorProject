package team.travel.travelplanner;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import team.travel.travelplanner.model.GasStationModel;
import team.travel.travelplanner.service.impl.GasStationServiceImpl;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GasStationTest {

   @Autowired
    private GasStationServiceImpl gasStationService;

    @Test
    public void testGetGasStationsAlongRoute() throws Exception {
        Resource polylineResource = new ClassPathResource("gas_station_polyline.txt");
        String polyline = polylineResource.getContentAsString(StandardCharsets.US_ASCII);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        LineString lineString = geometryFactory.createLineString(EncodedPolylineUtils.decodePolyline(polyline));

        List<GasStationModel> gasStations = gasStationService.getGasStationsAlongRoute(lineString, 482803, "REGULAR_UNLEADED");
        assertEquals(4, gasStations.size());
    }
}
