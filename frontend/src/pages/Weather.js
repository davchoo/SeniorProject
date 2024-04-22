import React, { useState, useEffect } from 'react';
import wxColorMap from './wxColorMap';
import { qpfColorMap } from './qpfColorMap';
import { tempColorMap } from './tempColorMap';
import axios from "axios"

const Weather = ({ setForecastedRoute, weatherAlerts, setChosenTime }) => {
  const [selectedOption, setSelectedOption] = useState(null);
  const [showLegend, setShowLegend] = useState(false);
  const [selectedRadar, setSelectedRadar] = useState('');
  const [expandedAlert, setExpandedAlert] = useState(null);
  const [showLegendButton, setShowLegendButton] = useState(false);
  const [sortedAlerts, setSortedAlerts] = useState([])
  const [selectedDateTime, setSelectedDateTime] = useState(0);

  console.log(weatherAlerts)

  useEffect(() => {
    setSelectedOption(null);
    setShowLegend(false);
    setSelectedRadar('');
    setShowLegendButton(false);
  }, []);

  useEffect(() => {
    if (weatherAlerts && weatherAlerts.alerts) {
      setSortedAlerts(Object.values(weatherAlerts.alerts).sort((a, b) => new Date(b.sent) - new Date(a.sent)))
      // TODO could check new alerts by ids?
    }
  }, [weatherAlerts])

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

  const today = new Date();

  const dateTimes = Array.from({ length: 24 * 7 }, (_, i) => {
    const dateTime = new Date(today);
    dateTime.setHours(today.getHours() + i);
    return dateTime.toLocaleString();
  });

  const handleDateTimeChange = (index) => {
    console.log(index)
    setSelectedDateTime(index);
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

      {selectedOption && (
        <div className="mt-4">
          <p className="font-notosansjp text-custom-black font-semibold">Date and Time Slider:</p>
          <div className='flex flex-row'>
            <input
              type="range"
              min={0}
              max={dateTimes.length - 1}
              value={selectedDateTime}
              onChange={(e) => handleDateTimeChange(parseInt(e.target.value))}
            />
            <button onClick={() => setChosenTime(new Date(dateTimes[selectedDateTime]))}>Confirm Date</button>
          </div>

          <p>{dateTimes[selectedDateTime]}</p>
        </div>
      )}

      {selectedOption === 'radar' && (
        <div className="mt-8">
          <p className="font-notosansjp text-custom-black font-semibold">Select Radar:</p>
          <div className="flex">
            <button
              className={`mr-2 py-1 px-2 rounded-md font-notosansjp text-custom-black font-semibold ${selectedRadar === 'temperature' ? 'bg-custom-green4' : 'bg-custom-green3'} hover:bg-custom-green4`}
              onClick={() => {
                setSelectedRadar('temperature');
                setShowLegend(true);
                setShowLegendButton(true);
              }}
            >
              Temperature
            </button>
            <button
              className={`py-1 px-2 rounded-md font-notosansjp text-custom-black font-semibold ${selectedRadar === 'precipitation' ? 'bg-custom-green4' : 'bg-custom-green3'} hover:bg-custom-green4`}
              onClick={() => {
                setSelectedRadar('precipitation');
                setShowLegend(true);
                setShowLegendButton(true);
              }}
            >
              Precipitation
            </button>
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

          {showLegend && selectedOption === 'forecasted-route' && (
            <div className="mt-4" style={{ width: '300px' }}>
              <p className="font-notosansjp text-custom-black">Weather Forecast Legend:</p>
              <div className="mt-4 flex flex-wrap">
                {Object.entries(wxColorMap).map(([key, color]) => (
                  <div key={key} className="flex items-center mr-2 mb-2" style={{ width: '80px' }}>
                    <div className="w-4 h-4" style={{ backgroundColor: color, border: '1px solid black' }}></div>
                    <p className="font-notosansjp text-custom-black ml-2 text-xs">{key}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {showLegend && selectedRadar === 'precipitation' && selectedOption === 'radar' && (
            <div className="mt-4" style={{ width: '300px' }}>
              <p className="font-notosansjp text-custom-black">Quantitative Precipitation Forecast (QPF) Legend (inches):</p>
              <div className="mt-4 flex items-center">
                <div className="w-full flex justify-between">
                  <div className="w-1/6 font-notosansjp text-custom-black">0.00</div>
                  <div className="w-4/6 h-6 flex justify-between">
                    {qpfColorMap.slice(1, -1).map((color, index) => (
                      <div key={index} className="w-1/6 h-full" style={{ backgroundColor: color }}></div>
                    ))}
                  </div>
                  <div className="w-1/12 font-notosansjp text-custom-black text-right">10.00</div>
                </div>
              </div>
              <div className="mt-2 flex justify-between">
                <div className="w-1/6"></div>
                <p className="font-notosansjp text-custom-black">0.25</p>
                <p className="font-notosansjp text-custom-black">2.50</p>
                <p className="font-notosansjp text-custom-black">5.00</p>
                <p className="font-notosansjp text-custom-black">7.50</p>
                <div className="w-1/6"></div>
              </div>
            </div>
          )}

          {showLegend && selectedRadar === 'temperature' && selectedOption === 'radar' && (
            <div className="mt-4" style={{ width: '300px' }}>
              <p className="font-notosansjp text-custom-black">Temperature Legend (°F):</p>
              <div className="mt-4 flex items-center">
                <div className="w-full flex justify-between">
                  <div className="w-1/6 font-notosansjp text-custom-black">-10.0</div>
                  <div className="w-4/6 h-6 flex justify-between">
                    {tempColorMap.map((color, index) => (
                      <div key={index} className="w-1/6 h-full" style={{ backgroundColor: color }}></div>
                    ))}
                  </div>
                  <div className="w-1/12 font-notosansjp text-custom-black text-right">110.0</div>
                </div>
              </div>
              <div className="mt-2 flex justify-between">
                <div className="w-1/6"></div>
                <p className="font-notosansjp text-custom-black">-5.0</p>
                <p className="font-notosansjp text-custom-black">0.0</p>
                <p className="font-notosansjp text-custom-black">50.0</p>
                <p className="font-notosansjp text-custom-black">100.0</p>
                <div className="w-1/6"></div>
              </div>
            </div>
          )}

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
