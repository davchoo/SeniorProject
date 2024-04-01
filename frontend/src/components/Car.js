import React, { useState, useEffect } from 'react';
import Papa from 'papaparse'; // Import PapaParse library for parsing CSV

const Car = ({setType,setTankSizeInGallons,setMilesPerGallon}) => {
  const [make, setMake] = useState('');
  const [model, setModel] = useState('');
  const [vehicleInfo, setVehicleInfo] = useState(null);
  const [error, setError] = useState('');
  const [makes, setMakes] = useState([]);
  const [models, setModels] = useState({});
  const [filteredModels, setFilteredModels] = useState([]);

  useEffect(() => {
    const fetchMakesAndModels = async () => {
      try {
        console.log("hello")
        const response = await fetch("/vehicles.csv"); // Fetch CSV file
        console.log(response)
        const csv = await response.text();

        // Parse CSV using PapaParse
        const results = Papa.parse(csv, { header: true });
        const uniqueMakes = [...new Set(results.data.map(entry => entry.make))];
        const uniqueModels = {};
        uniqueMakes.forEach(make => {
          // Filter out duplicate models for each make
          uniqueModels[make] = [...new Set(results.data.filter(entry => entry.make === make).map(entry => entry.model))];
        });

        setMakes(uniqueMakes);
        setModels(uniqueModels);
      } catch (error) {
        console.error('Error fetching makes and models:', error);
      }
    };

    fetchMakesAndModels();
  }, []);

  const fetchVehicleInfo = async () => {
    try {
      console.log("here");
      const response = await fetch('/vehicles.csv'); // Fetch CSV file
      const csv = await response.text();
      const results = Papa.parse(csv, { header: true });

      const selectedModelData = results.data.find(entry => entry.make === make && entry.model === model);
      if (selectedModelData) {
        const mpgData = parseFloat(selectedModelData.comb08);
        console.log(mpgData)
        setMilesPerGallon(mpgData);
        const tankSize = getTankSizeEstimation(selectedModelData.VClass, selectedModelData.year)
        setTankSizeInGallons(tankSize)
        const rangeEstimation = getRangeEstimation(selectedModelData.VClass, selectedModelData.year);
        const totalMiles = mpgData * rangeEstimation;
        const totalMeters = totalMiles * 1609.34;
        setVehicleInfo({
          ...selectedModelData,
          totalMiles: totalMiles.toFixed(2),
          totalMeters: totalMeters.toFixed(2),
          mpg: mpgData,
          tankSize: tankSize
        });
        setError('');
      } else {
        setVehicleInfo(null);
        setError('No vehicle found for the provided make and model.');
      }
    } catch (error) {
      console.error('Error getting the vehicles information:', error);
      setError('Failed to get vehicle information. Please try again later with valid information.');
    }
  };

  const getTankSizeEstimation = (vehicleClass, year) => {
    switch (vehicleClass) {
      case 'Compact Cars':
        return 13; // Estimated tank size for Compact Cars: 12 gallons
      case 'Large Cars':
        return 18; // Estimated tank size for Large Cars: 18 gallons
      case 'Midsize Cars':
        return 16; // Estimated tank size for Midsize Cars: 16 gallons
      case 'Midsize Station Wagons':
        return 16; // Estimated tank size for Midsize Station Wagons: 16 gallons
      case 'Minicompact Cars':
        return 10; // Estimated tank size for Minicompact Cars: 10 gallons
      case 'Subcompact Cars':
        return 10; // Estimated tank size for Subcompact Cars: 10 gallons
      case 'Small Station Wagons':
        return 12; // Estimated tank size for Small Station Wagons: 12 gallons
      case 'Small Pickup Trucks':
        return 16; // Estimated tank size for Small Pickup Trucks: 16 gallons
      case 'Standard Pickup Trucks':
        return 20; // Estimated tank size for Standard Pickup Trucks: 20 gallons
      case 'Sport Utility Vehicle (SUV)':
        return 18; // Estimated tank size for SUVs: 18 gallons
      case 'Minivan':
        return 20; // Estimated tank size for Minivans: 20 gallons
      case 'Special Purpose Vehicles':
        return 16; // Estimated tank size for Special Purpose Vehicles: 16 gallons
      case 'Vans, Cargo Type':
        return 20; // Estimated tank size for Cargo Vans: 20 gallons
      case 'Vans, Passenger Type':
        return 20; // Estimated tank size for Passenger Vans: 20 gallons
      case 'Two Seaters':
        return 8; // Estimated tank size for Two Seaters: 8 gallons
      default:
        return 14; // Default estimated tank size: 14 gallons
    }
  };
  

  const getRangeEstimation = (vehicleClass, year) => {
    switch (vehicleClass) {
      case 'Compact Cars':
        return 350;
      case 'Large Cars':
      case 'Midsize Cars':
      case 'Midsize Station Wagons':
        return 400;
      case 'Minicompact Cars':
      case 'Subcompact Cars':
      case 'Small Station Wagons':
      case 'Small Pickup Trucks':
      case 'Standard Pickup Trucks':
      case 'Sport Utility Vehicle (SUV)':
      case 'Minivan':
      case 'Special Purpose Vehicles':
      case 'Vans, Cargo Type':
      case 'Vans, Passenger Type':
        return 300;
      case 'Two Seaters':
        return 250;
      default:
        return 350;
    }
  };

  const handleMakeChange = (event) => {
    const selectedMake = event.target.value;
    setMake(selectedMake);
    setModel('');
    // Filter models based on the selected make
    const filteredModels = models[selectedMake] || [];
    setFilteredModels(filteredModels);
  };

  const handleModelChange = (event) => {
    setModel(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    fetchVehicleInfo();
  };

  return (
    <div className="max-w-2xl mx-auto p-6 bg-gray-100 rounded-lg shadow-md">
      <h1 className="text-center text-blue-500 text-3xl font-semibold mb-8">Vehicle Information</h1>
      <form onSubmit={handleSubmit} className="flex flex-col">
        <div className="mb-4">
          <label className="font-bold">Make:</label>
          <select value={make} onChange={handleMakeChange} className="p-2 mt-1 border border-gray-300 rounded-md">
            <option value="">--Select Make--</option>
            {makes.map(make => (
              <option key={make} value={make}>{make}</option>
            ))}
          </select>
        </div>
        <div className="mb-4">
          <label className="font-bold">Model:</label>
          <select value={model} onChange={handleModelChange} className="p-2 mt-1 border border-gray-300 rounded-md">
            <option value="">--Select Model--</option>
            {filteredModels.map(model => (
              <option key={model} value={model}>{model}</option>
            ))}
          </select>
        </div>
        <button type="submit" disabled={!make || !model} className="bg-blue-500 text-white font-semibold px-4 py-2 rounded-md cursor-pointer transition duration-300 hover:bg-blue-600">Get Vehicle Info</button>
      </form>
      {error && <p className="text-red-500 mt-4">{error}</p>}
      {vehicleInfo && (
        <div className="mt-8 border border-gray-300 p-4 rounded-md bg-white shadow-md">
          <h2 className="text-blue-500 text-xl font-semibold mb-2">Vehicle Details</h2>
          <p>
            <strong>{vehicleInfo.make} {vehicleInfo.model} Info:</strong>
            <ul>
              <li>MPG: {vehicleInfo.mpg}</li>
              <li>Estimated Tank Size In Gallons: {vehicleInfo.tankSize}</li>
            </ul>
          </p>
        </div>
      )}
    </div>
  );
};

export default Car;
