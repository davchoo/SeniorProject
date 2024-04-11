import React, { useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, Marker, Polyline, InfoWindow } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';
import axios from "axios";
import { GasStationsMarkers } from '../pages/Gas';
import ReactStars from "react-rating-stars-component";


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

const Map = ({ data, setPolyline, setStartAddress, setEndAddress, setPlanDistance, setPlanDuration, setDistanceBetweenStops }) => {
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
  const [distancesBetweenEachStop, setDistancesBetweenEachStop] = useState([]);

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
          waypoints: data.map(gasStation => ({
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
            // Use promise chaining to ensure sequential execution
            Promise.resolve()
              .then(() => {
                setDirections(result);
                console.log(result)
              })
              .then(() => {
                setPath(getFullRoute(result.routes[0]));
              })
              .then(() => {
                let distances = [];
                let duration = 0;
                let totalDistance = 0;
                result.routes[0].legs.forEach(leg => {
                  // Collect distance and duration for each leg
                  distances.push(leg.distance.text);
                  duration+=(leg.duration.value/3600);
                  totalDistance+=(leg.distance.value/1609.34) // Value of meters
                });
                
                setDistancesBetweenEachStop(distances);
                setDistanceBetweenStops(distances);

                setDistance(totalDistance);
                setPlanDistance(totalDistance)
              
                setDuration(duration);
                setPlanDuration(duration)
              })
              .then(() => {
                // Access the updated value of path after it's set
                setPolyline(window.google.maps.geometry.encoding.encodePath(getFullRoute(result.routes[0])));
              })
              .then(() => {
                setStartAddress(result.routes[0].legs[0].start_address);
              })
              .then(() => {
                setEndAddress(result.routes[0].legs[result.routes[0].legs.length-1].end_address);
              })
              .catch((error) => {
                console.error('Error setting state:', error);
              });
          } else {
            console.error('Failed to fetch directions. Status: ', status);
            setDirections(null);
            setPath(null);
          }
        }
      );
    }
  }, [origin, destination, data]);

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
        <div style={{ marginTop: '15px', marginLeft: '10px'}}>Distance: {distance ? `${Math.round(distance)} miles` : ''}</div>
        <div style={{ marginBottom: '20px', marginLeft: '10px'}}>Duration: {duration ? `${Math.floor(duration)} hours and ${Math.round((duration - Math.floor(duration)) * 60)} minutes` : ''}</div>
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

        {path && (
          <>
            <Polyline
              path={path}
              options={{
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 3
              }}
            />
          </>
        )}

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


function decodePolyline(encoded) {
  return window.google.maps.geometry.encoding.decodePath(encoded);
}


function getFullRoute(route) {
  let coordinates = []
  for (let leg of route.legs) {
    for (let step of leg.steps) {
      for (let coordinate of decodePolyline(step.polyline.points)) {
        coordinates.push(coordinate)
      }
    }
  }
  return coordinates
}


export default Map;
