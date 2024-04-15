import React, { useState, useEffect } from 'react';
import weatherAlerts from '../pages/weather_alert.json'; // Importing the JSON for testing  
import wxColorMap from './wxColorMap';
import { qpfColorMap } from './qpfColorMap';
import { tempColorMap } from './tempColorMap';

const Weather = () => {
  const [selectedOption, setSelectedOption] = useState(null);
  const [showLegend, setShowLegend] = useState(false);
  const [selectedRadar, setSelectedRadar] = useState('');
  const [expandedAlert, setExpandedAlert] = useState(null);

  useEffect(() => {
    setSelectedOption(null);
    setShowLegend(false);
    setSelectedRadar('');
  }, []);

  const handleOptionChange = (value) => {
    setSelectedOption(value);
    setShowLegend(false);
  };

  const toggleLegend = () => {
    setShowLegend(!showLegend);
  };

  const sortedAlerts = Object.values(weatherAlerts.alerts).sort((a, b) => new Date(b.sent) - new Date(a.sent));

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
              id="overlay"
              name="weatherOption"
              value="overlay"
              checked={selectedOption === 'overlay'}
              onChange={() => handleOptionChange('overlay')}
              className="mr-1"
            />
            <label>Weather Overlay</label>
          </div>
          <div>
            <input
              type="radio"
              id="radar"
              name="weatherOption"
              value="radar"
              checked={selectedOption === 'radar'}
              onChange={() => handleOptionChange('radar')}
              className="mr-1"
            />
            <label>Weather Radar</label>
          </div>
        </div>
      </div>

      {selectedOption === 'radar' && (
        <div className="mt-4">
          <label htmlFor="radarSelect" className="font-notosansjp text-custom-black font-semibold">Select Radar:</label>
          <select
            id="radarSelect"
            className="ml-2"
            value={selectedRadar}
            onChange={(e) => setSelectedRadar(e.target.value)}
          >
            <option value="">Choose A Radar</option>
          </select>
        </div>
      )}

      {selectedOption && (
        <div>
          <button
            className={`font-notosansjp text-custom-black font-semibold py-1 px-2 rounded-md mb-2 mt-20 ${
              showLegend ? 'bg-custom-green4' : 'bg-custom-green3'
            } hover:bg-custom-green4`}
            onClick={toggleLegend}
          >
            {showLegend ? 'Hide' : 'Show'} Legend
          </button>

          {showLegend && selectedOption === 'overlay' && (
            <div className="mt-4" style={{ width: '300px' }}>
              <p className="font-notosansjp text-custom-black">Weather Overlay Legend:</p>
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

          {showLegend && selectedOption === 'radar' && (
            <div>
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
            </div>
          )}

        </div>
      )}

      <div className="mt-20">
        <p className="font-notosansjp text-custom-black font-semibold">Alerts Along Your Route:</p>
        <div className="alert-container" style={{ maxHeight: '80vh', width: '300px', overflowY: 'auto' }}>
          {sortedAlerts.map((alert, index) => (
            <div key={index}>
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
          ))}
        </div>
      </div>
    </div>
  );
};

export default Weather;
