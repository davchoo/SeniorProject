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
import org.springframework.stereotype.Service;
import team.travel.travelplanner.config.WeatherDataConfig;
import team.travel.travelplanner.entity.WeatherForecastFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.repository.WeatherForecastFeatureRepository;
import team.travel.travelplanner.service.WeatherDataService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherDataServiceImpl implements WeatherDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherDataServiceImpl.class);

    private static final int WGS84_SRID = 4326;

    private final WeatherDataConfig weatherDataConfig;

    private final WeatherForecastFeatureRepository weatherForecastFeatureRepository;

    public WeatherDataServiceImpl(WeatherDataConfig weatherDataConfig, WeatherForecastFeatureRepository weatherForecastFeatureRepository) {
        this.weatherDataConfig = weatherDataConfig;
        this.weatherForecastFeatureRepository = weatherForecastFeatureRepository;

        update();
    }

    private void update() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(WFSDataAccessFactory.URL.key, weatherDataConfig.getNationalWeatherForecastWfcUrl());
            params.put(WFSDataAccessFactory.LENIENT.key, true);
            params.put(WFSDataAccessFactory.SCHEMA_CACHE_LOCATION.key, weatherDataConfig.getSchemaCachePath().toAbsolutePath().toString());
            params.put(WFSDataAccessFactory.PROTOCOL.key, false);

            WFSDataStore dataStore = new WFSDataStoreFactory().createDataStore(params);

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
        } catch (IOException exception) {
            LOGGER.error("Failed to fetch National Weather Forecast Chart via WFS", exception);
        }
    }

    private void saveFeature(int day, WeatherFeatureType featureType, SimpleFeature feature) {
        WeatherForecastFeature weatherForecastFeature = new WeatherForecastFeature();
        weatherForecastFeature.setDay(day);
        weatherForecastFeature.setPopUpContent((String) feature.getAttribute("popupConte"));

        // TODO properly store the fileDate timestamp
        Timestamp fileDate = (Timestamp) feature.getAttribute("idp_filedate");
        weatherForecastFeature.setFileDate(String.valueOf(fileDate));

        // TODO determine validStart and validEnd based on day, fileDate, and popUpContent

        weatherForecastFeature.setWeatherFeatureType(featureType);

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
