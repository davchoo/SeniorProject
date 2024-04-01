package team.travel.travelplanner.ndfd.converter;

import com.google.common.base.Preconditions;
import ucar.ma2.Array;
import ucar.ma2.ArrayLong;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.CF;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dataset.CoordinateTransform;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.write.Nc4Chunking;
import ucar.nc2.write.Nc4ChunkingStrategy;
import ucar.nc2.write.NetcdfFileFormat;
import ucar.nc2.write.NetcdfFormatWriter;
import ucar.unidata.util.Parameter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public abstract class AbstractGridConverter {
    private final List<GridDataset> datasets;

    protected final GridDatatype baseGrid;
    protected final GridCoordSystem baseCs;

    protected final ArrayLong.D1 timeData = new ArrayLong.D1(1, true);
    protected final int[] timeOrigin = new int[1];

    public AbstractGridConverter(List<GridDataset> datasets) {
        this.datasets = datasets;

        this.baseGrid = datasets.getFirst().getGrids().getFirst();
        this.baseCs = baseGrid.getCoordinateSystem();
        checkDatasets();
    }

    protected void addGlobalAttributes(NetcdfFormatWriter.Builder writerBuilder) {
        writerBuilder.addAttribute(new Attribute(CF.CONVENTIONS, "CF-1.8"));
    }

    protected void addTimeAxis(NetcdfFormatWriter.Builder writerBuilder) {
        writerBuilder.addUnlimitedDimension("time");
        writerBuilder.addVariable("time", DataType.ULONG, "time")
                .addAttribute(new Attribute(CDM.UNITS, "seconds since " + Instant.EPOCH));
    }

    protected void addSpaceAxis(NetcdfFormatWriter.Builder writerBuilder) {
        copyAxis(baseCs.getXHorizAxis(), writerBuilder);
        copyAxis(baseCs.getYHorizAxis(), writerBuilder);
    }

    protected abstract void addDataVariable(NetcdfFormatWriter.Builder writerBuilder);

    protected void addAdditionalVariables(NetcdfFormatWriter.Builder writerBuilder) {
    }

    protected void writeSpaceVariables(NetcdfFormatWriter writer) throws IOException, InvalidRangeException {
        writer.write("x", baseCs.getXHorizAxis().read());
        writer.write("y", baseCs.getYHorizAxis().read());
    }

    protected void writeAdditionalVariables(NetcdfFormatWriter writer) throws IOException, InvalidRangeException {
    }

    protected abstract void writeDataVariable(Array volumeData, int fullTimeIdx, Instant time, NetcdfFormatWriter writer) throws IOException, InvalidRangeException;

    protected void writeTimeVariable(int fullTimeIdx, Instant time, NetcdfFormatWriter writer) throws InvalidRangeException, IOException {
        timeData.setLong(0, time.getEpochSecond());
        timeOrigin[0] = fullTimeIdx;
        writer.write("time", timeOrigin, timeData);
    }

    public final void convert(String destinationPath) throws Exception {
        Nc4Chunking chunker = Nc4ChunkingStrategy.factory(Nc4Chunking.Strategy.grib, 1, false);
        NetcdfFormatWriter.Builder writerBuilder = NetcdfFormatWriter.createNewNetcdf4(NetcdfFileFormat.NETCDF4, destinationPath, chunker);

        addGlobalAttributes(writerBuilder);

        addCoordinateTransformVars(baseCs.getCoordinateTransforms(), writerBuilder);
        addTimeAxis(writerBuilder);
        addSpaceAxis(writerBuilder);
        addDataVariable(writerBuilder);
        addAdditionalVariables(writerBuilder);

        try (NetcdfFormatWriter writer = writerBuilder.build()) {
            writeSpaceVariables(writer);
            writeAdditionalVariables(writer);

            int fullTimeIdx = 0;
            for (GridDataset dataset : datasets) {
                GridDatatype grid = dataset.getGrids().getFirst();
                CoordinateAxis1DTime timeAxis = grid.getCoordinateSystem().getTimeAxis1D();
                for (int timeIdx = 0; timeIdx < timeAxis.getSize(); timeIdx++) {
                    CalendarDate time = timeAxis.getCalendarDate(timeIdx);
                    Instant instant = Instant.ofEpochMilli(time.getMillis());
                    writeTimeVariable(fullTimeIdx, instant, writer);

                    Array data = grid.readVolumeData(timeIdx);
                    if (grid.getZDimension() != null) { // TODO don't drop if z dimension length > 1
                        data = data.slice(0, 0); // Drop z dimension
                    }
                    writeDataVariable(data, fullTimeIdx, instant, writer);
                    fullTimeIdx++;
                }
            }
        }
    }

    protected void copyAxis(CoordinateAxis axis, NetcdfFormatWriter.Builder writerBuilder) {
        writerBuilder.addDimension(Dimension.builder(axis.getDimensionsString(), (int) axis.getSize()).build());
        writerBuilder.addVariable(axis.getShortName(), DataType.FLOAT, axis.getDimensionsString())
                .getAttributeContainer().addAll(axis.getOriginalVariable().attributes());
    }

    protected void addCoordinateTransformVars(List<CoordinateTransform> transforms, NetcdfFormatWriter.Builder writerBuilder) {
        for (CoordinateTransform transform : transforms) {
            Variable.Builder<?> varBuilder = writerBuilder.addVariable(transform.getName(), DataType.INT, "");
            for (Parameter p : transform.getParameters()) {
                if (p.isString()) {
                    if (p.getStringValue() == null) {
                        continue;
                    }
                    varBuilder.addAttribute(new Attribute(p.getName(), p.getStringValue()));
                } else {
                    varBuilder.addAttribute(new Attribute(p.getName(), p.getNumericValue()));
                }
            }
        }
    }

    protected int getXLength() {
        return (int) baseCs.getXHorizAxis().getSize();
    }

    protected int getYLength() {
        return (int) baseCs.getYHorizAxis().getSize();
    }

    protected void checkDatasets() {
        int xLength = getXLength();
        int yLength = getYLength();
        VariableDS baseVariable = baseGrid.getVariable();
        for (GridDataset dataset : datasets) {
            GridDatatype grid = dataset.getGrids().getFirst();
            GridCoordSystem cs = grid.getCoordinateSystem();
            Preconditions.checkArgument(Objects.equals(baseCs.getCoordinateTransforms(), cs.getCoordinateTransforms()), "Datasets have different coordinate transforms");

            Preconditions.checkArgument(cs.getXHorizAxis().getSize() == xLength, "Datasets have different x lengths");
            Preconditions.checkArgument(cs.getYHorizAxis().getSize() == yLength, "Datasets have different y lengths");

            VariableDS variable = grid.getVariable();
            Preconditions.checkArgument(Objects.equals(baseVariable.getShortName(), variable.getShortName()), "Dataset grid variables have different short names");
            Preconditions.checkArgument(Objects.equals(baseVariable.getDataType(), variable.getDataType()), "Datasets grid variables different data types");
            Preconditions.checkArgument(
                    Objects.equals(
                            baseVariable.findAttribute(CDM.UNITS),
                            variable.findAttribute(CDM.UNITS)),
                    "Dataset grid variables have different units"
            );
        }
    }
}
