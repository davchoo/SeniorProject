package team.travel.travelplanner.ndfd;

import team.travel.travelplanner.ndfd.degrib.WeatherWord;
import ucar.nc2.grib.grib2.Grib2SectionLocalUse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDFDWeatherSection {
    private final List<List<WeatherWord>> weatherStrings;

    public NDFDWeatherSection(Grib2SectionLocalUse localUseSection) throws IOException {
        int[] intData = unpackMDLIntegerData(localUseSection);
        this.weatherStrings = unpackWeatherStringTable(intData);
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

    public List<WeatherWord> getWeatherString(int index) {
        return weatherStrings.get(index);
    }

    public int getNumWeatherStrings() {
        return weatherStrings.size();
    }
}
