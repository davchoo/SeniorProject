package team.travel.travelplanner.service.impl;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import team.travel.travelplanner.model.weather.RasterWeatherModel;
import team.travel.travelplanner.ndfd.NDFDWeatherSection;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherTable4;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherType;
import team.travel.travelplanner.ndfd.grid.SimpleGridDataSource;
import team.travel.travelplanner.ndfd.grid.TimeSliceData;
import team.travel.travelplanner.service.RasterWeatherDataService;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.grid.GridDataset;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Service
public class RasterWeatherDataServiceImpl implements RasterWeatherDataService {
    @Override
    public RasterWeatherModel checkWeather(LineString route, int[] durations, Instant startTime) throws IOException {
        List<GridDataset> datasets = getWeatherDatasets();
        try (SimpleGridDataSource ds = new SimpleGridDataSource(datasets)) {
            Map<Instant, NDFDWeatherSection> weatherSections = NDFDWeatherSection.loadWeatherSections(datasets);
            Instant dataStartTime = ds.getDataStartTime();

            Instant currentTime = startTime;
            CoordinateSequence sequence = route.getCoordinateSequence();
            float[] data = new float[sequence.size()];
            Arrays.fill(data, Float.NaN);

            int i = 0;
            // Skip points that are in the past
            for (; i < sequence.size() - 1; i++) {
                if (currentTime.isAfter(dataStartTime)) {
                    break;
                }
                currentTime = currentTime.plusMillis(durations[i]);
            }

            TimeSliceData timeSlice = ds.getTimeSlice(currentTime);
            if (timeSlice == null) {
                return null; // No data?
            }
            NDFDWeatherSection section = weatherSections.get(timeSlice.getTime());
            for (; i < sequence.size(); i++) {
                if (currentTime.isAfter(timeSlice.getTime())) {
                    // Need next time slice
                    timeSlice = ds.getTimeSlice(currentTime);
                    if (timeSlice == null) {
                        break; // Too far into the future, no more data available
                    }
                    section = weatherSections.get(timeSlice.getTime());
                }
                float value = timeSlice.getFloat(sequence.getY(i), sequence.getX(i));
                if (!Float.isNaN(value)) {
                    SimpleWeatherType type = SimpleWeatherTable4.getLabel2(section.getSimpleWeatherCode((int) value));
                    data[i] = type.ordinal();
                }
                if (i < sequence.size() - 1) {
                    currentTime = currentTime.plusMillis(durations[i]);
                }
            }
            return new RasterWeatherModel(data, Arrays.stream(SimpleWeatherType.values()).map(SimpleWeatherType::getLabel).toList());
        }
    }

    private List<GridDataset> getWeatherDatasets() throws IOException {
        // TODO configurable base directory
        // TODO specify continent?
        return List.of(
                GridDataset.open("ndfd/AR.conus/VP.001-003/ds.wx.bin", EnumSet.of(NetcdfDataset.Enhance.CoordSystems)), // TODO need other enhancements?
                GridDataset.open("ndfd/AR.conus/VP.004-007/ds.wx.bin", EnumSet.of(NetcdfDataset.Enhance.CoordSystems))
        );
    }
}
