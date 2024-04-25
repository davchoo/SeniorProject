import React, { useState, useEffect, useMemo } from 'react';
import axios from "axios"
import { PrecipitationLegend, TemperatureLegend, WXLegend } from '../components/Legends';

const RouteStartSlider = ({ setRouteStartTime }) => {
  const [selectedDateTime, setSelectedDateTime] = useState(0);
  const [confirmButtonColor, setConfirmButtonColor] = useState('bg-custom-green3');
  const today = new Date();

  const dateTimes = Array.from({ length: 24 * 7 }, (_, i) => {
    const dateTime = new Date(today);
    dateTime.setHours(today.getHours() + i);
    return dateTime.toLocaleString();
  });

  const handleDateTimeChange = (index) => {
    setSelectedDateTime(index);
  };

  const handleConfirmDateClick = () => {
    setRouteStartTime(new Date(dateTimes[selectedDateTime]));
    setConfirmButtonColor('bg-custom-green4');
    // Reset the button color after 1 second (we can adjust after tesing this with the backend)
    setTimeout(() => {
      setConfirmButtonColor('bg-custom-green3');
    }, 1000);
  };

  return (
    <div className="mt-4">
      <p className="font-notosansjp text-custom-black font-semibold">Route Start:</p>
      <div className='flex flex-row'>
        <input
          type="range"
          min={0}
          max={dateTimes.length - 1}
          value={selectedDateTime}
          onChange={(e) => handleDateTimeChange(parseInt(e.target.value))}
          className='flex-grow'
        />
        <button
          onClick={handleConfirmDateClick}
          className={`py-1 px-2 rounded-md font-notosansjp text-custom-black font-semibold ${confirmButtonColor} hover:bg-custom-green4`}
        >
          Confirm Date
        </button>
      </div>
      <p>{dateTimes[selectedDateTime]}</p>
    </div>
  )
}

const RadarTimeSlider = ({ availableTimes, setSelectedTime }) => {
  const [index, setIndex] = useState(0)

  useEffect(() => {
    setSelectedTime(availableTimes[index])
  }, [index])

  useEffect(() => {
    setIndex(0)
  }, [availableTimes])

  return (
    <div className="mt-4">
      <p className="font-notosansjp text-custom-black font-semibold">Radar time:</p>
      <div className='flex flex-row'>
        <input
          type="range"
          min={0}
          max={availableTimes.length - 1}
          value={index}
          onChange={(e) => setIndex(parseInt(e.target.value))}
          list='radar-times'
          className='w-full'
        />
        <datalist id='radar-times'>
          {availableTimes.map((value, index) => (
            <option key={index} value={index}></option>
          ))}
        </datalist>
      </div>
      <p>{new Date(availableTimes[index]).toLocaleString()}</p>
    </div>
  )
}

const Weather = ({ setForecastedRoute, weatherAlerts, setRouteStartTime, availableLayers, selectedLayerName, setSelectedLayerName, setSelectedLayerTime, setShowRadar }) => {
  const [selectedOption, setSelectedOption] = useState('radar');
  const [showLegend, setShowLegend] = useState(false);
  const [selectedRadar, setSelectedRadar] = useState('weather');
  const [expandedAlert, setExpandedAlert] = useState(null);
  const [showLegendButton, setShowLegendButton] = useState(false);
  const [sortedAlerts, setSortedAlerts] = useState([])

  const availableTimes = useMemo(() => {
    if (!availableLayers || !availableLayers[selectedLayerName]) {
      return []
    }
    return availableLayers[selectedLayerName].dimensions.time.values
  }, [availableLayers, selectedLayerName])

  useEffect(() => {
    if (weatherAlerts) {
      setSortedAlerts(Object.values(weatherAlerts).sort((a, b) => new Date(b.sent) - new Date(a.sent)))
      // TODO could check new alerts by ids?
    }
  }, [weatherAlerts])

  useEffect(() => {
    let layerName = {
      temperature: 'ndfd:conus.temp',
      precipitation: 'ndfd:conus.qpf',
      weather: 'ndfd:conus.wx',
    }[selectedRadar] // TODO This really should be hardcoded but :shrug:
    if (!layerName) {
      return
    }
    setSelectedLayerName(layerName)
  }, [selectedRadar])

  const handleOptionChange = (value) => {
    setSelectedOption(value);
    setShowLegend(false);
    setShowLegendButton(value === 'forecasted-route' || (value === 'radar' && selectedRadar !== ''));
    if (value === 'forecasted-route') {
      setShowLegend(true);
    }
  };

  const toggleLegend = () => {
    setShowLegend(!showLegend);
  };

  const toggleExpand = (alert) => {
    setExpandedAlert(expandedAlert === alert ? null : alert);
  };

  const qpfRange = (index) => {
    const min = index * 0.05;
    const max = (index + 1) * 0.05;
    return `${min}in - ${max}in`;
  };

  return (
    <div>
      <div>
        <p className="font-notosansjp text-custom-black font-semibold text-sm">Select Weather View:</p>
        <div className="flex flex-row items-center">
          <div className="mr-4">
            <input
              type="radio"
              id="forecasted-route"
              name="weatherOption"
              value="forecasted-route"
              checked={selectedOption === 'forecasted-route'}
              onChange={() => {
                handleOptionChange('forecasted-route');
                setForecastedRoute(true)
              }}
              className="mr-1"
            />
            <label>Forecasted Route</label>
          </div>
          <div>
            <input
              type="radio"
              id="radar"
              name="weatherOption"
              value="radar"
              checked={selectedOption === 'radar'}
              onChange={() => {
                handleOptionChange('radar')
                setForecastedRoute(false)
              }}
              className="mr-1"
            />
            <label>Weather Radar</label>
          </div>
        </div>
      </div>

      {selectedOption === 'forecasted-route' && <RouteStartSlider setRouteStartTime={setRouteStartTime} />}
      {selectedOption === 'radar' && <RadarTimeSlider availableTimes={availableTimes} setSelectedTime={setSelectedLayerTime} />}

      {selectedOption === 'radar' && (
        <div className="mt-8">
          <p className="font-notosansjp text-custom-black font-semibold">Select Radar:</p>
          <div className="flex">
            {['weather', 'temperature', 'precipitation'].map((value) => (
              <button
                key={value}
                className={`mr-2 py-1 px-2 rounded-md font-notosansjp text-custom-black font-semibold ${selectedRadar === value ? 'bg-custom-green4' : 'bg-custom-green3'} hover:bg-custom-green4`}
                onClick={() => {
                  setSelectedRadar(value);
                  setShowLegend(true);
                  setShowLegendButton(true);
                }}
              >
                {value.substring(0, 1).toUpperCase() + value.substring(1)}
              </button>
            ))}
          </div>
        </div>
      )}


      {selectedOption && (
        <div>
          <button
            className={`font-notosansjp text-custom-black font-semibold py-1 px-2 rounded-md mb-2 mt-20 ${showLegend ? 'bg-custom-green4' : 'bg-custom-green3'
              } hover:bg-custom-green4`}
            onClick={toggleLegend}
          >
            {showLegend ? 'Hide' : 'Show'} Legend
          </button>

          {showLegend && selectedOption === 'forecasted-route' && <WXLegend /> }
          {showLegend && selectedOption === 'radar' && selectedRadar === 'weather' && <WXLegend /> }
          {showLegend && selectedOption === 'radar' && selectedRadar === 'precipitation' && <PrecipitationLegend /> }
          {showLegend && selectedOption === 'radar' && selectedRadar === 'temperature' && <TemperatureLegend /> }

        </div>
      )}

      <div className="mt-20">
        <p className="font-notosansjp text-custom-black font-semibold">Alerts Along Your Route:</p>
        {sortedAlerts?.map((alert, index) => {
          // Convert the sent timestamp to a Date object
          const sentTimestamp = new Date(alert.sent);
          // Get the current timestamp
          const currentTimestamp = new Date();
          // Calculate the difference in milliseconds between the current time and the sent time
          const timeDifference = currentTimestamp - sentTimestamp;
          // Convert milliseconds to minutes
          const timeDifferenceInMinutes = timeDifference / (1000 * 60);
          // Check if the difference is less than 10 minutes
          const isNewAlert = timeDifferenceInMinutes < 10;

          return (
            <div key={index} className={isNewAlert ? "bg-red-200" : ""}>
              <p className="font-notosansjp text-custom-black font-semibold underline">{alert.event}:</p>
              <ul className="list-disc pl-8">
                <li key={index} className="mt-2">
                  <p className="font-notosansjp text-custom-black font-semibold">
                    Sent: {new Date(alert.sent).toLocaleString()}
                  </p>
                  <p className="font-notosansjp text-custom-black font-semibold">
                    Expires: {new Date(alert.expires).toLocaleString()}
                  </p>
                  <p className="font-notosansjp text-custom-black font-semibold">Status: {alert.status}</p>
                  <p className="font-notosansjp text-custom-black">Area Description: {alert.areaDescription}</p>
                  <p className="font-notosansjp text-custom-black">Severity: {alert.severity}</p>
                  <p className="font-notosansjp text-custom-black">Headline: {alert.headline}</p>
                  {expandedAlert === alert && (
                    <div>
                      <p className="font-notosansjp text-custom-black">Description: {alert.description}</p>
                      <p className="font-notosansjp text-custom-black">Instruction: {alert.instruction}</p>
                    </div>
                  )}
                  <button onClick={() => toggleExpand(alert)}>
                    {expandedAlert === alert ? 'Collapse Description and Instructions' : 'Expand Description and Instructions'}
                  </button>
                </li>
              </ul>
            </div>
          );
        })}
      </div>
    </div>
  );
};

// Define Axios methods
export const weatherApi = {
  checkRoute: async (polyline, durations, date) => {
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/api/weather/check_route`, {
        polyline: polyline,
        durations: durations,
        startTime: date,

      });
      return response.data;
    } catch (error) {
      console.error('Error in checkRoute:', error);
      throw error;
    }
  },

  getFeatures: async (fileDate, day) => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/api/weather/features`, {
        params: {
          file_date: fileDate,
          day: day
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error in getFeatures:', error);
      throw error;
    }
  },

  getAvailableFileDates: async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/api/weather/features/file_dates`);
      return response.data;
    } catch (error) {
      console.error('Error in getAvailableFileDates:', error);
      throw error;
    }
  },

  checkRouteAlerts: async (polyline, durations, date) => {
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/api/weather/alerts/check_route`, {
        polyline: polyline,
        durations: durations,
        startTime: date
      });
      console.log(response.data)
      return response.data;
    } catch (error) {
      console.error('Error in checkRouteAlerts:', error);
      throw error;
    }
  },

  getCounties: async (fipsCodes) => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/api/weather/county`, {
        params: {
          fips_codes: fipsCodes
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error in getCounties:', error);
      throw error;
    }
  },

  checkRouteRaster: async (polyline, durations, date) => {
    try {
      console.log(durations)
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/api/weather/raster/check_route`, {
        polyline: polyline,
        durations: durations,
        startTime: new Date()
      });
      console.log(response)
      return response.data;
    } catch (error) {
      console.error('Error in checkRouteRaster:', error);
      throw error;
    }
  }
};

export default Weather;
