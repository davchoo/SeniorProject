import React, { useRef } from 'react';
import { Autocomplete } from '@react-google-maps/api';

export const AutoComplete = ({ handlePlaceSelect, label }) => {
  const autoCompleteRef = useRef(null);

  const onLoad = (autocomplete) => {
    autoCompleteRef.current = autocomplete;
  };

  const onPlaceChanged = () => {
    if (autoCompleteRef.current !== null) {
      const place = autoCompleteRef.current.getPlace();
      handlePlaceSelect(place);
    } else {
      console.error('Autocomplete is not loaded yet.');
    }
  };

  return (
    <div className='flex flex-row'>
      <label>{label}</label>
      <Autocomplete onLoad={onLoad} onPlaceChanged={onPlaceChanged}>
        <input />
      </Autocomplete>
    </div>
  );
};
