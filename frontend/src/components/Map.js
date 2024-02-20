import React, { useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, Marker, DirectionsRenderer } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';

const libraries = ['places'];
const mapContainerStyle = {
  width: '65vw',
  height: '90vh',
  border: '4px solid white',
  borderRadius: '8px',
  marginLeft: 'auto', 
};

const center = {
  lat: 7.2905715,
  lng: 80.6337262,
};

const Map = () => {
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries,
  });

  const [origin, setOrigin] = useState(null)
  const [destination, setDestination] = useState(null)
  const [directions, setDirections] = useState(null);

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
          } else {
            console.error('Failed to fetch directions. Status: ', status);
          }
        }
      );
    }
  }, [origin, destination]);

  if (loadError) {
    return <div>Error Loading Maps</div>;
  }

  if (!isLoaded) {
    return <div>Loading Maps</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'flex-start', alignItems: 'flex-start', height: '100vh', marginTop: '20px' }}>
      <div style={{ width: '30vw', marginBottom: '20px', display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, true)} label="Enter Origin:" />
        <AutoComplete handlePlaceSelect={(place) => handlePlaceSelect(place, false)} label="Enter Destination:" />
      </div>
      <GoogleMap
        mapContainerStyle={mapContainerStyle}
        zoom={10}
        center={origin || destination || center}
      >
        {origin && <Marker position={origin} />}
        {destination && <Marker position={destination} />}  
        {directions && (
          <DirectionsRenderer
            directions={directions}
            options={{
              polylineOptions: {
                strokeColor: '#8b0000',
                strokeOpacity: 1,
                strokeWeight: 4,
              },
            }}
          />
        )}
      </GoogleMap>
    </div>
  );

};

export default Map;