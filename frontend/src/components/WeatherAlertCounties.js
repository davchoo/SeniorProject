import { useContext, useEffect, useState } from "react";
import { decodeWKB64 } from "../utils/wkb64";
import { weatherApi } from "../pages/Weather";
import { MapContext } from "@react-google-maps/api";

const createFeature = (geometry, properties) => {
  return {
    type: "Feature",
    geometry,
    properties
  };
};

const createFeatureCollection = (features) => {
  return {
    type: "FeatureCollection",
    features
  };
};

export default function WeatherAlertCounties({ alerts }) {
  const map = useContext(MapContext);

  const [alertGeometries, setAlertGeometries] = useState([]);
  const [alertCounties, setAlertCounties] = useState([]);

  const [uniqueCounties, setUniqueCounties] = useState(new Set());
  const [cachedCounties, setCachedCounties] = useState({});

  useEffect(() => {
    let newAlertGeometries = [];
    let newAlertCounties = [];
    let newRequiredCounties = new Set();
    for (let alert of alerts) {
      if (alert.geometry) {
        newAlertGeometries.push({
          alert,
          feature: createFeature(decodeWKB64(alert.geometry), {alertId: alert.id}),
        });
      } else {
        for (let fips of alert.geocodeSAME) {
          newAlertCounties.push({ alert, fips });
          newRequiredCounties.add(fips);
        }
      }
    }

    setAlertGeometries(newAlertGeometries);
    setAlertCounties(newAlertCounties);
    setUniqueCounties(newRequiredCounties);
  }, [alerts]);

  useEffect(() => {
    let newCachedCounties = { ...cachedCounties };
    // Remove counties we don't need anymore
    for (let fips in newCachedCounties) {
      if (!uniqueCounties.has(fips)) {
        delete newCachedCounties[fips];
      }
    }
    setCachedCounties(newCachedCounties);
    // Find all of the counties we're missing
    let missingCounties = [];
    for (let fips of uniqueCounties) {
      if (!newCachedCounties[fips]) {
        missingCounties.push(fips);
      }
    }
    if (missingCounties.length == 0) {
      return;
    }
    let controller = new AbortController();
    weatherApi
      .getCounties(missingCounties.join(), controller.signal)
      .then((counties) => {
        for (let fips in counties) {
          let features = counties[fips].map(model => createFeature(
            decodeWKB64(model.geometry),
            {fips}
          ))
          counties[fips] = createFeatureCollection(features)
        }
        setCachedCounties((prevCounties) => {
          return { ...prevCounties, ...counties };
        });
      })
      .catch(console.log);
    return () => controller.abort();
  }, [uniqueCounties]);

  useEffect(() => {
    if (typeof map !== "object") {
      return; // For some odd reason map can be an integer
    }
    window.theMap = map
    map.data.setStyle((feature) => {
      return {
        clickable: false,
        zIndex: -10000,
        fillOpacity: 0.2,
        strokeOpacity: 0.2,
        strokeWeight: 1
      }
    })
  }, [map]);

  return (
    <>
      {alertGeometries.map(({ alert, feature }) => (
        <GeoJsonGeometry key={alert.id} geoJSONFeature={feature} />
      ))}

      {Array.from(uniqueCounties).map(
        (fips) =>
          cachedCounties[fips] && (
            <GeoJsonGeometry
              key={fips}
              geoJSONFeature={cachedCounties[fips]}
            />
          )
      )}
    </>
  );
}

const GeoJsonGeometry = ({ geoJSONFeature }) => {
  const map = useContext(MapContext);
  const [features, setFeatures] = useState([]);

  useEffect(() => {
    if (typeof map !== "object") {
      return; // For some odd reason map can be an integer
    }
    let newFeatures = map.data.addGeoJson(geoJSONFeature);
    setFeatures(newFeatures);
    return () => {
      newFeatures.forEach((feature) => map.data.remove(feature))
    };
  }, [map, geoJSONFeature]);
};
