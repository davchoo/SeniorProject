import React, { useState } from 'react';
import { GoogleMap, useLoadScript, Marker, Polyline, InfoWindow } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';
import axios from "axios";

const libraries = ['places'];
const mapContainerStyle = {
  width: '65vw',
  height: '70vh',
  border: '4px solid white',
  borderRadius: '8px',
  marginLeft: 'auto',
};

const center = {
  lat: 39.8283, // Latitude of the center of the USA
  lng: -98.5795, // Longitude of the center of the USA
};

const Map = () => {
 const { isLoaded, loadError } = useLoadScript({
   googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
   libraries,
 });

  const [origin, setOrigin] = useState(null);
  const [destination, setDestination] = useState(null);
  const [directions, setDirections] = useState(null);
  const [gasStations, setGasStations] = useState([]);
  const [infoWindow, setInfoWindow] = useState(null);

  const handlePlaceSelect = (selectedPlace, isOrigin) => {
  if (selectedPlace && selectedPlace.geometry && selectedPlace.geometry.location) {
    const lat = selectedPlace.geometry.location.lat();
    const lng = selectedPlace.geometry.location.lng();
    if (isOrigin) {
      setOrigin({ lat, lng });
    } else {
      setDestination({ lat, lng });
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

const getGasStations = () => {
  if(origin != null && destination != null){

  // Make an HTTP POST request to your backend
  axios.post('http://localhost:8080/api/trip/gas', {
      originLat: origin.lat,
      originLng: origin.lng,
      destinationLat: destination.lat,
      destinationLng: destination.lng,
      type: "REGULAR_UNLEADED", // need to get this from what Kaan is working on
      travelersMeterCapacity: 482803 // need to get this from what Kaan is working on
  })
      .then(response => {
        // Handle success
        setDirections(response.data.directionsResult)
        setGasStations(response.data.gasStationList)
        console.log("Response from backend:", response.data);
    
      })
      .catch(error => {
        // Handle error
        console.error("Error getting gas stations:", error);
      });
  }
};

// useEffect(() => {
//   if (origin && destination) {
//     const directionsService = new window.google.maps.DirectionsService();
//     directionsService.route(
//       {
//         origin: new window.google.maps.LatLng(origin.lat, origin.lng),
//         destination: new window.google.maps.LatLng(destination.lat, destination.lng),
//         travelMode: 'DRIVING',
//       },
//       (result, status) => {
//         if (status === 'OK') {
//           console.log(result)
//           setDirections(result);
//         } else {
//           console.error('Failed to fetch directions. Status: ', status);
//         }
//       }
//     );
//   }
// }, [origin, destination]);


  if (loadError) {
    return <div>Error Loading Maps</div>;
  }

  if (!isLoaded) {
    return <div>Loading Maps</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'flex-start', alignItems: 'flex-start', height: '100vh', marginTop: '20px' }}>
      <div style={{ width: '30vw', marginBottom: '20px', marginTop: '80px', display: 'flex', flexDirection: 'column', alignItems: 'flex-start', zIndex: 1 }}>
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, true)} label="Enter Origin:" />
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, false)} label="Enter Destination:" />

      </div>
      <GoogleMap
        mapContainerStyle={mapContainerStyle}
        zoom={4.6}
        center={center}
      >
        {origin != null ? (
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
        ) : null}
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

        {destination != null ? (
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
        ) : null}
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

        {directions && directions.routes && directions.routes.length > 0 && (
          <>
            <Polyline
              path={decodePolyline(directions.routes[0].overviewPolyline.encodedPath)}
              options={{
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 3
              }}
            />
          </>
        )}

        {gasStations != null ?
          gasStations.map(station => (
            <Marker
              key={station.name}
              position={{
                lat: station.location.latitude,
                lng: station.location.longitude
              }}
              title={station.formattedAddress}
              icon={{
                url: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png',
                scaledSize: new window.google.maps.Size(30, 30)
              }}
            />
          ))
          : null}
      </GoogleMap>
    </div>
  );
};

function decodePolyline(encoded) {
  let index = 0;
  let lat = 0;
  let lng = 0;
  const path = [];

  while (index < encoded.length) {
      let result = 1;
      let shift = 0;
      let b;

      do {
          b = encoded.charAt(index++).charCodeAt(0) - 63 - 1;
          result += b << shift;
          shift += 5;
      } while (b >= 0x1f);

      lat += result & 1 ? ~(result >> 1) : result >> 1;

      result = 1;
      shift = 0;

      do {
          b = encoded.charAt(index++).charCodeAt(0) - 63 - 1;
          result += b << shift;
          shift += 5;
      } while (b >= 0x1f);

      lng += result & 1 ? ~(result >> 1) : result >> 1;

      path.push({ lat: lat / 1e5, lng: lng / 1e5 });
  }

  return path;
}

export default Map;
