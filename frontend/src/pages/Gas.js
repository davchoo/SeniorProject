import React, { useState } from 'react';
import { GoogleMap, useLoadScript, Marker } from '@react-google-maps/api';
import gasStationsData from './gasStations.json';
import axios from 'axios';

const Gas = ({ showGasInfo, setSelectedGasStations, getPolyline, getStartAddress, getEndAddress }) => {
  const [selectedMake, setSelectedMake] = useState('');
  const [selectedModel, setSelectedModel] = useState('');
  const [showGasStations, setShowGasStations] = useState(false);
  const [selectedFuelType, setSelectedFuelType] = useState('');
  const [gasStations, setGasStations] = useState([]);
  const [price, setPrice] = useState();
  

  const toggleGasStations = () => {
    setShowGasStations(!showGasStations);
  };

  const handleFuelTypeChange = (event) => {
    setSelectedFuelType(event.target.value);
  };

  const getGasStations = async () => {
    console.log(getPolyline)
    const polyline = getPolyline; // Call the function to retrieve the polyline
    console.log(getStartAddress)
    console.log(selectedFuelType)
    if (polyline && selectedFuelType) {
      axios.post('http://localhost:8080/api/trip/gas', {
        polyline: polyline, // Use the obtained polyline
        startAddress: getStartAddress,
        endAddress: getEndAddress,
        type: selectedFuelType,
        tankSizeInGallons: 18.56934615384615,
        milesPerGallon: 26,
      })
        .then(response => {
          setGasStations(response.data.gasStations)
          setSelectedGasStations(response.data.gasStations)
          setPrice((response.data.totalTripGasPrice * response.data.tankSizeInGallons).toFixed(2))
          console.log(gasStations)
          console.log("Response from backend:", response.data);
        })
        .catch(error => {
          console.error("Error getting gas stations:", error);
        });
    }
  };

  // Function to filter gas stations based on fuel type
  const filterGasStations = () => {
    return gasStationsData.gasStationList.filter(station => {
      return station.fuelOptions.fuelPrices.some(fuel => fuel.type === selectedFuelType);
    });
  };

  const selectedGasStations = filterGasStations();

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

          <div style={{ display: 'flex', marginTop: '10px', marginLeft: '10px' }}>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Estimated Total Fuel Cost of the Trip: ${price}</p>
        </div>
      </div>
      <div style={{ display: 'flex', marginTop: '10px', marginLeft: '10px' }}>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Number of Fuel Stops Needed: {gasStations.length}</p>
        </div>
      </div>
        </div>
      </div>

      <div style={{ padding: '10px' }}>
      <button
  onClick={async () => {
    toggleGasStations();
    await getGasStations(); // Wait for getGasStations to finish
    //setSelectedGasStations(gasStations);
  }}
        

          className={`font-notosansjp font-extrabold mr-10 mt-10 text-custom-black ${showGasStations ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 hover:bg-custom-green4`}
        >
          Show Gas Stations
        </button>
      </div>
    </div>
  );
};

export const GasStationsMarkers = ({ gasStations, onClick }) => (
  <>
    {gasStations.map((station) => (
      <Marker
        key={station.name}
        position={{
          lat: station.location.lat,
          lng: station.location.lng,
        }}
        title={station.formattedAddress}
        icon={{
          url: 'http://maps.gstatic.com/mapfiles/ms2/micons/gas.png', 
          scaledSize: new window.google.maps.Size(30, 30),
        }}
        onClick={() => onClick(station)} 
      />
    ))}
  </>
);

export default Gas;