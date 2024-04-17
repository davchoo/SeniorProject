import React, { useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, Marker, PolylineF, InfoWindow } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';
import axios from "axios";
import { GasStationsMarkers } from '../pages/Gas';
import ReactStars from "react-rating-stars-component";
import polyLineData from "../pages/weather.json";
import { mapQPFColor } from '../pages/qpfColorMap';
import wxColorMap from "../pages/wxColorMap"
import { haversineDistance } from '../utils/Distance';

const libraries = ['places'];
const mapContainerStyle = {
  width: '65vw',
  height: '80vh',
  border: '4px solid white',
  borderRadius: '8px',
  marginLeft: 'auto',
};

const center = {
  lat: 39.8283, // Latitude of the center of the USA
  lng: -98.5795, // Longitude of the center of the USA
};

const Map = ({ data, setPolyline, setStartAddress, setEndAddress, toggleWeather }) => {
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries,
  });

  const [origin, setOrigin] = useState(null);
  const [destination, setDestination] = useState(null);
  const [path, setPath] = useState(null);
  const [directions, setDirections] = useState(null);
  const [gasStations, setGasStations] = useState([]);
  const [infoWindow, setInfoWindow] = useState(null);
  const [distance, setDistance] = useState(null);
  const [duration, setDuration] = useState(null);
  const [selectedGasMarker, setSelectedGasMarker] = useState(null);
  const [segments, setSegments] = useState([]);
  const [weatherDisplay, setWeatherDisplay] = useState(true); 

  const [durations, setDurations] = useState();
  const [rasterResponse, setRasterResponse] = useState(null);

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

  useEffect(() => {
    if (origin && destination) {
      const directionsService = new window.google.maps.DirectionsService();
      directionsService.route(
        {
          origin: new window.google.maps.LatLng(origin.lat, origin.lng),
          destination: new window.google.maps.LatLng(destination.lat, destination.lng),
          travelMode: 'DRIVING',
        },
        (result, status) => {
          if (status === 'OK') {
            setDirections(result);

            let { coordinates, durations } = getFullRoute(result.routes[0])
            setPath(coordinates);
            setDurations(durations);
            console.log(coordinates.length)

            setPolyline(window.google.maps.geometry.encoding.encodePath(coordinates));
            setSegments(getPolylineSegments(coordinates))

            setDistance(result.routes[0].legs[0].distance.text);
            setDuration(result.routes[0].legs[0].duration.text);

            setStartAddress(result.routes[0].legs[0].start_address);
            setEndAddress(result.routes[0].legs[0].end_address);
          } else {
            console.error('Failed to fetch directions. Status: ', status);
            setDirections(null);
            setPath(null);
          }
        }
      );
    }
  }, [origin, destination]);

  useEffect(() => {
    if (!window.google || !path || !durations) {
      return
    }
    let controller = new AbortController()
    axios.post('http://localhost:8080/api/weather/raster/check_route',{
      polyline: window.google.maps.geometry.encoding.encodePath(path), // TODO reuse from setPolyline?
      durations,
      startTime: new Date() // TODO route start time?
    }, {signal: controller.signal})
      .then(response => setRasterResponse(response.data), console.log)
    return () => controller.abort()
  }, [path, durations]);

  const handleMarkerClick = (gasStation) => {
    setSelectedGasMarker(gasStation);
  };

  if (loadError) {
    return <div>Error Loading Maps</div>;
  }

  if (!isLoaded) {
    return <div>Loading Maps</div>;
  }

  return (
    <div className='flex-col'>
      <div>
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, true)} label="Enter Origin:" />
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, false)} label="Enter Destination:" />
        <div style={{ marginTop: '15px', marginLeft: '10px' }}>Distance: {distance ? distance.replace('mi', 'miles') : ''}</div>
        <div style={{ marginBottom: '20px', marginLeft: '10px' }}>Duration: {duration ? duration.replace(/\bmin(s?)\b/, 'minute$1').replace(/\bhour(s?)\b/, 'hour$1') : ''}</div>
      </div>

      <GoogleMap
        mapContainerStyle={mapContainerStyle}
        zoom={4.6}
        center={center}
      >
        {origin != null && (
          <Marker
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
          <InfoWindow
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
          </InfoWindow>
        )}

        {destination != null && (
          <Marker
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
          <InfoWindow
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
          </InfoWindow>
        )}

        {path ? (
          weatherDisplay ? (
            rasterResponse && segments && (segments.map((point, index) => (
              <PolylineF
                key={index}
                path={point}
                options={{
                  strokeColor: getColor(rasterResponse, index),
                  strokeOpacity: 1.0,
                  strokeWeight: 3
                }} 
              />
            )))) : 
            <PolylineF
              path={path}
              options={{
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 3
              }}
            />
        ) : null}
        {data != null && (
          <GasStationsMarkers
            gasStations={data}
            onClick={handleMarkerClick}
          />
        )}

        {selectedGasMarker && (
          <InfoWindow
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
          </InfoWindow>
        )}
      </GoogleMap>
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
        let duration = totalDuration * distances[i] / totalDistance

        coordinates.push(step.path[i])
        durations.push(duration)
      }
    }
  }
  return {coordinates, durations} // TODO too many coordinates and durations! Have to decimate?
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


export default Map;
