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
    <div style={{ marginTop: '20px', position: 'relative', fontFamily: 'Noto Sans JP', fontSize: '14px', display: 'flex', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', }}>
        <p style={{ marginLeft: '10px' }}> Select Weather View:</p>
        <div style={{ display: 'flex', marginBottom: '10px', marginTop:'10px' }}>
          <div style={{ marginLeft: '10px', marginRight: '20px' }}>
            <input
              type="radio"
              id="overlay"
              name="weatherOption"
              value="overlay"
              checked={selectedOption === 'overlay'}
              onChange={() => handleOptionChange('overlay')}
              style={{ marginRight: '5px' }}
            />
            <label> Weather Overlay </label>
          </div>
          <div>
            <input
              type="radio"
              id="radar"
              name="weatherOption"
              value="radar"
              checked={selectedOption === 'radar'}
              onChange={() => handleOptionChange('radar')}
              style={{ marginRight: '5px' }}
            />
            <label> Weather Radar </label>
          </div>
        </div>

        {selectedOption === 'radar' && (
          <div style={{ marginTop: '15px', marginBottom: '20px', marginLeft: '30px' }}>
            <label style={{ fontWeight: 'bold', fontSize: '14px', color: '#000' }}>Select A Radar View:</label>
            <select value={selectedRadar} onChange={(e) => setSelectedRadar(e.target.value)} style={{ marginLeft: '10px', fontSize: '14px' }}>
              <option value="">Radar View</option>
            </select>
          </div>
        )}

        <div style={{ position: 'absolute', left: '10px', bottom: '-10px', top: '180px' }}>
          <p>Alerts Along Your Route:</p>
        </div>
      </div>

      <div style={{ marginTop: '310px', marginLeft: '220px'}}>
        <button
          className={`font-notosansjp font-extrabold text-custom-black py-1 px-2 rounded-md mb-2 ${showLegend ? 'bg-custom-green4' : 'bg-custom-green3'} hover:bg-custom-green4`}
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
