package team.travel.travelplanner.ndfd.converter;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.constants.CF;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.write.NetcdfFormatWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class NoOpGridConverter extends AbstractGridConverter {
    private final int[] origin;

    public NoOpGridConverter(List<GridDataset> datasets) {
        super(datasets);
        this.origin = new int[3];
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addDataVariable(NetcdfFormatWriter.Builder writerBuilder) {
        VariableDS dataVariable = baseGrid.getVariable();
        writerBuilder.addVariable(dataVariable.getShortName(), dataVariable.getDataType(), "time y x").addAttributes(dataVariable.attributes()).addAttribute(new Attribute(CF.COORDINATES, "time y x"));
    }

    @Override
    protected void writeDataVariable(Array volumeData, int fullTimeIdx, Instant time, NetcdfFormatWriter writer) throws IOException, InvalidRangeException {
        origin[0] = fullTimeIdx;
        int[] originalShape = volumeData.getShape();
        int[] shape = new int[]{1, originalShape[0], originalShape[1]}; // Add time axis
        volumeData = volumeData.reshapeNoCopy(shape);
        writer.write(baseGrid.getVariable().getShortName(), origin, volumeData);
    }
}
