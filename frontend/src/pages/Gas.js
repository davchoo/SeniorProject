import React, { useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, MarkerF } from '@react-google-maps/api';
import axios from 'axios';
import Car from '../components/Car';
import ClipLoader from "react-spinners/ClipLoader";

const Gas = ({ showGasInfo, setSelectedGasStations, getPolyline, origin, destination, distance, duration, distanceBetweenStops }) => {
  const [selectedMake, setSelectedMake] = useState();
  const [selectedModel, setSelectedModel] = useState();
  const [year, setYear] = useState();
  const [showGasStations, setShowGasStations] = useState(false);
  const [gasStations, setGasStations] = useState([]);
  const [price, setPrice] = useState();
  const [type, setType] = useState();
  const [milesPerGallon, setMilesPerGallon] = useState();
  const [tankSizeInGallons, setTankSizeInGallons] = useState();
  const [clicked, setClicked] = useState(false);
  const [loading, setLoading] = useState(true);

  const toggleGasStations = () => {
    setShowGasStations(!showGasStations);
  };

  const getGasStations = async () => {
    console.log(getPolyline)
    const polyline = getPolyline; // Call the function to retrieve the polyline
    console.log(origin)
    if (polyline) {
      axios.post('/api/trip/gas', {
        polyline: polyline, // Use the obtained polyline
        startAddress: origin,
        endAddress: destination,
        type: type,
        tankSizeInGallons: tankSizeInGallons,
        milesPerGallon: milesPerGallon,
        make: selectedMake,
        model: selectedModel,
        year: year,
        distance: parseFloat(distance),
        duration: duration
      }, { withCredentials: true })
        .then(response => {
          setGasStations(response.data.gasStations)
          setSelectedGasStations(response.data.gasStations)
          setPrice((response.data.totalTripGasPrice * response.data.car.tankSizeInGallons).toFixed(2))
          console.log(gasStations)
          console.log("Response from backend:", response.data);
          setLoading(false) 
        })
        .catch(error => {
          if(error.response.data.message.includes("not one of the values accepted for Enum class: [DIESEL, MIDGRADE, PREMIUM, REGULAR_UNLEADED]")){
            window.alert("At this time, we cannot support your car's fuel type: " + type)
          }
          console.error("Error getting gas stations:", error);
        });
    }
  };

  return (
    <div>
      <div  >
        <p className="font-notosansjp text-custom-black text-sm">Enter Information About Your Vehicle:</p>
      </div>
      <div>
        <Car setMilesPerGallon={setMilesPerGallon} setTankSizeInGallons={setTankSizeInGallons} setFuelType={setType} setSelectedMake={setSelectedMake} setSelectedModel={setSelectedModel} setSelectedYear={setYear} />
      </div>
      <div className='flex-col'>
        <p>Estimated Price: ${price}</p>
        <p>Total Stops: {gasStations.length}</p>
      </div>
      {distanceBetweenStops.length > 0 && gasStations.length > 0 && !loading ?
        <div>
          Distances between each stop:
          <ul>
            {distanceBetweenStops.map((distance, index) => (
              <li key={index}>
                {distance}
                {index === 0 && gasStations[0] ? ` From ${origin} to ${gasStations[0].name}: ${gasStations[0].formattedAddress}` : ''}
                {index > 0 && index < gasStations.length ? ` From ${gasStations[index - 1].name} to ${gasStations[index].name}: ${gasStations[index].formattedAddress}` : ''}
                {index === gasStations.length && gasStations[gasStations.length - 1] ? ` From ${gasStations[gasStations.length - 1].name} to ${destination}:` : ''}
              </li>
            ))}
          </ul>
        </div> : null}

      <div style={{ padding: '10px' }}>
        <button
          onClick={async () => {
            setClicked(true);
            toggleGasStations();
            await getGasStations(); // Wait for getGasStations to finish
            //setSelectedGasStations(gasStations);
          }}

          className={`font-notosansjp text-custom-black font-semibold mr-10 mt-10 ${showGasStations ? 'bg-custom-green4 ' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 ${(!origin || !destination || !type || !selectedMake || !selectedModel) ? 'disabled bg-gray-400' : 'hover:bg-custom-green4'}`}
          disabled={!origin || !destination || !type || !selectedMake || !selectedModel}
        >
         Show Gas Stations
        </button>
      </div>
      {clicked ? 
        <div className="flex justify-center items-center mt-4">
          <ClipLoader
            loading={loading}
            size={50} 
            aria-label="Loading Spinner"
            data-testid="loader"
          />
        </div>
      : null}
    </div>
  );
};

export const GasStationsMarkers = ({ gasStations, onClick }) => (
  <>
    {gasStations.map((station) => (
      <MarkerF
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
