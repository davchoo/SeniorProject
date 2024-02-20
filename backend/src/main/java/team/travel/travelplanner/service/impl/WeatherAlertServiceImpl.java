package team.travel.travelplanner.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import team.travel.travelplanner.config.WeatherAlertConfig;
import team.travel.travelplanner.entity.WeatherAlert;
import team.travel.travelplanner.model.geojson.Feature;
import team.travel.travelplanner.model.geojson.FeatureCollection;
import team.travel.travelplanner.model.geojson.GeoJSONObject;
import team.travel.travelplanner.model.weather.AlertModel;
import team.travel.travelplanner.repository.WeatherAlertRepository;
import team.travel.travelplanner.service.WeatherAlertService;

@Service
public class WeatherAlertServiceImpl implements WeatherAlertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherAlertServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    private final TaskScheduler taskScheduler;

    private final WeatherAlertConfig weatherAlertConfig;

    private final WeatherAlertRepository weatherAlertRepository;

    public WeatherAlertServiceImpl(ObjectMapper objectMapper, RestClient.Builder restClientBuilder, TaskScheduler taskScheduler, WeatherAlertConfig weatherAlertConfig, WeatherAlertRepository weatherAlertRepository) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.build();
        this.taskScheduler = taskScheduler;
        this.weatherAlertConfig = weatherAlertConfig;
        this.weatherAlertRepository = weatherAlertRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (weatherAlertConfig.isPullingEnabled()) {
            taskScheduler.scheduleAtFixedRate(this::pullWeatherAlerts, weatherAlertConfig.getPullPeriod());
        }
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
        for (Feature feature : featureCollection.features()) {
            AlertModel alertModel = objectMapper.convertValue(feature.properties(), AlertModel.class);
            WeatherAlert alert = new WeatherAlert();
            BeanUtils.copyProperties(alertModel, alert);
            alert.setGeometry(feature.geometry());
            weatherAlertRepository.save(alert);
        }
        long newCount = weatherAlertRepository.count();
        LOGGER.info("Pulled {} new alerts from the NWS", newCount - initialCount);
    }
}
