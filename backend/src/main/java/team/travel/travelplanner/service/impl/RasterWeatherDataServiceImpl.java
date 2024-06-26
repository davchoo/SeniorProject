package team.travel.travelplanner.service.impl;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import team.travel.travelplanner.config.RasterWeatherDataConfig;
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.RasterWeatherModel;
import team.travel.travelplanner.ndfd.NDFDWeatherSection;
import team.travel.travelplanner.ndfd.converter.AbstractGridConverter;
import team.travel.travelplanner.ndfd.converter.NoOpGridConverter;
import team.travel.travelplanner.ndfd.converter.WeatherGridConverter;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherTable4;
import team.travel.travelplanner.ndfd.grid.SimpleGridDataSource;
import team.travel.travelplanner.ndfd.grid.TimeSliceData;
import team.travel.travelplanner.service.RasterWeatherDataService;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.grid.GridDataset;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class RasterWeatherDataServiceImpl implements RasterWeatherDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RasterWeatherDataServiceImpl.class);

    private final GeometryFactory geometryFactory;

    private final S3AsyncClient s3Client;
    private final S3TransferManager transferManager;

    private final RasterWeatherDataConfig config;
    private final RestClient restClient;
    private final TaskScheduler taskScheduler;

    private Instant lastUpdate;

    public RasterWeatherDataServiceImpl(GeometryFactory geometryFactory, S3AsyncClient s3Client,
                                        S3TransferManager transferManager, RasterWeatherDataConfig config,
                                        RestClient.Builder restClientBuilder, TaskScheduler taskScheduler) {
        this.geometryFactory = geometryFactory;
        this.s3Client = s3Client;
        this.transferManager = transferManager;
        this.config = config;
        this.restClient = restClientBuilder.build();
        this.taskScheduler = taskScheduler;
        this.lastUpdate = Instant.EPOCH;
    }

    @Override
    public RasterWeatherModel checkWeather(RouteModel route, String area, String dataset) throws IOException {
        List<GridDataset> datasets = openDatasets(area, dataset);
        try (SimpleGridDataSource ds = new SimpleGridDataSource(datasets)) {
            List<String> labels = null;
            Map<Instant, NDFDWeatherSection> weatherSections = null;
            if (dataset.equals("wx")) {
                weatherSections = NDFDWeatherSection.loadWeatherSections(datasets);
                labels = SimpleWeatherTable4.getPackedCodeLabels();
            }

            Instant dataStartTime = ds.getDataStartTime();

            Instant currentTime = route.startTime();
            CoordinateSequence sequence = route.geometry(geometryFactory).getCoordinateSequence();
            float[] data = new float[sequence.size()];
            Arrays.fill(data, Float.NaN);

            int i = 0;
            // Skip points that are in the past
            for (; i < sequence.size() - 1; i++) {
                if (currentTime.isAfter(dataStartTime)) {
                    break;
                }
                currentTime = currentTime.plusMillis(route.durations()[i]);
            }

            TimeSliceData timeSlice = ds.getTimeSlice(currentTime);
            if (timeSlice == null) {
                return null; // No data?
            }
            for (; i < sequence.size(); i++) {
                if (currentTime.isAfter(timeSlice.getTime())) {
                    // Need next time slice
                    timeSlice = ds.getTimeSlice(currentTime);
                    if (timeSlice == null) {
                        break; // Too far into the future, no more data available
                    }
                }
                float value = timeSlice.getFloat(sequence.getY(i), sequence.getX(i));
                if (weatherSections != null && !Float.isNaN(value)) {
                    NDFDWeatherSection section = weatherSections.get(timeSlice.getTime());
                    int code = section.getSimpleWeatherCode((int) value);
                    data[i] = SimpleWeatherTable4.getPackedCode(code);
                } else {
                    data[i] = value;
                }
                if (i < sequence.size() - 1) {
                    currentTime = currentTime.plusMillis(route.durations()[i]);
                }
            }
            return new RasterWeatherModel(data, labels);
        }
    }

    @Override
    public Collection<String> getAvailableAreas() {
        return config.getAreas(); // TODO walk GRIB storage to get available areas
    }

    @Override
    public Collection<String> getAvailableDatasets() {
        return config.getDatasets(); // TODO walk GRIB storage to get available datasets for an area
    }

    private List<GridDataset> openDatasets(String area, String dataset) throws IOException {
        Path base = config.getGribStoragePath().resolve("AR." + area);
        List<String> paths;
        try (Stream<Path> stream = Files.walk(base)) {
            paths = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.endsWith("ds." + dataset + ".bin"))
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .toList();
        } catch (IOException ex) {
            LOGGER.debug("Failed to walk GRIB storage path for Area: {} Dataset: {}", area, dataset, ex);
            return List.of();
        }
        List<GridDataset> datasets = new ArrayList<>(paths.size());
        for (String path : paths) {
            datasets.add(GridDataset.open(path, EnumSet.of(NetcdfDataset.Enhance.CoordSystems))); // TODO need other enhancements?
        }
        return datasets;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!config.isAutoUpdate()) {
            return;
        }
        checkLastUpdate();
        taskScheduler.scheduleAtFixedRate(this::checkForUpdates, config.getUpdatePeriod());
    }

    private void checkLastUpdate() {
        if (!Files.isDirectory(config.getGribStoragePath())) {
            // No files downloaded, set to epoch to make sure files are downloaded regardless of modification time
            lastUpdate = Instant.EPOCH;
            return;
        }
        try (Stream<Path> stream = Files.walk(config.getGribStoragePath())) {
            lastUpdate = stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".bin"))
                    .map(path -> {
                        try {
                            return Files.getLastModifiedTime(path);
                        } catch (IOException e) {
                            LOGGER.error("Unable to get modification time for {}", path, e);
                            return FileTime.fromMillis(0);
                        }
                    })
                    .map(FileTime::toInstant)
                    .max(Instant::compareTo)
                    .orElse(Instant.EPOCH);
        } catch (IOException e) {
            LOGGER.error("Failed to walk local raster weather data storage folder", e);
            lastUpdate = Instant.EPOCH;
        }
    }

    public void checkForUpdates() {
        LOGGER.info("Checking NDFD S3 Bucket for updated objects");
        try {
            Integer fileCount = s3Client.listObjectsV2(builder -> builder
                            .bucket(config.getNdfdS3Bucket())
                            .prefix("opnl/")
                            .build())
                    .thenComposeAsync(response -> downloadObjects(response.contents()))
                    .get();
            lastUpdate = Instant.now();
            LOGGER.info("Finished checking S3 Bucket. Downloaded {} files", fileCount);
            boolean hasUpdatedGrids = updateGrids();
            if (hasUpdatedGrids) {
                notifyGeoserver();
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to check NDFD S3 bucket", e);
        }
    }

    private CompletableFuture<Integer> downloadObjects(List<S3Object> objects) {
        try {
            AtomicInteger fileCount = new AtomicInteger();
            List<CompletableFuture<?>> futures = new ArrayList<>();
            Path tmpDir = Files.createTempDirectory(config.getTemporaryStoragePath(), "tmp-raster-data");
            int i = 0;
            for (S3Object object : objects) {
                if (!shouldDownload(object)) {
                    continue;
                }
                Path tmpDestination = tmpDir.resolve(System.currentTimeMillis() + "_" + i++ + ".tmp");
                Path destination = getDestination(object.key());
                Files.createDirectories(destination.getParent());
                DownloadFileRequest request = DownloadFileRequest.builder()
                        .getObjectRequest(builder -> builder
                                .bucket(config.getNdfdS3Bucket())
                                .key(object.key()))
                        .destination(tmpDestination)
                        .build();
                futures.add(transferManager.downloadFile(request)
                        .completionFuture()
                        .handle((result, throwable) -> {
                            if (throwable != null) {
                                LOGGER.error("Failed to download {}", object.key(), throwable);
                                return null;
                            }
                            try {
                                Files.move(tmpDestination, destination, StandardCopyOption.ATOMIC_MOVE);
                                fileCount.incrementAndGet();
                            } catch (IOException e) {
                                LOGGER.error("Failed to move file to final destination. Key: {} Destination: {}", object.key(), destination, e);
                            }
                            return null;
                        }));
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply((x) -> {
                        try {
                            Files.delete(tmpDir);
                        } catch (IOException e) {
                            LOGGER.warn("Failed to delete temp directory", e);
                        }
                        return fileCount.get();
                    });
        } catch (IOException e) {
            LOGGER.error("Failed to download objects", e);
            return CompletableFuture.completedFuture(0);
        }
    }

    private Path getDestination(String key) {
        if (!key.startsWith("opnl/")) {
            throw new IllegalArgumentException("Expected key to start with 'opnl/' Key: " + key);
        }
        String relPath = key.substring("opnl/".length());
        return config.getGribStoragePath().resolve(relPath);
    }

    private boolean shouldDownload(S3Object object) {
        String key = object.key();
        // Only operational datasets
        if (!key.startsWith("opnl/")) {
            return false;
        }
        key = key.substring("opnl/".length());
        // Check area
        if (key.startsWith("AR.")) {
            String area = key.substring("AR.".length(), key.indexOf("/"));
            if (!config.getAreas().contains(area)) {
                return false;
            }
        } else {
            return false;
        }
        // Make sure it's not in a weird folder
        if (key.contains("/old/")) {
            return false;
        }
        // Make sure it ends in .bin
        if (!key.endsWith(".bin")) {
            return false;
        }
        // Check what dataset it is
        int dsIdx = key.lastIndexOf("/ds.");
        if (dsIdx == -1) {
            return false; // Not a dataset?
        }
        dsIdx += "/ds.".length();
        int period = key.indexOf(".", dsIdx + 1);
        String dataset = key.substring(dsIdx, period);
        if (!config.getDatasets().contains(dataset)) {
            return false;
        }
        // Check if was modified since our last update
        return object.lastModified().isAfter(lastUpdate);
    }

    private boolean updateGrids() {
        LOGGER.info("Updating grids");
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory(config.getTemporaryStoragePath(), "tmp-grid");
        } catch (IOException e) {
            LOGGER.error("Unable to create temporary directory", e);
            return false;
        }

        boolean updatedGrid = false;
        int i = 0;
        for (String area : config.getAreas()) {
            for (String dataset : config.getDatasets()) {
                List<GridDataset> datasets = List.of();
                try {
                    datasets = openDatasets(area, dataset);
                    if (datasets.isEmpty()) {
                        LOGGER.info("No datasets for Area: {} Dataset: {}", area, dataset);
                        continue;
                    }
                    long dataSetLastModified = datasets.stream()
                            .mapToLong(GridDataset::getLastModified)
                            .max()
                            .orElse(0);

                    Path tmpDestination = tmpDir.resolve(System.currentTimeMillis() + "_" + i++ + ".tmp");
                    Path destination = config.getNetcdfStoragePath().resolve(Path.of(area, dataset + ".nc"));
                    Files.createDirectories(destination.getParent());
                    if (Files.exists(destination) && Files.getLastModifiedTime(destination).toMillis() > dataSetLastModified) {
                        continue;
                    }

                    LOGGER.info("Updating grid for Area: {} Dataset: {}", area, dataset);
                    AbstractGridConverter converter;
                    if (dataset.equals("wx")) {
                        converter = new WeatherGridConverter(datasets);
                    } else {
                        converter = new NoOpGridConverter(datasets, dataset, dataset);
                    }
                    converter.convert(tmpDestination.toString());
                    Files.move(tmpDestination, destination, StandardCopyOption.ATOMIC_MOVE);
                    updatedGrid = true;
                } catch (Exception e) {
                    LOGGER.error("Failed to update grid. Area: {} Dataset: {}", area, dataset, e);
                } finally {
                    for (GridDataset ds : datasets) {
                        try {
                            ds.close();
                        } catch (IOException e) {
                            LOGGER.error("Failed to close dataset", e);
                        }
                    }
                }
            }
        }
        try {
            Files.delete(tmpDir);
        } catch (IOException e) {
            LOGGER.warn("Failed to delete temp directory", e);
        }
        LOGGER.info("Finished updating grids");
        return updatedGrid;
    }

    private void notifyGeoserver() {
        RasterWeatherDataConfig.GeoServerConfig gsConfig = config.getGeoServer();
        if (gsConfig != null) {
            LOGGER.info("Notifying GeoServer of updated grids");
            String credentials = gsConfig.getUsername() + ":" + gsConfig.getPassword();
            String authorization = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            ResponseEntity<Void> response = restClient.post()
                    .uri(gsConfig.getResetEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve()
                    .toBodilessEntity();
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOGGER.error("Failed to notify GeoServer. Status Code: {}", response.getStatusCode());
                return;
            }
            LOGGER.info("Successfully notified GeoServer");

            preloadGeoServerCapabilities();
            truncateGeoServerLayers(authorization);
        }
    }

    private void preloadGeoServerCapabilities() {
        RasterWeatherDataConfig.GeoServerConfig gsConfig = config.getGeoServer();
        if (gsConfig.getCapabilitiesEndpoint() == null) {
            return;
        }
        LOGGER.info("Preloading capabilities XML from GeoServer");
        ResponseEntity<Void> response = restClient.get()
                .uri(gsConfig.getCapabilitiesEndpoint())
                .retrieve()
                .toBodilessEntity();
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Failed to preload capabilities XML from GeoServer. Status Code: {}", response.getStatusCode());
        }
    }

    private void truncateGeoServerLayers(String authorization) {
        RasterWeatherDataConfig.GeoServerConfig gsConfig = config.getGeoServer();
        if (gsConfig.getMassTruncateEndpoint() == null) {
            return;
        }
        for (String layer : gsConfig.getTruncatedLayers()) {
            LOGGER.info("Truncating layer: {} in GeoServer", layer);
            String body = String.format("<truncateLayer><layerName>%s</layerName></truncateLayer>", layer);
            ResponseEntity<Void> response = restClient.post()
                    .uri(gsConfig.getMassTruncateEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .contentType(MediaType.TEXT_XML)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOGGER.error("Failed to mass truncate layer: {} in GeoServer. Status Code: {}", layer, response.getStatusCode());
            }
        }
    }
}
