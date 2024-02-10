import React from 'react';
import { GoogleMap, useLoadScript, Marker } from '@react-google-maps/api';
import { AutoComplete } from './AutoComplete';

const libraries = ['places'];
const mapContainerStyle = {
  width: '85vw',
  height: '80vh',
  border: '2px solid white',
  borderRadius: '8px', 
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

  if (loadError) {
    return <div>Error loading maps</div>;
  }

  if (!isLoaded) {
    return <div>Loading maps</div>;
  }

  return (
    <div>
      <AutoComplete />
      <GoogleMap
        mapContainerStyle={mapContainerStyle}
        zoom={10}
        center={center}
      >
        <Marker position={center} />
      </GoogleMap>
    </div>
  );
};

export default Map;
