package team.travel.travelplanner.ndfd.converter;

import team.travel.travelplanner.ndfd.NDFDWeatherSection;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherTable4;
import ucar.ma2.*;
import ucar.nc2.Attribute;
import ucar.nc2.constants.CF;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.write.NetcdfFormatWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class WeatherGridConverter extends AbstractGridConverter {
    private final Map<Instant, NDFDWeatherSection> weatherSections;
    private final ArrayByte.D3 wxData;
    private final int[] origin;

    public WeatherGridConverter(List<GridDataset> datasets) throws IOException {
        super(datasets);
        this.weatherSections = NDFDWeatherSection.loadWeatherSections(datasets);

        this.wxData = new ArrayByte.D3(1, getYLength(), getXLength(), true);
        this.origin = new int[3];
    }

    @Override
    protected void addDataVariable(NetcdfFormatWriter.Builder writerBuilder) {
        writerBuilder.addVariable("wx", DataType.UBYTE, "time y x")
                .addAttribute(baseGrid.getVariable().findAttribute(CF.GRID_MAPPING))
                .addAttribute(new Attribute(CF.COORDINATES, "time y x"));
    }

    @Override
    protected void writeDataVariable(Array volumeData, int fullTimeIdx, Instant time, NetcdfFormatWriter writer) throws IOException, InvalidRangeException {
        int xLength = getXLength();
        int yLength = getYLength();
        NDFDWeatherSection wxSection = weatherSections.get(time);

        Index index = volumeData.getIndex();
        for (int y = 0; y < yLength; y++) {
            for (int x = 0; x < xLength; x++) {
                int tableIdx = volumeData.getInt(index.set(y, x));
                int code = wxSection.getSimpleWeatherCode(tableIdx);
                wxData.set(0, y, x, SimpleWeatherTable4.getPackedCode(code));
            }
        }

        origin[0] = fullTimeIdx;
        writer.write("wx", origin, wxData);
    }
}
