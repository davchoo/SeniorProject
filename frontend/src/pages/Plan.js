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
    <div className="min-h-screen bg-custom-green flex justify-center items-center font-notosansjp font-bold m-1 relative z-0">
      <div className="w-full max-w-screen-xl relative h-full"> 
        <button onClick={toggleSidebar} className="absolute top-2 left-[-6rem] z-0 text-xl text-white">
          <GiHamburgerMenu />
        </button>
        
        <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar}>
          <div style={{ padding: '20px', borderRight: '2px solid white' }}>
            <h2 className="font-notosansjp font-extrabold text-custom-black">Saved Trips</h2>
          </div>
        </Sidebar>

        <div className="flex flex-col justify-center items-center m-2">
          <p className="mt-4 text-center text-3xl text-custom-black">
            Let's Start Planning Your Trip!
          </p>

          <div style={{ position: 'absolute', left: '-105px', bottom: '-10px', top: '130px' }}>
            <p className="text-sm text-custom-black font-notosansjp">
              Provide your origin and destination locations to begin.
            </p>
          </div>

          <div style={{ position: 'absolute', left: '-105px', bottom: '-20px', top: '300px' }}>
            <p className="text-sm text-custom-black font-notosansjp mt-2 mb-10">
              {showGasInfo && "Viewing Gas."}
              {showWeatherInfo && "Viewing Weather."}
              {!showGasInfo && !showWeatherInfo && "Select an option below."}
            </p>
            <div className="flex"> 
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

          <Map showGasInfo={showGasInfo} data={data} setPolyline={setPolyline} setStartAddress={setStartAddress} setEndAddress={setEndAddress}/>
        </div>
      </div>
    </div>
  );
}

export default Plan;
