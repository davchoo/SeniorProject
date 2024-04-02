import React, { useState } from 'react';
import { GiHamburgerMenu } from 'react-icons/gi';
import Map from '../components/Map';
import Sidebar from '../components/SaveSidebar';
import Gas from '../pages/Gas';
import Weather from '../pages/Weather';

function Plan() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [showGasInfo, setShowGasInfo] = useState(false);
  const [showWeatherInfo, setShowWeatherInfo] = useState(false);
  const [data, setSelectedData] = useState([]);
  const [polyline, setPolyline] = useState("");
  const [startAddress, setStartAddress] = useState("")
  const [endAddress, setEndAddress] = useState("")
 
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const toggleGasInfo = () => {
    setShowGasInfo(!showGasInfo);
    setShowWeatherInfo(false); 
  };

  const toggleWeatherInfo = () => {
    setShowWeatherInfo(!showWeatherInfo);
    setShowGasInfo(false); 
  };

  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };

  return (
    <div className='bg-custom-green'>
      <div> 
        <button onClick={toggleSidebar} className="absolute z-0 text-xl text-white">
          <GiHamburgerMenu />
        </button>
        
        <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar}>
          <div style={{ padding: '20px', borderRight: '2px solid white' }}>
            <h2 className="font-notosansjp font-extrabold text-custom-black">Saved Trips</h2>
          </div>
        </Sidebar>
        <p className="mt-2 text-center text-3xl text-custom-black">
          Let's Start Planning Your Trip!
        </p>
        <p className="text-sm text-custom-black font-notosansjp text-center">
          Provide your origin and destination locations to begin.
        </p>
  
        <div className="flex flex-row m-2 p-2 justify-between">
          <Map showGasInfo={showGasInfo} data={data} setPolyline={setPolyline} setStartAddress={setStartAddress} setEndAddress={setEndAddress}/>
          <div className='flex flex-col'>
            <p className="text-sm text-custom-black font-notosansjp">
              {showGasInfo && <div className='text-center'>Viewing Gas</div>}
              {showWeatherInfo && "Viewing Weather."}
              {!showGasInfo && !showWeatherInfo && "Select an option below."}
            </p>
            <div className='items-center'>
              <button
                onClick={toggleGasInfo}
                className={`font-notosansjp font-extrabold mr-4 text-custom-black ${showGasInfo ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 hover:bg-custom-green4`}
              >
                Gas 
              </button>
              <button
                onClick={toggleWeatherInfo}
                className={`font-notosansjp font-extrabold ml-4 text-custom-black ${showWeatherInfo ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 hover:bg-custom-green4`}
              >
                Weather
              </button>
            </div>
            {showGasInfo && <Gas showGasInfo={showGasInfo} setSelectedGasStations={setSelectedData} getPolyline={polyline}/>}
            {showWeatherInfo && <Weather />}
          </div>
          <div>
            </div>
        </div>
      </div>
    </div>
  );
  
    
}

export default Plan;
