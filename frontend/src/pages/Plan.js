import React, { useEffect, useState } from 'react';
import Map from '../components/Map';
import Sidebar from '../components/SaveSidebar';
import { GiHamburgerMenu } from 'react-icons/gi';
import { checkIsLoggedIn } from '../AuthContext';
import { useNavigate } from 'react-router-dom';
import Gas from '../pages/Gas';
import Weather from '../pages/Weather';
import WeatherRadar from '../components/WeatherRadar';

function Plan() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [showGasInfo, setShowGasInfo] = useState(false);
  const [showWeatherInfo, setShowWeatherInfo] = useState(false);
  const [data, setSelectedData] = useState([]);
  const [polyline, setPolyline] = useState("");
  const [startAddress, setStartAddress] = useState("")
  const [endAddress, setEndAddress] = useState("")
  const [loggedIn, setLoggedIn] = useState(false);
  const [showOverlay, setShowOverlay] = useState(false);
  const navigate = useNavigate()

  const [availableLayers, setAvailableLayers] = useState()
  const [selectedLayerName, setSelectedLayerName] = useState()
  const [selectedLayerTime, setSelectedLayerTime] = useState()
  const [showRadar, setShowRadar] = useState(true); // TODO move to Weather component?

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const toggleGasInfo = () => {
    setShowGasInfo(!showGasInfo);
    setShowOverlay(false)
    setShowWeatherInfo(false);
  };

  const toggleWeatherInfo = () => {
    setShowWeatherInfo(!showWeatherInfo);
    setShowOverlay(true)
    setShowGasInfo(false);
  };

  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };

  useEffect(() => {
    const fetchLoggedInStatus = async () => {
      try {
        const isLoggedIn = await checkIsLoggedIn();
        console.log("isLoggedIn:", isLoggedIn);
        setLoggedIn(isLoggedIn);
      } catch (error) {
        console.error('Error checking login status:', error);
      } 
    };
    fetchLoggedInStatus();
  }, []);

  useEffect(() => {
    if (!availableLayers) {
      return
    }
    // TODO remove debug stuff
    // Print available layers and times for each layer
    for (let layerName of Object.keys(availableLayers)) {
      console.log(layerName + ": " + availableLayers[layerName].dimensions.time.values)
    }

    const defaultLayer = "ndfd:conus.wx"
    setSelectedLayerName(defaultLayer)
    setSelectedLayerTime(availableLayers[defaultLayer].dimensions.time.values[0])
  }, [JSON.stringify(availableLayers)])

  return (
    <div className='bg-custom-green'>
      <div>
        <button onClick={toggleSidebar} className="absolute z-0 text-xl text-custom-black" style={{ marginLeft: '5px', marginTop: '20px' }}>
          <GiHamburgerMenu />
        </button>
        
        <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar}>
          {loggedIn ? 
          <div style={{ padding: '20px', borderRight: '2px solid white' }}>
            <h2 className="font-notosansjp font-extrabold text-custom-black">Saved Trips</h2>
          </div>
          : 
          <div className="flex flex-col items-center pt-24">
            <p className="text-center">You have to be logged in to an account to see saved trips.</p>
            <button onClick={() => navigate("/login")} className="mt-4 bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out text-lg">
              Login
            </button>
          </div>}
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
          <Map showGasInfo={showGasInfo} data={data} setPolyline={setPolyline} setStartAddress={setStartAddress} setEndAddress={setEndAddress} showOverlay={showOverlay}>
            {showWeatherInfo && showRadar && (
              <WeatherRadar setAvailableLayers={setAvailableLayers} layerName={selectedLayerName} time={selectedLayerTime} />
            )}
          </Map>
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
            {showGasInfo && <Gas showGasInfo={showGasInfo} setSelectedGasStations={setSelectedData} getPolyline={polyline} origin={startAddress} destination={endAddress}/>}
            {showWeatherInfo && <Weather/>}
          </div>
          <div>
          </div>
        </div>
      </div>
    </div>
  );  
}

export default Plan;
