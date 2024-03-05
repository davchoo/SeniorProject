import React, { useState } from 'react';

const Weather = () => {
  const [selectedOption, setSelectedOption] = useState(null);
  const [showLegend, setShowLegend] = useState(false);

  const handleOptionChange = (value) => {
    setSelectedOption(value);
  };

  const toggleLegend = () => {
    setShowLegend(!showLegend);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', marginTop: '20px' }}>
      <label>
        <input
          type="radio"
          name="weatherOption"
          value="overlay"
          checked={selectedOption === 'overlay'}
          onChange={() => handleOptionChange('overlay')}
        />
        Show Weather Overlay
      </label>

      <label style={{ marginTop: '10px' }}>
        <input
          type="radio"
          name="weatherOption"
          value="radar"
          checked={selectedOption === 'radar'}
          onChange={() => handleOptionChange('radar')}
        />
        Show Weather Radar
      </label>

      <div style={{ marginTop: '250px', marginLeft: 'auto', marginRight: '-400px' }}>
        <button
          className={`font-notosansjp font-extrabold text-custom-black ${showLegend ? 'bg-custom-green4' : 'bg-custom-green3'} py-1 px-2 rounded-md mb-2`}
          onClick={toggleLegend}
        >
          {showLegend ? 'Hide' : 'Show'} Legend
        </button>

        {showLegend && (
          <div style={{ marginTop: '10px', marginRight: '-400px'}}>
            <p style={{ color: '#333' }}>Legend Content Here</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Weather;
