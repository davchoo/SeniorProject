package team.travel.travelplanner.ndfd;

import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherProbability;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherTable4;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherType;
import ucar.ma2.*;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.CF;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dataset.CoordinateTransform;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.grib.grib2.Grib2Index;
import ucar.nc2.grib.grib2.Grib2Record;
import ucar.nc2.grib.grib2.table.Grib2Tables;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.write.Nc4Chunking;
import ucar.nc2.write.Nc4ChunkingStrategy;
import ucar.nc2.write.NetcdfFileFormat;
import ucar.nc2.write.NetcdfFormatWriter;
import ucar.unidata.util.Parameter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class WeatherNetCDFConverter {
    public static void convert(List<GridDataset> datasets, String dstLocation) throws Exception {
        Map<Instant, NDFDWeatherSection> weatherSections = loadWeatherSections(datasets);

        Nc4Chunking chunker = Nc4ChunkingStrategy.factory(Nc4Chunking.Strategy.grib, 1, false);
        NetcdfFormatWriter.Builder writerBuilder = NetcdfFormatWriter.createNewNetcdf4(NetcdfFileFormat.NETCDF4, dstLocation, chunker);

        GridDatatype baseGrid = datasets.getFirst().getGrids().getFirst();
        GridCoordSystem baseCs = baseGrid.getCoordinateSystem();

        writerBuilder.addAttribute(new Attribute(CF.CONVENTIONS, "CF-1.8"));
        // TODO reference times?
        addCoordinateTransformVars(baseCs.getCoordinateTransforms(), writerBuilder);

        writerBuilder.addUnlimitedDimension("time");
        writerBuilder.addVariable("time", DataType.ULONG, "time")
                .addAttribute(new Attribute(CDM.UNITS, "seconds since " + Instant.EPOCH));

        copyAxis(baseCs.getXHorizAxis(), writerBuilder);
        copyAxis(baseCs.getYHorizAxis(), writerBuilder);

        writerBuilder.addVariable("wx", DataType.UBYTE, "time y x")
                .addAttribute(baseGrid.getVariable().findAttribute(CF.GRID_MAPPING))
                .addAttribute(new Attribute(CF.COORDINATES, "time y x"));

        try (NetcdfFormatWriter writer = writerBuilder.build()) {
            writer.write("x", baseCs.getXHorizAxis().read());
            writer.write("y", baseCs.getYHorizAxis().read());

            ArrayLong.D1 timeData = new ArrayLong.D1(1, true);

            int xLength = (int) baseCs.getXHorizAxis().getSize();
            int yLength = (int) baseCs.getYHorizAxis().getSize();
            ArrayByte.D3 wxData = new ArrayByte.D3(1, yLength, xLength, true);

            int[] origin = new int[3];
            int[] timeOrigin = new int[1];
            int fullTimeIdx = 0;

            for (GridDataset dataset : datasets) {
                GridDatatype grid = dataset.getGrids().getFirst();
                CoordinateAxis1DTime timeAxis = grid.getCoordinateSystem().getTimeAxis1D();
                for (int timeIdx = 0; timeIdx < timeAxis.getSize(); timeIdx++) {
                    CalendarDate time = timeAxis.getCalendarDate(timeIdx);
                    Instant instant = Instant.ofEpochMilli(time.getMillis());
                    timeData.setLong(0, instant.getEpochSecond());

                    NDFDWeatherSection wxSection = weatherSections.get(instant);
                    Array data = grid.readVolumeData(timeIdx);
                    if (grid.getZDimension() != null) {
                        data = data.slice(0, 0); // Drop z dimension
                    }

                    Index index = data.getIndex();
                    for (int y = 0; y < yLength; y++) {
                        for (int x = 0; x < xLength; x++) {
                            int tableIdx = data.getInt(index.set(y, x));
                            int code = wxSection.getSimpleWeatherCode(tableIdx);
                            SimpleWeatherType type = SimpleWeatherTable4.getLabel2(code);
                            SimpleWeatherProbability probability = SimpleWeatherTable4.getProbability(code);
                            byte val = (byte) (3 * type.ordinal() + probability.ordinal());
                            wxData.set(0, y, x, val);
                        }
                    }

                    timeOrigin[0] = fullTimeIdx;
                    origin[0] = fullTimeIdx;
                    fullTimeIdx++;
                    writer.write("wx", origin, wxData);
                    writer.write("time", timeOrigin, timeData);
                }
            }
        }
    }

    private static void copyAxis(CoordinateAxis axis, NetcdfFormatWriter.Builder writerBuilder) {
        writerBuilder.addDimension(Dimension.builder(axis.getDimensionsString(), (int) axis.getSize()).build());
        writerBuilder.addVariable(axis.getShortName(), DataType.FLOAT, axis.getDimensionsString())
                .getAttributeContainer().addAll(axis.getOriginalVariable().attributes());
    }

    private static void addCoordinateTransformVars(List<CoordinateTransform> transforms, NetcdfFormatWriter.Builder writerBuilder) {
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

    public static void main(String[] args) throws Exception {
        List<GridDataset> datasets = List.of(
                GridDataset.open("ndfd/AR.conus/VP.001-003/ds.wx.bin", EnumSet.of(NetcdfDataset.Enhance.CoordSystems)), // TODO need other enhancements?
                GridDataset.open("ndfd/AR.conus/VP.004-007/ds.wx.bin", EnumSet.of(NetcdfDataset.Enhance.CoordSystems))
        );
        convert(datasets, "wx.nc");
    }


    private static Map<Instant, NDFDWeatherSection> loadWeatherSections(List<GridDataset> datasets) throws IOException {
        // TODO deduplicate
        Map<Instant, NDFDWeatherSection> weatherSections = new HashMap<>();
        for (GridDataset dataset : datasets) {
            Grib2Index index = new Grib2Index();
            index.readIndex(dataset.getLocation(), dataset.getLastModified());
            for (Grib2Record record : index.getRecords()) {
                Grib2Tables tables = Grib2Tables.factory(record);
                CalendarDate date = tables.getForecastDate(record);
                if (date == null) {
                    // ERROR missing forecast date??
                    continue;
                }
                Instant instant = Instant.ofEpochMilli(date.getMillis());
                weatherSections.put(instant, new NDFDWeatherSection(record.getLocalUseSection()));
            }
        }
        return weatherSections;
    }
}
