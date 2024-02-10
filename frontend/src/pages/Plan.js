import React from 'react';
import Map from '../components/Map';

function Plan() {
  return (
    <div className="min-h-screen bg-custom-green flex justify-center items-center font-notosansjp font-bold m-1">
      <div className="w-full max-w-screen-xl">
        <div className="flex flex-col min-h-screen justify-center items-center m-2 relative">
          <p className="mt-4 text-center text-2xl text-custom-black">
            Let's Start Planning!
          </p>

          <div className="max-w-md w-full px-0 py-0 mb-3 bg-white rounded-lg shadow-md">
            <div className="text-center text-sm text-custom-black mt-2">
              <p>Choose your departure and destination points below:</p>
            </div>
          </div>
          <Map />
        </div>
      </div>
    </div>
  );
}

export default Plan;
