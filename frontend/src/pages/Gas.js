import React, { useState } from 'react';
import { GoogleMap, useLoadScript, Marker } from '@react-google-maps/api';

const Gas = ({ showGasInfo }) => {
  const [selectedMake, setSelectedMake] = useState('');
  const [selectedModel, setSelectedModel] = useState('');
  const [showGasStations, setShowGasStations] = useState(false);
  const [selectedFuelType, setSelectedFuelType] = useState('');

  const toggleGasStations = () => {
    setShowGasStations(!showGasStations);
  };

  const handleFuelTypeChange = (event) => {
    setSelectedFuelType(event.target.value);
  };

  // Function to filter gas stations based on fuel type
  const filterGasStations = () => {
    return gasStationsData.gasStationList.filter(station => {
      return station.fuelOptions.fuelPrices.some(fuel => fuel.type === selectedFuelType);
    });
  };

  const GasStationsMarkers = ({ gasStations }) => (
    <>
      {gasStations.map((station) => (
        <Marker
          key={station.name}
          position={{
            lat: station.location.latitude,
            lng: station.location.longitude,
          }}
          title={station.formattedAddress}
          icon={{
            url: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png',
            scaledSize: new window.google.maps.Size(30, 30),
          }}
        />
      ))}
    </>
  );

  return (
    <div>
      <div style={{ position: 'absolute', right: '12px', top: '130px' }}>
        <p className="text-sm text-custom-black font-notosansjp">Enter Information About Your Vehicle:</p>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', marginTop: '50px' }}>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Car Make:</p>
          <select value={selectedMake} onChange={(e) => setSelectedMake(e.target.value)}>
            <option value="">Select Make</option>
          </select>
        </div>

        <div>
          <div style={{ marginLeft: '30px' }}>
            <p className="text-sm text-custom-black font-notosansjp">Car Model:</p>
            <select value={selectedModel} onChange={(e) => setSelectedModel(e.target.value)}>
              <option value="">Select Model</option>
            </select>
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', marginTop: '10px', marginLeft: '10px' }}>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Select Fuel Type:</p>
          <select id="fuelType" onChange={handleFuelTypeChange} value={selectedFuelType}>
            <option value="">Select...</option>
            <option value="REGULAR_UNLEADED">Regular Unleaded</option>
            <option value="MIDGRADE">Midgrade</option>
            <option value="PREMIUM">Premium</option>
            <option value="DIESEL">Diesel</option>
          </select>
        </div>
      </div>

      <div style={{ padding: '10px' }}>
        <button
          onClick={toggleGasStations}
          className={`font-notosansjp font-extrabold mr-10 mt-10 text-custom-black ${showGasStations ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2`}
        >
          Show Gas Stations
        </button>
      </div>
    </div>
  );
};

export default Gas;