import React, { useState } from 'react';

const Weather = () => {
  const [selectedOption, setSelectedOption] = useState(null);
  const [showLegend, setShowLegend] = useState(false);
  const [selectedRadar, setSelectedRadar] = useState('');

  const handleOptionChange = (value) => {
    setSelectedOption(value);
    setShowLegend(false);
  };

  const toggleLegend = () => {
    setShowLegend(!showLegend);
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
