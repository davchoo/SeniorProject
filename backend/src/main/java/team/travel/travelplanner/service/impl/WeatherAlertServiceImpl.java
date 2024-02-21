package team.travel.travelplanner.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;
import team.travel.travelplanner.config.WeatherAlertConfig;
import team.travel.travelplanner.entity.WeatherAlert;
import team.travel.travelplanner.model.geojson.Feature;
import team.travel.travelplanner.model.geojson.FeatureCollection;
import team.travel.travelplanner.model.geojson.GeoJSONObject;
import team.travel.travelplanner.model.weather.AlertModel;
import team.travel.travelplanner.repository.WeatherAlertRepository;
import team.travel.travelplanner.service.WeatherAlertService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WeatherAlertServiceImpl implements WeatherAlertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherAlertServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    private final TaskScheduler taskScheduler;

    private final TransactionTemplate transactionTemplate;

    private final WeatherAlertConfig weatherAlertConfig;

    private final WeatherAlertRepository weatherAlertRepository;

    public WeatherAlertServiceImpl(ObjectMapper objectMapper, RestClient.Builder restClientBuilder, PlatformTransactionManager platformTransactionManager, TaskScheduler taskScheduler, WeatherAlertConfig weatherAlertConfig, WeatherAlertRepository weatherAlertRepository) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.build();
        this.taskScheduler = taskScheduler;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.weatherAlertConfig = weatherAlertConfig;
        this.weatherAlertRepository = weatherAlertRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!weatherAlertConfig.isPullingEnabled()) {
            return;
        }
        taskScheduler.scheduleAtFixedRate(() -> transactionTemplate.executeWithoutResult(status -> {
            try {
                pullWeatherAlerts();
            } catch (Exception e) {
                LOGGER.error("Failed to pull NWS alerts", e);
                status.setRollbackOnly();
            }
        }), weatherAlertConfig.getPullPeriod());
    }

    private synchronized void pullWeatherAlerts() {
        LOGGER.info("Pulling weather alerts from the NWS API"); // TODO need to demote to trace?
        GeoJSONObject geoJSONObject = restClient.get()
                .uri(weatherAlertConfig.getNwsAlertEndpoint())
                .retrieve()
                .body(GeoJSONObject.class);
        if (!(geoJSONObject instanceof FeatureCollection featureCollection)) {
            LOGGER.error("Expected FeatureCollection from the NWS alert API. Received {}", geoJSONObject);
            return;
        }
        // TODO extract and unit test?
        long initialCount = weatherAlertRepository.count();
        List<String> outdatedAlertIds = new ArrayList<>();
        // Kinda wasteful, but I'm not sure if there's another way to prevent repository.save() from overwriting
        // existing entries. It helps prevent us from sending redundant data though.
        Set<String> existingIds = weatherAlertRepository.getAllIds();
        for (Feature feature : featureCollection.features()) {
            AlertModel alertModel = objectMapper.convertValue(feature.properties(), AlertModel.class);
            if (existingIds.contains(alertModel.getId())) {
                continue;
            }
            outdatedAlertIds.addAll(alertModel.getReferences());

            WeatherAlert alert = new WeatherAlert();
            BeanUtils.copyProperties(alertModel, alert);
            alert.setGeometry(feature.geometry());
            weatherAlertRepository.save(alert);
        }
        weatherAlertRepository.markOutdated(outdatedAlertIds);
        long afterAddCount = weatherAlertRepository.count();
        // Endpoint keeps expired alerts for 1 week, might as well keep it in the DB
        weatherAlertRepository.deleteAllByExpiresBefore(Instant.now().minus(7, ChronoUnit.DAYS));
        long afterDeleteCount = weatherAlertRepository.count();
        // Counts can be messed up with concurrent changes, but that shouldn't happen (only 1 instance should be pulling)
        LOGGER.info("Pulled {} new alerts from the NWS and deleted {} expired alerts", afterAddCount - initialCount, afterAddCount - afterDeleteCount);
    }
}
