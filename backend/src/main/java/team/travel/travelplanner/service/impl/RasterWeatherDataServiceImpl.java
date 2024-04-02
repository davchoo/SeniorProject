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
        List<GridDataset> datasets = getDatasets("conus", "wx");
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
        Path base = config.getStoragePath().resolve("AR." + area);
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
        if (!Files.isDirectory(config.getStoragePath())) {
            // No files downloaded, set to epoch to make sure files are downloaded regardless of modification time
            lastUpdate = Instant.EPOCH;
            return;
        }
        try (Stream<Path> stream = Files.walk(config.getStoragePath())) {
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
        AtomicInteger fileCount = new AtomicInteger();
        s3Client.listObjectsV2(builder -> builder
                        .bucket(config.getNdfdS3Bucket())
                        .prefix("opnl")
                        .build())
                .thenComposeAsync(response -> downloadObjects(response.contents(), fileCount))
                .join();
        lastUpdate = Instant.now();
        LOGGER.info("Finished checking S3 Bucket. Downloaded {} files", fileCount.get());
    }

    private CompletableFuture<Void> downloadObjects(List<S3Object> objects, AtomicInteger fileCount) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (S3Object object : objects) {
            if (!shouldDownload(object)) {
                continue;
            }
            Path destination = getDestination(object.key());
            try {
                Files.createDirectories(destination.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DownloadFileRequest request = DownloadFileRequest.builder()
                    .getObjectRequest(builder -> builder
                            .bucket(config.getNdfdS3Bucket())
                            .key(object.key()))
                    .destination(destination)
                    .build();
            futures.add(transferManager.downloadFile(request)
                    .completionFuture()
                    .exceptionally(throwable -> {
                        LOGGER.warn("Failed to download {}", object.key(), throwable);
                        return null;
                    })
                    .thenAccept(x -> fileCount.incrementAndGet()));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private Path getDestination(String key) {
        if (!key.startsWith("opnl/")) {
            throw new IllegalArgumentException("Expected key to start with 'opnl/' Key: " + key);
        }
        String relPath = key.substring("opnl/".length());
        return config.getStoragePath().resolve(relPath);
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
}
