package team.travel.travelplanner.ndfd.grid;

import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleGridDataSource implements AutoCloseable {
    private final List<GridDataset> datasets;
    private final TreeMap<Instant, TimeSlice> timeSlices;

    private Instant referenceTime;

    public SimpleGridDataSource(List<GridDataset> datasets) {
        this.datasets = new ArrayList<>(datasets);
        this.timeSlices = new TreeMap<>();
        datasets.forEach(this::loadDataset);
    }

    private void loadDataset(GridDataset dataset) {
        GridDatatype grid = dataset.getGrids().getFirst(); // TODO how to handle multiple grids?
        GridCoordSystem cs = grid.getCoordinateSystem();
        CoordinateAxis1DTime timeAxis = cs.getTimeAxis1D();
        CoordinateAxis1DTime runTimeAxis = cs.getRunTimeAxis();

        if (referenceTime == null) {
            referenceTime = Instant.ofEpochMilli(runTimeAxis.getCalendarDate(0).getMillis());
        } else if (!timeSlices.isEmpty()) {
            Instant earliestTime = timeSlices.firstKey();
            Instant dataSetStartTime = Instant.ofEpochMilli(timeAxis.getCalendarDate(0).getMillis());
            if (earliestTime.isAfter(dataSetStartTime)) {
                referenceTime = Instant.ofEpochMilli(runTimeAxis.getCalendarDate(0).getMillis());
            }
        }

        for (int i = 0; i < timeAxis.getSize(); i++) {
            Instant time = Instant.ofEpochMilli(timeAxis.getCalendarDate(i).getMillis());
            timeSlices.put(time, new TimeSlice(i, time, grid));
        }
    }

    public Instant getDataStartTime() {
        return referenceTime;
    }

    public Instant getDataEndTime() {
        return timeSlices.lastKey();
    }

    public TimeSliceData getTimeSlice(Instant time) throws IOException {
        Map.Entry<Instant, TimeSlice> entry = timeSlices.ceilingEntry(time);
        if (entry == null) {
            return null;
        }
        return entry.getValue().getData();
    }

    @Override
    public void close() throws IOException {
        for (GridDataset dataset : datasets) {
            dataset.close();
        }
    }

    private record TimeSlice(int timeIdx, Instant time, GridDatatype grid) {
        public TimeSliceData getData() throws IOException {
            return new TimeSliceData(grid, timeIdx, time);
        }
    }
}
