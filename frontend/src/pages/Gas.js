// Gas.js
import React from 'react';

const Gas = ({ showGasInfo, onToggleGasInfo }) => {
  return (
    <div style={{ marginBottom: '20px', marginTop: '20px', display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
      <button
        type="button"
        onClick={onToggleGasInfo}
        className="w-full mt-5 py-0 px-1 bg-custom-green3 text-custom-black font-notosansjp font-bold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white"
      >
        Show Gas Stations
      </button>
      {showGasInfo && (
        <div>
          {/* This is where we can try and add some of the gas station information */}
          Gas Station Information
        </div>
      )}
    </div>
  );
};

export default Gas;
