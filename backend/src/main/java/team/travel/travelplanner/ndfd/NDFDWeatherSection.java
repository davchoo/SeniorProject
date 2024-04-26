package team.travel.travelplanner.ndfd;

import team.travel.travelplanner.ndfd.degrib.WeatherWord;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherCode4;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.grib.grib2.Grib2Index;
import ucar.nc2.grib.grib2.Grib2Record;
import ucar.nc2.grib.grib2.Grib2SectionLocalUse;
import ucar.nc2.grib.grib2.table.Grib2Tables;
import ucar.nc2.time.CalendarDate;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class NDFDWeatherSection {
    private final List<List<WeatherWord>> weatherStrings;
    private final int[] simpleWeatherCodes;

    public NDFDWeatherSection(Grib2SectionLocalUse localUseSection) throws IOException {
        int[] intData = unpackMDLIntegerData(localUseSection);
        this.weatherStrings = unpackWeatherStringTable(intData);
        this.simpleWeatherCodes = convertWeatherStrings(this.weatherStrings);
    }

    private int[] unpackMDLIntegerData(Grib2SectionLocalUse localUseSection) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(localUseSection.getRawBytes()));
        boolean isMDLPacked = dis.readByte() == 1;
        if (!isMDLPacked) {
            throw new IOException("Expected Local Use Section to be MDL packed");
        }
        int numGroups = dis.readUnsignedShort();
        List<int[]> integerData = new ArrayList<>();
        for (int i = 0; i < numGroups; i++) {
            int numValues = dis.readInt();
            int refValue = dis.readInt();
            int scale = dis.readUnsignedShort();
            int recScale10 = (int) (Math.pow(10, -scale));
            int bitsPerElement = dis.readUnsignedByte();
            byte dataType = dis.readByte();

            if (bitsPerElement >= 32) {
                throw new IOException("Bits per element is too large. Expected: [1-32). Actual: " + bitsPerElement);
            }
            if (dataType == 1) {
                int[] iData = new int[numValues];

                int readBits = 0;
                int buffer = 0;
                for (int j = 0; j < numValues; j++) {
                    while (readBits < bitsPerElement) {
                        readBits += 8;
                        buffer = buffer << 8 | dis.readUnsignedByte();
                    }
                    iData[j] = recScale10 * (buffer >> (readBits - bitsPerElement) + refValue);
                    readBits -= bitsPerElement;
                    buffer &= (1 << readBits) - 1;
                }
                integerData.add(iData);
            } else {
                throw new IOException("Unsupported data type: " + dataType);
            }
        }

        if (integerData.size() == 1) {
            return integerData.getFirst();
        }
        // Merge the groups together
        int[] fullData = new int[integerData.stream().mapToInt(x -> x.length).sum()];
        int start = 0;
        for (int[] group : integerData) {
            System.arraycopy(group, 0, fullData, start, group.length);
            start += group.length;
        }
        return fullData;
    }

    private List<List<WeatherWord>> unpackWeatherStringTable(int[] intData) {
        List<List<WeatherWord>> table = new ArrayList<>();
        char[] buffer = new char[64];
        int i = 0;
        while (i < intData.length) {
            int j = 0;
            while (i < intData.length && intData[i] != 0) {
                buffer[j++] = (char) intData[i++];
                if (buffer.length == j) {
                    // Resize the buffer
                    char[] newBuffer = new char[2 * buffer.length];
                    System.arraycopy(buffer, 0, newBuffer, 0, j);
                    buffer = newBuffer;
                }
            }
            i++; // Skip null terminator
            char[] shrunk = new char[j];
            System.arraycopy(buffer, 0, shrunk, 0, j);
            String weatherString = new String(shrunk);

            List<WeatherWord> weatherWords = Arrays.stream(weatherString.split("\\^")).map(WeatherWord::parse).toList();
            table.add(weatherWords);
        }
        return table;
    }

    private int[] convertWeatherStrings(List<List<WeatherWord>> weatherStrings) {
        int[] codes = new int[weatherStrings.size()];
        for (int i = 0; i < weatherStrings.size(); i++) {
            codes[i] = SimpleWeatherCode4.NDFD_WxTable4(weatherStrings.get(i));
        }
        return codes;
    }

    public List<WeatherWord> getWeatherString(int index) {
        return weatherStrings.get(index);
    }

    public int getSimpleWeatherCode(int index) {
        return simpleWeatherCodes[index];
    }

    public int getNumWeatherStrings() {
        return weatherStrings.size();
    }

    public static Map<Instant, NDFDWeatherSection> loadWeatherSections(List<GridDataset> datasets) throws IOException {
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
