import React, { useState } from 'react';
import Map from '../components/Map';
import Sidebar from '../components/SaveSidebar';
import { GiHamburgerMenu } from 'react-icons/gi';
import Gas from '../pages/Gas';
import Weather from '../pages/Weather';

function Plan() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [showGasInfo, setShowGasInfo] = useState(false);
  const [showWeatherInfo, setShowWeatherInfo] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const toggleGasInfo = () => {
    setShowGasInfo(!showGasInfo);
    setShowWeatherInfo(false); // This should close the Weather button when Gas is opened
  };

  const toggleWeatherInfo = () => {
    setShowWeatherInfo(!showWeatherInfo);
    setShowGasInfo(false); // This should close the Gas button when Weather is opened
  };

  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };

  return (
    <div className="min-h-screen bg-custom-green flex justify-center items-center font-notosansjp font-bold m-1">
      <div className="w-full max-w-screen-xl relative">
        <button onClick={toggleSidebar} className="fixed top-4 left-4 text-xl text-white">
          <GiHamburgerMenu />
        </button>

        <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar}>
          <div style={{ padding: '20px', borderRight: '2px solid white' }}>
            <h2 className="font-notosansjp font-extrabold text-custom-black">Saved Trips</h2>
          </div>
        </Sidebar>

        <div className="flex flex-col justify-center items-center m-2">
          <p className="mt-4 text-center text-2xl text-custom-black">
            Let's Start Planning!
          </p>

          <div className="max-w-md w-full px-0 py-0 mb-3 bg-white rounded-lg shadow-md">
            <div className="text-center text-sm text-custom-black mt-2">
              <p>Choose your departure and destination points below:</p>
            </div>
          </div>

          <div style={{ position: 'absolute', left: '-100px', top: '300px' }}>
            <button
              onClick={()=>setShowGasInfo(true)}
              className={`font-notosansjp font-extrabold mr-10 text-custom-black bg-${showGasInfo ? 'custom-green4' : 'custom-green3'} hover:bg-custom-green4 py-1 px-2 rounded-md mb-2`}
            >
              Gas Stations
            </button>
            {/* {showGasInfo && (
              <Gas
              />
            )} */}

            <button
              onClick={toggleWeatherInfo}
              className={`font-notosansjp font-extrabold mt-10 text-custom-black bg-${showWeatherInfo ? 'custom-green4' : 'custom-green3'} hover:bg-custom-green4 py-1 px-2 rounded-md mb-2`}
            >
              Weather
            </button>
          </div>
          <Map showGasInfo={showGasInfo} />
        </div>
      </div>
    </div>
  );
}

export default Plan;