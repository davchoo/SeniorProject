import React, { useCallback, useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, Marker, MarkerF, PolylineF, InfoWindowF } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';
import axios from "axios";
import { GasStationsMarkers } from '../pages/Gas';
import ReactStars from "react-rating-stars-component";
import wxColorMap from "../pages/wxColorMap"
import { haversineDistance } from '../utils/Distance';
import { weatherApi } from '../pages/Weather';

const libraries = ['places'];
const mapContainerStyle = {
  width: 'calc(100% - 8px)',
  height: 'calc(100% - 8px)',
  border: '4px solid white',
  borderRadius: '8px',
};
const mapOptions = {
  gestureHandling: "greedy"
}

const center = {
  lat: 39.8283, // Latitude of the center of the USA
  lng: -98.5795, // Longitude of the center of the USA
};

const Map = ({ data, setPolyline, setStartAddress, setEndAddress, setPlanDistance, setPlanDuration, setDistanceBetweenStops, forecastedRoute, setWeatherAlerts, chosenTime, children }) => {
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries,
  });

  const [origin, setOrigin] = useState(null);
  const [destination, setDestination] = useState(null);
  const [hqPath, setHQPath] = useState();
  const [path, setPath] = useState(null);
  const [directions, setDirections] = useState(null);
  const [infoWindow, setInfoWindow] = useState(null);
  const [distance, setDistance] = useState(null);
  const [duration, setDuration] = useState(null);
  const [selectedGasMarker, setSelectedGasMarker] = useState(null);

  const [selectedWeatherMarker, setSelectedWeatherMarker] = useState();
  const [segments, setSegments] = useState([]);

  const [durations, setDurations] = useState();
  const [rasterResponse, setRasterResponse] = useState(null);

  const [alerts, setAlerts] = useState([])
  const [mapAlerts, setMapAlerts] = useState([])

  const [map, setMap] = useState()

  const handlePlaceSelect = (selectedPlace, isOrigin) => {
    if (selectedPlace && selectedPlace.geometry && selectedPlace.geometry.location) {
      const lat = selectedPlace.geometry.location.lat();
      const lng = selectedPlace.geometry.location.lng();
      if (isOrigin) {
        setOrigin({ lat, lng });
        setStartAddress(selectedPlace.formatted_address)
      } else {
        setDestination({ lat, lng });
        setEndAddress(selectedPlace.formatted_address)
      }
    } else {
      console.error('Invalid place object:', selectedPlace);
      if (isOrigin) {
        setOrigin(null);
      } else {
        setDestination(null); 
      }
    }
  };

  const handleMapMouseMove = useCallback(({ latLng }) => {
    if (!forecastedRoute) {
      setSelectedWeatherMarker()
      return
    }
    if (mapAlerts) {
      // Find the closest point with an exhaustive search
      let closestAlert;
      let closestDistance = 1e6;
      for (let alert of mapAlerts) {
        // TODO handle wraparound for longitude
        let distance = Math.pow(latLng.lat() - alert.segment[0].lat(), 2) + Math.pow(latLng.lng() - alert.segment[0].lng(), 2)
        if (distance < closestDistance) {
          closestAlert = alert
          closestDistance = distance
        }
      }

      if (closestAlert) {
        let alertPoint = map.getProjection().fromLatLngToPoint(closestAlert.segment[0])
        let mousePoint = map.getProjection().fromLatLngToPoint(latLng)
        let distance = Math.pow(alertPoint.x - mousePoint.x, 2) + Math.pow(alertPoint.y - mousePoint.y, 2)
        let scale = 1 << map.getZoom()
        let scaledDistance = distance * scale
        if (scaledDistance > 1.5) {
          closestAlert = null
        }
      }
      setSelectedWeatherMarker(closestAlert)
    }
  }, [map, mapAlerts, forecastedRoute])

  useEffect(() => {
    setDirections(null);
    setHQPath(null);
    setPath(null);
    setDurations(null);
    if (origin && destination) {
      const directionsService = new window.google.maps.DirectionsService();
      directionsService.route(
        {
          origin: new window.google.maps.LatLng(origin.lat, origin.lng),
          destination: new window.google.maps.LatLng(destination.lat, destination.lng),

          waypoints: data?.map(gasStation => ({
            location: {
              lat: gasStation.location.lat,
              lng: gasStation.location.lng
            },
            stopover: true,
          })),
          travelMode: 'DRIVING',
        },
        (result, status) => {
          if (status === 'OK') {
            setDirections(result);

            let fullRoute = getFullRoute(result.routes[0]);
            setHQPath(fullRoute.coordinates);

            let { coordinates, durations } = decimate(fullRoute);
            setPath(coordinates);
            setDurations(durations);
          } else {
            console.error('Failed to fetch directions. Status: ', status);
          }
        }
      );
    }
  }, [origin, destination, JSON.stringify(data)]);

  useEffect(() => {
    if (!path) {
      setPolyline(null)
      setSegments([])
      setMapAlerts([])
      return;
    }
    setPolyline(window.google.maps.geometry.encoding.encodePath(path))
    setSegments(getPolylineSegments(path))
    setMapAlerts([])
  }, [path])

  useEffect(() => {
    let distances = [];
    let duration = 0;
    let totalDistance = 0;
    if (directions) {
      directions.routes[0].legs.forEach(leg => {
        // Collect distance and duration for each leg
        distances.push(leg.distance.text);
        duration += (leg.duration.value / 3600);
        totalDistance += (leg.distance.value / 1609.34) // Meters to miles
      });
    }
    
    setDistanceBetweenStops(distances);

    setDistance(totalDistance);
    setPlanDistance(totalDistance)
  
    setDuration(duration);
    setPlanDuration(duration)
  }, [directions])

  useEffect(() => {
    if (!map || !directions) {
      return
    }
    map.fitBounds(directions.routes[0].bounds)
  }, [map, directions])

  useEffect(() => {
    if (!window.google || !path || !durations) {
      return
    }
  
    const date = chosenTime ? chosenTime : new Date(); // set this from emma/kat calendar
    const polyline = window.google.maps.geometry.encoding.encodePath(path);

    let controller = new AbortController()
    axios.post('/api/weather/raster/check_route', {
      polyline: polyline,
      durations,
      startTime: date
    }, { signal: controller?.signal })
      .then(response => setRasterResponse(response.data))
      .catch(error => {
        // Handle error
        if (!axios.isCancel(error)) {
          console.error('Error fetching data:', error);
        }
      });
    return () => controller.abort()
  }, [path, durations, chosenTime]);
  
  useEffect(() => {
    if (!window.google || !path || !durations) {
      return
    }
    const date = chosenTime ? chosenTime : new Date(); // set this from emma/kat calendar
    const polyline = window.google.maps.geometry.encoding.encodePath(path);
    const getAlerts = async () => {
      try {
        const result = await weatherApi.checkRouteAlerts(polyline, durations, date);
        setAlerts(result.alerts);
        setWeatherAlerts(result.alerts);
        setMapAlerts(createMapAlerts(segments, result.segmentAlerts));
        setSelectedWeatherMarker()
        return result;
      } catch (error) {
        // Handle error
        console.error('Error fetching alerts:', error);
      }
    };

    // Initial fetch
    getAlerts();
  
    // Fetch data every 5 minutes
    const interval = setInterval(() => {
      getAlerts()
    }, 5 * 60 * 1000);
  
    // Cleanup function
    return () => clearInterval(interval);
  }, [path, durations, segments, chosenTime]);
  

  const handleGasMarkerClick = (gasStation) => {
    setSelectedGasMarker(gasStation);
  };

  if (loadError) {
    return <div>Error Loading Maps</div>;
  }

  if (!isLoaded) {
    return <div>Loading Maps</div>;
  }

  return (
    <div className='flex flex-col flex-grow'>
      <div className='flex flex-row mb-2 p-4 bg-white rounded-[8px]'>
        <div className='w-1/3'>
          <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, true)} label="Enter Origin:" />
          <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, false)} label="Enter Destination:" />
        </div>
        {distance && duration ? (
          <div className='ml-4'>
            <div>Distance: {`${Math.round(distance)} miles` }</div>
            <div>Duration: {`${Math.floor(duration)} hours and ${Math.round((duration - Math.floor(duration)) * 60)} minutes`}</div>
          </div>
        ) : null}
      </div>
      <div className='grow'>
        <GoogleMap
          mapContainerStyle={mapContainerStyle}
          zoom={4.6}
          center={center}
          options={mapOptions}
          onLoad={setMap}
          onMouseMove={handleMapMouseMove}
        >
          {origin != null && (
            <MarkerF
              position={{
                lat: origin.lat,
                lng: origin.lng,
              }}
              label={{
                text: 'A',
                color: 'black',
                fontSize: '17px',
                fontWeight: 'bold',
              }}
              onClick={() => setInfoWindow({ type: 'origin', location: origin })}
            />
          )}
          {infoWindow && infoWindow.type === 'origin' && (
            <InfoWindowF
              position={{
                lat: infoWindow.location.lat,
                lng: infoWindow.location.lng,
              }}
              onCloseClick={() => setInfoWindow(null)}
            >
              <div style={{ padding: '5px', maxWidth: '150px' }}>
                <p style={{ margin: '0' }}>Origin Location</p>
                <p style={{ margin: '0', fontSize: '12px' }}></p>
              </div>
            </InfoWindowF>
          )}

          {destination != null && (
            <MarkerF
              position={{
                lat: destination.lat,
                lng: destination.lng,
              }}
              label={{
                text: 'B',
                color: 'black',
                fontSize: '17px',
                fontWeight: 'bold',
              }}
              onClick={() => setInfoWindow({ type: 'destination', location: destination })}
            />
          )}
          {infoWindow && infoWindow.type === 'destination' && (
            <InfoWindowF
              position={{
                lat: infoWindow.location.lat,
                lng: infoWindow.location.lng,
              }}
              onCloseClick={() => setInfoWindow(null)}
            >
              <div style={{ padding: '5px', maxWidth: '150px' }}>
                <p style={{ margin: '0' }}>Destination Location</p>
                <p style={{ margin: '0', fontSize: '12px' }}></p>
              </div>
            </InfoWindowF>
          )}

          {forecastedRoute && rasterResponse && segments && (segments.map((point, index) => (
            <PolylineF
              key={index}
              path={point}
              options={{
                strokeColor: getColor(rasterResponse, index),
                strokeOpacity: 1.0,
                strokeWeight: 3
              }}
            />
          )))}
          {hqPath && !forecastedRoute && (
            <PolylineF
              path={hqPath}
              options={{
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 3
              }}
            />
          )}
          {data != null && (
            <GasStationsMarkers
              gasStations={data}
              onClick={handleGasMarkerClick}
            />
          )}

          {rasterResponse && selectedWeatherMarker && (
            <InfoWindowF
              position={selectedWeatherMarker.segment[0]}
              onCloseClick={() => setSelectedWeatherMarker()}
            >

              <div style={{ maxHeight: '250px', overflowY: 'auto', maxWidth: '250px' }}>
                <h3 style={{ textAlign: 'center' }}> 🚨Weather Alert🚨</h3>
                <p style={{ textAlign: 'center' }}>ETA: {calculateDurationUpToPoint(durations, selectedWeatherMarker.segmentIndex, chosenTime)}</p>
                <p style={{ textAlign: 'center' }}>Weather: {rasterResponse.labels[rasterResponse.data[selectedWeatherMarker.segmentIndex]]}</p>
                <p style={{ textAlign: 'center' }}>{alerts[selectedWeatherMarker.alertIndex]?.headline}</p>
                <p style={{ textAlign: 'center' }}>{alerts[selectedWeatherMarker.alertIndex]?.description}</p>
              </div>

            </InfoWindowF>
          )}
          {selectedGasMarker && (
            <InfoWindowF
              position={{
                lat: selectedGasMarker.location.lat,
                lng: selectedGasMarker.location.lng,
              }}
              onCloseClick={() => setSelectedGasMarker(null)}
            >
              <div style={{ maxHeight: '150px', overflowY: 'auto', maxWidth: '200px' }}>
                <h3 style={{ textAlign: 'center' }}>{selectedGasMarker.name}</h3>
                <hr />
                <p><u>Fuel Price:</u></p>
                <ul>
                  {Object.entries(selectedGasMarker.prices).map(([type, price]) => (
                    <li key={type}>
                      {type}: ${price.toFixed(2)}
                    </li>
                  ))}
                </ul>
                <p><u>Address:</u> {selectedGasMarker.formattedAddress}</p>
                <p><u>Hours:</u></p>
                <ul>
                  {selectedGasMarker.currentOpeningHours?.weekdayDescriptions?.map((description, index) => (
                    <li key={index}>
                      {description}
                    </li>
                  ))}
                </ul>
                <p><u>Rating:</u> <ReactStars value={selectedGasMarker.rating} count={5} activeColor="#ffd700" size={24} edit={false} /></p>
                <p><u>Reviews:</u></p>
                <div style={{ maxHeight: '100px', overflowY: 'auto' }}>
                  {selectedGasMarker.reviews && (
                    <ul>
                      {selectedGasMarker.reviews.map((review, index) => (

                        <li key={index}>
                          <p>Rating {index}:<ReactStars value={selectedGasMarker.rating} count={5} activeColor="#ffd700" size={12} edit={false} /></p>
                          <p>{review.text.text}</p>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            </InfoWindowF>
          )}
  
          {children}
        </GoogleMap>
      </div>
    </div>
  );
};

/**
 * @param {google.maps.DirectionsRoute} route 
 * @returns {coordinates: [LatLng, ...], durations: [ms between coordinate[i] and coordinate[i+1], ...]}
 */
function getFullRoute(route) {
  let coordinates = []
  let durations = []
  for (let leg of route.legs) {
    for (let step of leg.steps) {
      let distances = []
      let totalDistance = 0
      for (let i = 0; i < step.path.length - 1; i++) {
        let distance = haversineDistance(step.path[i], step.path[i + 1])
        distances.push(distance)
        totalDistance += distance
      }

      let totalDuration = 0 // Totally a sane default
      if (step.duration) { // duration can be null...when would this happen?
        totalDuration = step.duration.value * 1000 // seconds to milliseconds
      }
      for (let i = 0; i < step.path.length - 1; i++) {
        let duration = Math.round(totalDuration * distances[i] / totalDistance)

        coordinates.push(step.path[i])
        durations.push(duration)
      }
    }
  }
  return { coordinates, durations } // TODO too many coordinates and durations! Have to simplify?
}

function decimate({ coordinates, durations }) {
  const maxDistance = 2.5 // km
  let newCoordinates = [coordinates[0]]
  let newDurations = []
  let currentDuration = durations[0]
  let currentTotalDistance = 0

  for (let i = 1; i < coordinates.length - 1; i++) {
    let distance = haversineDistance(coordinates[i - 1], coordinates[i])
    if (currentTotalDistance + distance >= maxDistance) {
      newCoordinates.push(coordinates[i - 1])
      newDurations.push(currentDuration)

      currentDuration = 0
      currentTotalDistance = 0
    }
    currentDuration += durations[i]
    currentTotalDistance += distance
  }
  newCoordinates.push(coordinates[coordinates.length - 1])
  newDurations.push(currentDuration)
  return { coordinates: newCoordinates, durations: newDurations }
}

const getPolylineSegments = (path) => {
  const segments = [];
  for (let i = 0; i < path.length - 1; i++) {
    const segment = [path[i], path[i + 1]];
    segments.push(segment);
  }
  return segments;
};

const getColor = (rasterResponse, index) => {
  const color = wxColorMap[rasterResponse.labels[rasterResponse.data[index]]] // TODO handle other datasets
  return color;
}

const createMapAlerts = (segments, segmentAlerts) => {

  if (segments && segments.length > 0 && segmentAlerts && segmentAlerts.length > 0) {
    const comparePairs = (a, b) => {
      // Compare the second number of each pair
      const secondNumberA = segmentAlerts[a * 2 + 1];
      const secondNumberB = segmentAlerts[b * 2 + 1];
      return secondNumberA - secondNumberB;
    };

    const segmentAlertLocations = []
    for (let i = 0; i < segmentAlerts.length; i += 2) {
      const segmentIndex = segmentAlerts[i];
      const alertIndex = segmentAlerts[i + 1];
      const segment = segments[segmentIndex];
      segmentAlertLocations.push({segment, alertIndex, segmentIndex});

      // Leaving this code in case we want to have an alert at the 'mean' location
      // console.log(sortedPairs[i + 1])
      // if (sortedPairs[i + 1] === alertIndex) {
      //   total += sortedPairs[i];
      //   count++;
      // }
      // else {
      //   let mean = Math.ceil(total / count);
      //   segmentAlertLocations.push(segments[mean]);
      //   count = total = 0;
      //   alertIndex = sortedPairs[i + 1];
      // }
    }
    // let mean = Math.ceil(total / count); //
    // segmentAlertLocations.push(segments[mean]);
    return segmentAlertLocations;
  }

}

const calculateDurationUpToPoint = (durations, index, chosenTime) => {
  if (!durations || durations.length <= index) {
    return null;
  }
  let totalTime = 0;
  for (let i = 0; i < index; i++) {
    totalTime += durations[i];
  }

  const date = new Date()

  return chosenTime ? new Date(chosenTime.getTime() + totalTime).toLocaleString() : new Date(date.getTime() + totalTime).toLocaleString();

}


export default Map;
