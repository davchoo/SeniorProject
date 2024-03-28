package team.travel.travelplanner.ndfd.grid;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;

import java.io.IOException;
import java.time.Instant;

public class TimeSliceData {
    private final Instant time;

    private final Array data;
    private final Index index;
    private final GridCoordSystem crs;
    private final int[] xyIdx;

    public TimeSliceData(GridDatatype grid, int timeIdx, Instant time) throws IOException {
        this.time = time;

        Array data = grid.readVolumeData(timeIdx);
        if (grid.getZDimension() != null) {
            data = data.slice(0, 0); // Drop z dimension
        }
        this.data = data;
        this.index = data.getIndex();
        this.crs = grid.getCoordinateSystem();
        this.xyIdx = new int[2];
    }
    public Instant getTime() {
        return time;
    }

    public float getFloat(double latitude, double longitude) {
        crs.findXYindexFromLatLon(latitude, longitude, xyIdx);
        if (xyIdx[0] == -1 || xyIdx[1] == -1) {
            return Float.NaN;
        }
        index.set(xyIdx[1], xyIdx[0]); // y then x
        return data.getFloat(index);
    }
}
