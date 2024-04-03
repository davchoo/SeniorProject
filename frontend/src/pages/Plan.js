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
        <button onClick={toggleSidebar} className="absolute z-0 text-xl text-custom-black" style={{ marginLeft: '5px', marginTop: '20px' }}>
          <GiHamburgerMenu />
        </button>
        
        <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar}>
          <div style={{ padding: '20px'}}>
            <h2 className="font-notosansjp text-custom-black font-bold">Saved Trips</h2>
          </div>
        </Sidebar>
        <div className="flex flex-col justify-center items-center m-2">
          <p className="font-notosansjp text-custom-black font-bold mt-4 text-3xl text-center">
            Let's Start Planning Your Trip!
          </p>
        </div>

        <p className="font-notosansjp text-custom-black font-semibold text-sm text-center">
          Provide your origin and destination locations to begin.
        </p>

        <div className="font-notosansjp text-custom-black font-semibold flex flex-row m-2 p-2 justify-between">
          <Map showGasInfo={showGasInfo} data={data} setPolyline={setPolyline} setStartAddress={setStartAddress} setEndAddress={setEndAddress}/>
          <div className='flex flex-col'>
            <p className="font-notosansjp text-custom-black font-semibold text-sm ">
              {showGasInfo && <div className='text-center'>Viewing Gas.</div>}
              {showWeatherInfo && <div className='text-center'>Viewing Weather.</div>} 
              {!showGasInfo && !showWeatherInfo && "Select an option below."}
            </p>
            <div className='items-center'>
              <button
                onClick={toggleGasInfo}
                className={`font-notosansjp text-custom-black font-semibold mr-4  ${showGasInfo ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 hover:bg-custom-green4`}
              >
                Gas 
              </button>
              <button
                onClick={toggleWeatherInfo}
                className={`font-notosansjp text-custom-black font-semibold ml-4 ${showWeatherInfo ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2 hover:bg-custom-green4`}
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
