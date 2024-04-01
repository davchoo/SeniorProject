import React, { useState } from 'react';
import { GoogleMap, useLoadScript, Marker } from '@react-google-maps/api';
import gasStationsData from './gasStations.json';
import axios from 'axios';
import Car from '../components/Car';

const Gas = ({ showGasInfo, setSelectedGasStations, getPolyline, getStartAddress, getEndAddress }) => {
  const [selectedMake, setSelectedMake] = useState('');
  const [selectedModel, setSelectedModel] = useState('');
  const [showGasStations, setShowGasStations] = useState(false);
  const [selectedFuelType, setSelectedFuelType] = useState('');
  const [gasStations, setGasStations] = useState([]);
  const [price, setPrice] = useState();
  const [type, setType] = useState();
  const[milesPerGallon, setMilesPerGallon] = useState();
  const [tankSizeInGallons, setTankSizeInGallons] = useState();
  

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
        tankSizeInGallons: tankSizeInGallons,
        milesPerGallon: milesPerGallon,
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

return (
    <div>
      <div  >
        <p className="text-sm text-custom-black font-notosansjp">Enter Information About Your Vehicle:</p>
      </div>
      

      <div>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Select Fuel Type:</p>
          <select id="fuelType" onChange={handleFuelTypeChange} value={selectedFuelType}>
            <option value="">Select...</option>
            <option value="REGULAR_UNLEADED">Regular Unleaded</option>
            <option value="MIDGRADE">Midgrade</option>
            <option value="PREMIUM">Premium</option>
            <option value="DIESEL">Diesel</option>
          </select>

          <div>
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Estimated Total Fuel Cost of the Trip: ${price}</p>
        </div>
      </div>
      <div >
        <div>
          <p className="text-sm text-custom-black font-notosansjp">Number of Fuel Stops Needed: {gasStations.length}</p>
        </div>
      </div>
        </div>
      </div>
      <div>
        <Car setMilesPerGallon={setMilesPerGallon} setTankSizeInGallons={setTankSizeInGallons}/>
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
          url: 'https://maps.gstatic.com/mapfiles/place_api/icons/gas_station-71.png',
          scaledSize: new window.google.maps.Size(25, 25),
        }}
        onClick={() => onClick(station)} 
      />
    ))}
  </>
);

export default Gas;