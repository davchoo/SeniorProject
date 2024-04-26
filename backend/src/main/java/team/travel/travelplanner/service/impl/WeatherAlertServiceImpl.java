package team.travel.travelplanner.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
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
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.geojson.Feature;
import team.travel.travelplanner.model.geojson.FeatureCollection;
import team.travel.travelplanner.model.geojson.GeoJSONObject;
import team.travel.travelplanner.model.weather.RouteWeatherAlertsModel;
import team.travel.travelplanner.model.weather.SegmentWeatherAlertModel;
import team.travel.travelplanner.model.weather.WeatherAlertModel;
import team.travel.travelplanner.repository.WeatherAlertRepository;
import team.travel.travelplanner.service.WeatherAlertService;

import java.time.Instant;
import java.util.*;

@Service
public class WeatherAlertServiceImpl implements WeatherAlertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherAlertServiceImpl.class);

    private final GeometryFactory geometryFactory;

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    private final TaskScheduler taskScheduler;

    private final TransactionTemplate transactionTemplate;

    private final WeatherAlertConfig weatherAlertConfig;

    private final WeatherAlertRepository weatherAlertRepository;

    public WeatherAlertServiceImpl(GeometryFactory geometryFactory, ObjectMapper objectMapper,
                                   RestClient.Builder restClientBuilder,
                                   PlatformTransactionManager platformTransactionManager,
                                   TaskScheduler taskScheduler, WeatherAlertConfig weatherAlertConfig,
                                   WeatherAlertRepository weatherAlertRepository) {
        this.geometryFactory = geometryFactory;
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
        loadWeatherAlerts(featureCollection);
    }

    public void loadWeatherAlerts(FeatureCollection featureCollection) {
        long initialCount = weatherAlertRepository.count();
        List<String> outdatedAlertIds = new ArrayList<>();
        // Kinda wasteful, but I'm not sure if there's another way to prevent repository.save() from overwriting
        // existing entries. It helps prevent us from sending redundant data though.
        Set<String> existingIds = weatherAlertRepository.getAllIds();
        for (Feature feature : featureCollection.features()) {
            WeatherAlertModel alertModel = objectMapper.convertValue(feature.properties(), WeatherAlertModel.class);
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
        // Keep a history of alerts past their expiry
        weatherAlertRepository.deleteAllByExpiresBefore(Instant.now().minus(weatherAlertConfig.getHistoryLength()));
        long afterDeleteCount = weatherAlertRepository.count();
        // Counts can be messed up with concurrent changes, but that shouldn't happen (only 1 instance should be pulling)
        LOGGER.info("Loaded {} new alerts from the NWS and deleted {} expired alerts", afterAddCount - initialCount, afterAddCount - afterDeleteCount);
    }

    @Override
    public RouteWeatherAlertsModel checkRouteWeatherAlerts(RouteModel route) {
        Geometry geometry = route.geometry(geometryFactory);
        List<SegmentWeatherAlertModel> segmentAlerts = weatherAlertRepository.checkRouteWeatherAlerts(geometry, route.durations(), route.startTime());

        Map<String, Integer> alertIndex = new HashMap<>();
        int[] packedSegmentAlerts = new int[2 * segmentAlerts.size()];
        for (int i = 0; i < segmentAlerts.size(); i++) {
            SegmentWeatherAlertModel model = segmentAlerts.get(i);
            packedSegmentAlerts[2 * i] = model.segmentId();
            packedSegmentAlerts[2 * i + 1] = alertIndex.computeIfAbsent(model.alertId(), k -> alertIndex.size());
        }

        List<WeatherAlert> alerts = weatherAlertRepository.findAllById(alertIndex.keySet());
        WeatherAlertModel[] alertModels = new WeatherAlertModel[alerts.size()];
        for (WeatherAlert alert : alerts) {
            int index = alertIndex.get(alert.getId());
            alertModels[index] = WeatherAlertModel.from(alert);
        }
        return new RouteWeatherAlertsModel(packedSegmentAlerts, List.of(alertModels));
    }
}
