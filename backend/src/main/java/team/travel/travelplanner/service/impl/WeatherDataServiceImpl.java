package team.travel.travelplanner.service.impl;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.Name;
import org.geotools.api.referencing.ReferenceIdentifier;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.impl.WFSDataAccessFactory;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.crs.AbstractSingleCRS;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import team.travel.travelplanner.config.WeatherDataConfig;
import team.travel.travelplanner.entity.WeatherForecastFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.repository.WeatherForecastFeatureRepository;
import team.travel.travelplanner.service.WeatherDataService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Service
public class WeatherDataServiceImpl implements WeatherDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherDataServiceImpl.class);

    private static final ZoneId EST = ZoneId.of("-05:00");

    private static final int WGS84_SRID = 4326;

    private final WeatherDataConfig weatherDataConfig;

    private final WeatherForecastFeatureRepository weatherForecastFeatureRepository;

    private final TaskScheduler taskScheduler;

    private final TransactionTemplate transactionTemplate;

    private boolean completedLastUpdate = false;

    public WeatherDataServiceImpl(WeatherDataConfig weatherDataConfig, WeatherForecastFeatureRepository weatherForecastFeatureRepository, TaskScheduler taskScheduler, PlatformTransactionManager platformTransactionManager) {
        this.weatherDataConfig = weatherDataConfig;
        this.weatherForecastFeatureRepository = weatherForecastFeatureRepository;
        this.taskScheduler = taskScheduler;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);

        if (!weatherDataConfig.isAutoUpdate()) {
            LOGGER.info("Auto update is disabled.");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        taskScheduler.schedule(this::checkForUpdates, Instant.now().plusSeconds(2));
    }

    @Scheduled(cron = "@hourly")
    private synchronized void checkForUpdates() {
        if (!weatherDataConfig.isAutoUpdate()) {
            return;
        }
        if (completedLastUpdate) {
            ZonedDateTime latestFileDate = weatherForecastFeatureRepository.findLatestFileDate();
            if (latestFileDate != null) {
                ZonedDateTime now = Instant.now().atZone(EST);
                if (latestFileDate.toLocalDate().equals(now.toLocalDate())) {
                    // Still the same day
                    if (latestFileDate.getHour() < 12 && now.getHour() < 12) {
                        // Acquired morning issuance, wait until the afternoon
                        return;
                    }
                    if (latestFileDate.getHour() >= 12 && now.getHour() >= 12) {
                        // Acquired afternoon issuance, wait until the next morning
                        return;
                    }
                }
                LOGGER.info("Checking National Weather Forecast Chart for updates. The latest file stored was dated: {}", latestFileDate);
            }
        } else {
            LOGGER.info("Checking National Weather Forecast Chart for updates. Application has just started or the last update was not complete.");
        }
        completedLastUpdate = false;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    updateNow();
                } catch (IOException e) {
                    LOGGER.error("Failed to fetch National Weather Forecast Chart via WFS", e);
                    status.setRollbackOnly();
                }
            }
        });
    }

    private Map<String, Object> createWFSParams() {
        return Map.of(
                WFSDataAccessFactory.URL.key, weatherDataConfig.getNationalWeatherForecastWfcUrl(),
                WFSDataAccessFactory.LENIENT.key, true,
                WFSDataAccessFactory.SCHEMA_CACHE_LOCATION.key, weatherDataConfig.getSchemaCachePath().toAbsolutePath().toString(),
                WFSDataAccessFactory.PROTOCOL.key, false,
                WFSDataAccessFactory.TIMEOUT.key, (int) weatherDataConfig.getTimeout().toMillis()
        );
    }

    private void updateNow() throws IOException {
        long initialCount = weatherForecastFeatureRepository.count();
        WFSDataStore dataStore = new WFSDataStoreFactory()
                .createDataStore(createWFSParams());
        for (Name name : dataStore.getNames()) {
            String typeName = name.getLocalPart();
            int separatorIdx = typeName.indexOf(":");
            if (separatorIdx != -1) {
                // Remove namespace prefix
                typeName = typeName.substring(separatorIdx + 1);
            }
            String[] parts = typeName.split("_", 3);
            if (parts.length != 3 || !parts[0].equals("Day")) {
                LOGGER.warn("National Weather Forecast Chart type name is in unexpected format. {}", name);
                continue;
            }
            int day = Integer.parseInt(parts[1]);
            WeatherFeatureType featureType = parseFeatureType(parts[2]);
            if (featureType == null) {
                // Skip unknown feature types
                continue;
            }

            SimpleFeatureSource featureSource = dataStore.getFeatureSource(name);
            SimpleFeatureCollection featureCollection = featureSource.getFeatures();

            SimpleFeatureIterator iterator = featureCollection.features();
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                saveFeature(day, featureType, feature);
            }
            iterator.close();
        }

        dataStore.dispose();
        completedLastUpdate = true;
        weatherForecastFeatureRepository.deduplicate();

        long newCount = weatherForecastFeatureRepository.count();
        LOGGER.info("Fetched National Weather Forecast Chart via WFS and received {} new features", newCount - initialCount);
    }

    private void saveFeature(int day, WeatherFeatureType featureType, SimpleFeature feature) {
        WeatherForecastFeature weatherForecastFeature = new WeatherForecastFeature();
        weatherForecastFeature.setDay(day);
        weatherForecastFeature.setPopUpContent((String) feature.getAttribute("popupConte"));
        weatherForecastFeature.setWeatherFeatureType(featureType);

        Timestamp rawFileDate = (Timestamp) feature.getAttribute("idp_filedate");
        ZonedDateTime fileDate = rawFileDate.toLocalDateTime().atZone(EST); // TODO check if file date is affected by DST
        weatherForecastFeature.setFileDate(fileDate);

        // https://www.wpc.ncep.noaa.gov/html/about_Gudes.shtml
        if (day == 1) {
            // Day 1 is issued by 5 am EST and 5 pm EST
            if (fileDate.getHour() < 12) {
                // Morning issuance is valid for 24 hours starting at 7 am EST
                ZonedDateTime validStart = fileDate.toLocalDate()
                        .atTime(7, 0)
                        .atZone(EST);
                weatherForecastFeature.setValidStart(validStart);
                weatherForecastFeature.setValidEnd(validStart.plusHours(24));
            } else {
                // Afternoon issuance is valid for 12 hours starting at 7 pm EST
                ZonedDateTime validStart = fileDate.toLocalDate()
                        .atTime(19, 0)
                        .atZone(EST);
                weatherForecastFeature.setValidStart(validStart);
                weatherForecastFeature.setValidEnd(validStart.plusHours(12));
            }
        } else {
            // Day 2 and 3 are issued by 5 am EST and are valid for 24 hours
            ZonedDateTime validStart = fileDate.toLocalDate()
                    .plusDays(day - 1)
                    .atTime(7, 0).atZone(EST);
            weatherForecastFeature.setValidStart(validStart);
            weatherForecastFeature.setValidEnd(validStart.plusHours(24));
        }

        Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
        // HACK: For some reason the SRID is not set for geometries from WFSFeatureSource, but it is included in the userData
        if (geometry.getUserData() instanceof AbstractSingleCRS crs) {
            ReferenceIdentifier identifier = crs.getIdentifier(Citations.EPSG);
            if (identifier != null) {
                geometry.setSRID(Integer.parseInt(identifier.getCode()));
            }
        }
        if (geometry.getSRID() == 0) {
            LOGGER.warn("Failed to find SRID for feature in Day_{}_{}, assuming {}", day, featureType, WGS84_SRID);
            geometry.setSRID(WGS84_SRID);
        }

        weatherForecastFeature.setGeometry(geometry);

        weatherForecastFeatureRepository.save(weatherForecastFeature);
    }

    private WeatherFeatureType parseFeatureType(String featureTypeStr) {
        return switch (featureTypeStr) {
            case "Critical_Fire_Weather_Possible" -> WeatherFeatureType.CRITICAL_FIRE_WEATHER_POSSIBLE;
            case "Freezing_Rain_Possible" -> WeatherFeatureType.FREEZING_RAIN_POSSIBLE;
            case "Heavy_Rain_Flash_Flooding_Possible" -> WeatherFeatureType.HEAVY_RAIN_FLASH_FLOODING_POSSIBLE;
            case "Heavy_Snow_Possible" -> WeatherFeatureType.HEAVY_SNOW_POSSIBLE;
            case "Mixed_Precipitation" -> WeatherFeatureType.MIXED_PRECIPITATION;
            case "Rain" -> WeatherFeatureType.RAIN;
            case "Rain_Thunderstorms" -> WeatherFeatureType.RAIN_THUNDERSTORMS;
            case "Severe_Thunderstorms_Possible" -> WeatherFeatureType.SEVERE_THUNDERSTORMS_POSSIBLE;
            case "Snow" -> WeatherFeatureType.SNOW;
            default -> null;
        };
    }
}
