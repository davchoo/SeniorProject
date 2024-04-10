import React, { useState, useEffect } from 'react';
import weatherAlerts from '../pages/weather_alert.json'; // Importing the JSON for testing  

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

        {selectedOption === 'radar' && (
          <div className="mt-4 ml-4">
            <label className="font-notosansjp text-custom-black font-semibold text-md">Select A Radar View:</label>
            <select
              value={selectedRadar}
              onChange={(e) => setSelectedRadar(e.target.value)}
              className="ml-2 text-md"
            >
              <option value="">Radar View</option>
            </select>
          </div>
        )}
      </div>

      <div className="mt-20">
        <p className="font-notosansjp text-custom-black font-semibold">Alerts Along Your Route:</p>
        <div className="alert-container" style={{ maxHeight: '80vh', width: '300px', overflowY: 'auto' }}>
          {Object.entries(sortedAlerts.reduce((acc, alert) => {
            if (!acc[alert.event]) {
              acc[alert.event] = [];
            }
            acc[alert.event].push(alert);
            return acc;
          }, {})).map(([event, alertsForEvent], index) => (
            <div key={index}>
              <p className="font-notosansjp text-custom-black font-semibold underline">{event}:</p>
              <ul className="list-disc pl-8">
                {alertsForEvent.map((alert, idx) => (
                  <li key={idx} className="mt-2">
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
                      {expandedAlert === alert ? 'Collapse Directions and Instructions' : 'Expand Directions and Instructions'}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>

      <div>
        <button
          className={`font-notosansjp text-custom-black font-semibold py-1 px-2 rounded-md mb-2 mt-20 ${
            showLegend ? 'bg-custom-green4' : 'bg-custom-green3'
          } hover:bg-custom-green4`}
          onClick={toggleLegend}
        >
          {showLegend ? 'Hide' : 'Show'} Legend
        </button>

        {showLegend && (
          <div className="mt-4">
            <p className="font-notosansjp text-custom-black">Legend Content Here</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Weather;
