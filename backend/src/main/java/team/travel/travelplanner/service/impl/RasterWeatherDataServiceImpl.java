package team.travel.travelplanner.service.impl;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import team.travel.travelplanner.config.RasterWeatherDataConfig;
import team.travel.travelplanner.model.weather.RasterWeatherModel;
import team.travel.travelplanner.ndfd.NDFDWeatherSection;
import team.travel.travelplanner.ndfd.converter.AbstractGridConverter;
import team.travel.travelplanner.ndfd.converter.NoOpGridConverter;
import team.travel.travelplanner.ndfd.converter.WeatherGridConverter;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherTable4;
import team.travel.travelplanner.ndfd.degrib.simple.SimpleWeatherType;
import team.travel.travelplanner.ndfd.grid.SimpleGridDataSource;
import team.travel.travelplanner.ndfd.grid.TimeSliceData;
import team.travel.travelplanner.service.RasterWeatherDataService;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.grid.GridDataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class RasterWeatherDataServiceImpl implements RasterWeatherDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RasterWeatherDataServiceImpl.class);

    private final S3AsyncClient s3Client;
    private final S3TransferManager transferManager;

    private final RasterWeatherDataConfig config;
    private final TaskScheduler taskScheduler;

    private Instant lastUpdate;

    public RasterWeatherDataServiceImpl(S3AsyncClient s3Client, S3TransferManager transferManager, RasterWeatherDataConfig config, TaskScheduler taskScheduler) {
        this.s3Client = s3Client;
        this.transferManager = transferManager;
        this.config = config;
        this.taskScheduler = taskScheduler;
        this.lastUpdate = Instant.EPOCH;
    }

    @Override
    public RasterWeatherModel checkWeather(LineString route, int[] durations, Instant startTime) throws IOException {
        List<GridDataset> datasets = getDatasets("conus", "wx"); // TODO move to parameters
        try (SimpleGridDataSource ds = new SimpleGridDataSource(datasets)) {
            Map<Instant, NDFDWeatherSection> weatherSections = NDFDWeatherSection.loadWeatherSections(datasets);
            Instant dataStartTime = ds.getDataStartTime();

            Instant currentTime = startTime;
            CoordinateSequence sequence = route.getCoordinateSequence();
            float[] data = new float[sequence.size()];
            Arrays.fill(data, Float.NaN);

            int i = 0;
            // Skip points that are in the past
            for (; i < sequence.size() - 1; i++) {
                if (currentTime.isAfter(dataStartTime)) {
                    break;
                }
                currentTime = currentTime.plusMillis(durations[i]);
            }

            TimeSliceData timeSlice = ds.getTimeSlice(currentTime);
            if (timeSlice == null) {
                return null; // No data?
            }
            NDFDWeatherSection section = weatherSections.get(timeSlice.getTime());
            for (; i < sequence.size(); i++) {
                if (currentTime.isAfter(timeSlice.getTime())) {
                    // Need next time slice
                    timeSlice = ds.getTimeSlice(currentTime);
                    if (timeSlice == null) {
                        break; // Too far into the future, no more data available
                    }
                    section = weatherSections.get(timeSlice.getTime());
                }
                float value = timeSlice.getFloat(sequence.getY(i), sequence.getX(i));
                if (!Float.isNaN(value)) {
                    SimpleWeatherType type = SimpleWeatherTable4.getLabel2(section.getSimpleWeatherCode((int) value));
                    data[i] = type.ordinal();
                }
                if (i < sequence.size() - 1) {
                    currentTime = currentTime.plusMillis(durations[i]);
                }
            }
            return new RasterWeatherModel(data, Arrays.stream(SimpleWeatherType.values()).map(SimpleWeatherType::getLabel).toList());
        }
    }

    private List<GridDataset> getDatasets(String area, String dataset) throws IOException {
        Path base = config.getGribStoragePath().resolve("AR." + area);
        List<String> paths;
        try (Stream<Path> stream = Files.walk(base)) {
            paths = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.endsWith("ds." + dataset + ".bin"))
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .toList();
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
        Integer fileCount = s3Client.listObjectsV2(builder -> builder
                        .bucket(config.getNdfdS3Bucket())
                        .prefix("opnl/")
                        .build())
                .thenComposeAsync(response -> downloadObjects(response.contents()))
                .join();
        lastUpdate = Instant.now();
        LOGGER.info("Finished checking S3 Bucket. Downloaded {} files", fileCount);
        if (fileCount != null && fileCount > 0) {
            updateGrids();
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

    private void updateGrids() {
        LOGGER.info("Updating grids");
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory(config.getTemporaryStoragePath(), "tmp-grid");
        } catch (IOException e) {
            LOGGER.error("Unable to create temporary directory", e);
            return;
        }

        int i = 0;
        for (String area : config.getAreas()) {
            for (String dataset : config.getDatasets()) {
                List<GridDataset> datasets = List.of();
                try {
                    datasets = getDatasets(area, dataset);
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
        // TODO notify geoserver?
    }
}
