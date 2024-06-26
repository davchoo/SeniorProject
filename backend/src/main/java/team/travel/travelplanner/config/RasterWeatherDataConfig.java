package team.travel.travelplanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "travel-planner.weather.raster-data")
public class RasterWeatherDataConfig {

    private boolean autoUpdate = true;

    private Duration updatePeriod = Duration.ofHours(1);

    private String ndfdS3Bucket = "noaa-ndfd-pds";

    private Path gribStoragePath = Path.of("./ndfd");

    private Path netcdfStoragePath = Path.of("./ndfd-nc");

    private Path temporaryStoragePath = Path.of(".");

    private Set<String> areas = Set.of("conus");

    private Set<String> datasets = Set.of("wx");

    private GeoServerConfig geoServer;

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Duration getUpdatePeriod() {
        if (updatePeriod.toMinutes() < 30) {
            return Duration.ofMinutes(30); // There's no point to updating faster than every 30 minutes
        }
        return updatePeriod;
    }

    public void setUpdatePeriod(Duration updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public String getNdfdS3Bucket() {
        return ndfdS3Bucket;
    }

    public void setNdfdS3Bucket(String ndfdS3Bucket) {
        this.ndfdS3Bucket = ndfdS3Bucket;
    }

    public Path getGribStoragePath() {
        return gribStoragePath;
    }

    public void setGribStoragePath(Path gribStoragePath) {
        this.gribStoragePath = gribStoragePath;
    }

    public Path getNetcdfStoragePath() {
        return netcdfStoragePath;
    }

    public void setNetcdfStoragePath(Path netcdfStoragePath) {
        this.netcdfStoragePath = netcdfStoragePath;
    }

    public Path getTemporaryStoragePath() {
        return temporaryStoragePath;
    }

    public void setTemporaryStoragePath(Path temporaryStoragePath) {
        this.temporaryStoragePath = temporaryStoragePath;
    }

    public Set<String> getAreas() {
        return areas;
    }

    public void setAreas(Set<String> areas) {
        this.areas = areas;
    }

    public Set<String> getDatasets() {
        return datasets;
    }

    public void setDatasets(Set<String> datasets) {
        this.datasets = datasets;
    }

    public GeoServerConfig getGeoServer() {
        return geoServer;
    }

    public void setGeoServer(GeoServerConfig geoServer) {
        this.geoServer = geoServer;
    }

    public static class GeoServerConfig {
        private URI resetEndpoint;

        private URI capabilitiesEndpoint;

        private URI massTruncateEndpoint;

        private List<String> truncatedLayers = List.of();

        private String username;

        private String password;

        public URI getResetEndpoint() {
            return resetEndpoint;
        }

        public void setResetEndpoint(URI resetEndpoint) {
            this.resetEndpoint = resetEndpoint;
        }

        public URI getCapabilitiesEndpoint() {
            return capabilitiesEndpoint;
        }

        public void setCapabilitiesEndpoint(URI capabilitiesEndpoint) {
            this.capabilitiesEndpoint = capabilitiesEndpoint;
        }

        public URI getMassTruncateEndpoint() {
            return massTruncateEndpoint;
        }

        public void setMassTruncateEndpoint(URI massTruncateEndpoint) {
            this.massTruncateEndpoint = massTruncateEndpoint;
        }

        public List<String> getTruncatedLayers() {
            return truncatedLayers;
        }

        public void setTruncatedLayers(List<String> truncatedLayers) {
            this.truncatedLayers = truncatedLayers;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
