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

      <div style={{ position: 'absolute', left: '10px', bottom: '-10px', top: '250px' }}>
        <p className="text-m text-custom-black font-notosansjp">
        Alerts Along Your Route: 
            </p>
        </div>
      <div style={{ marginTop: '200px', marginLeft: 'auto', marginRight: '-400px' }}>
        <button
          className={`font-notosansjp font-extrabold text-custom-black py-1 px-2 rounded-md mb-2 ${showLegend ? 'bg-custom-green4' : 'bg-custom-green3'} ${selectedOption ? 'bg-custom-green4' : ''}`}
          onClick={toggleLegend}
        >
          {showLegend ? 'Hide' : 'Show'} Legend
        </button>

        {showLegend && (
          <div style={{ marginTop: '10px', marginRight: '-400px' }}>
            <p style={{ color: 'text_custom_black' }}>Legend Content Here</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Weather;
