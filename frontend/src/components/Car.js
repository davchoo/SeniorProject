import React, { useState, useEffect, useMemo } from 'react';

const Car = ({ setFuelType, setTankSizeInGallons, setMilesPerGallon, setSelectedMake, setSelectedModel, setSelectedYear }) => {
  const [vehicleData, setVehicleData] = useState({}) 

  const [year, setYear] = useState('');
  const [make, setMake] = useState('');
  const [model, setModel] = useState('');

  const [vehicleInfo, setVehicleInfo] = useState(null);

  useEffect(() => {
    fetch("/vehicles_mpg.json")
      .then(async response => setVehicleData(await response.json()))
  }, [])

  const years = useMemo(() => Object.keys(vehicleData).sort((a, b) => b - a), [vehicleData])

  const makes = useMemo(() => {
    if (!year) {
      return []
    }
    return Object.keys(vehicleData[year])
  }, [vehicleData, year])

  const models = useMemo(() => {
    if (!year || !make) {
      return []
    }
    return Object.keys(vehicleData[year][make])
  }, [vehicleData, year, make])

  useEffect(() => {
    if (!vehicleData || !year || !make || !model) {
      setSelectedYear(null)
      setSelectedMake(null)
      setSelectedModel(null)
      setMilesPerGallon(null)
      setTankSizeInGallons(null)
      setFuelType(null)
      setVehicleInfo(null)
      return
    }
    const car = vehicleData[year][make][model];
    setSelectedYear(year)
    setSelectedMake(make)
    setSelectedModel(model)
    setMilesPerGallon(car[0])
    const tankSize = getTankSizeEstimation(car[1])
    setTankSizeInGallons(tankSize)
    const fuelType = transformFuelType(car[2])
    setFuelType(fuelType)
    setVehicleInfo({
      mpg: car[0],
      tankSize: tankSize,
      fuelType: fuelType
    })
  }, [vehicleData, year, make, model])

  const transformFuelType = (fuelType) => {
    switch (fuelType) {
      case 'Regular Gasoline':
        return "REGULAR_UNLEADED"
      case 'Midgrade Gasoline':
        return "REGULAR_UNLEADED"
      case 'Natural Gas':
        return "REGULAR_UNLEADED"
      case 'Midgrade Gasoline':
        return 'MIDGRADE'
      case 'Diesel':
        return 'DIESEL'
      case 'Premium Gasoline':
        return "PREMIUM"
      default:
        return fuelType
    }
  }

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
        // 2024 Aston Martin DB11 V8: 19.8 gal
        // 2024 Subaru BRZ: 13.2 gal
        return 15; // Estimated tank size for Minicompact Cars: 10 gallons
      case 'Subcompact Cars':
        // 2024 Ford Mustang (2.3L): 15.9 gal
        // 2024 Ford Mustang (5.0L): 15.98 gal
        // 2024 Chevrolet Camero: 19 gal
        // 2024 Nissan GT-R: 19.5 gal
        return 17; // Estimated tank size for Subcompact Cars: 10 gallons
      case 'Small Sport Utility Vehicle 2WD':
      case 'Small Sport Utility Vehicle 4WD':
        // 2024 Ford Bronco AWD: 21.1 gal (Long wheelbase)
        // 2024 Ford Bronco AWD: 17.4 gal (Short wheelbase)
        return 19
      case 'Standard Sport Utility Vehicle 2WD':
      case 'Standard Sport Utility Vehicle 4WD':
        // 2024 Ford Explorer AWD (2.3L): 17.9 gal
        // 2024 Ford Explorer AWD (3.0L): 20.2 gal
        // 2024 Ford Explorer AWD (3.3L): 20.2 gal
        // 2024 Ford Explorer AWD (3.3L, HEV): 18.0 gal
        return 19;
      case 'Small Station Wagons':
        return 12; // Estimated tank size for Small Station Wagons: 12 gallons
      case 'Small Pickup Trucks':
        return 16; // Estimated tank size for Small Pickup Trucks: 16 gallons
      case 'Standard Pickup Trucks':
        return 20; // Estimated tank size for Standard Pickup Trucks: 20 gallons
      case 'Minivan':
        return 20; // Estimated tank size for Minivans: 20 gallons
      case 'Special Purpose Vehicles':
        return 16; // Estimated tank size for Special Purpose Vehicles: 16 gallons
      case 'Vans, Cargo Type':
        return 20; // Estimated tank size for Cargo Vans: 20 gallons
      case 'Vans, Passenger Type':
        return 20; // Estimated tank size for Passenger Vans: 20 gallons
      case 'Two Seaters':
        // 2024 Chevrolet Corvette: 18.5 gal
        // 2024 Nissan Z: 16.375 gal
        // 2021 Audi TT: 14.5 gal
        // 2020 Ford GT: 15.2 gal
        return 16;
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

  const handleYearChange = (event) => {
    setYear(event.target.value);
    setMake('')
    setModel('')
  };

  const handleMakeChange = (event) => {
    setMake(event.target.value);
    setModel('');
  };

  const handleModelChange = (event) => {
    setModel(event.target.value);
  };

  const Selection = ({ label, values, currentValue, onChange }) => (
    <div className="mb-4 flex flex-row items-center">
      <label className="font-bold">{label}:</label>
      <select value={currentValue} onChange={onChange} className="ml-2 p-2 mt-1 border border-gray-300 rounded-md flex-grow">
        <option value="">--Select {label}--</option>
        {values.map(value => (
          <option key={value} value={value}>{value}</option>
        ))}
      </select>
    </div>
  )

  return (
    <div className="max-w-2xl mx-auto p-6 bg-gray-100 rounded-lg shadow-md">
      <h4 className="text-center text-custom-black text-md font-semibold mb-3 mt-1">Vehicle Selection</h4>
      <form className="flex flex-col">
        <Selection label="Year" values={years} currentValue={year} onChange={handleYearChange} />
        <Selection label="Make" values={makes} currentValue={make} onChange={handleMakeChange} />
        <Selection label="Model" values={models} currentValue={model} onChange={handleModelChange} />
      </form>
      {vehicleInfo && (
        <div className="mt-4 border light-gray p-4 rounded-md bg-white shadow-md">
          <h2 className="text-custom-black text-lg font-semibold mb-2 text-center">Vehicle Details</h2>
          <hr />
          <p>
            <strong>{year} {make} {model} Information:</strong>
            <ul>
              {vehicleInfo.fuelType !== 'Electricity' && (
                <>
                  <li>Miles Per Gallon (MPG): {vehicleInfo.mpg}</li>
                  <li>Estimated Tank Size: {vehicleInfo.tankSize} gallons</li>
                </>
              )}
              <li>Fuel Type: {vehicleInfo.fuelType}</li>
            </ul>
          </p>
        </div>
      )}
    </div>
  );
};

export default Car;
