import React, { useRef, useState } from 'react';
import { Autocomplete } from '@react-google-maps/api';

export const AutoComplete = ({ handlePlaceSelect, label }) => {
  const [inputValue, setInputValue] = useState('');
  const [error, setError] = useState(null);

  const autoCompleteRef = useRef(null);

  const onLoad = (autocomplete) => {
    autoCompleteRef.current = autocomplete;
  };

  const onPlaceChanged = () => {
    if (autoCompleteRef.current !== null) {
      const place = autoCompleteRef.current.getPlace();

      if (place && place.formatted_address) {
        setInputValue(place.formatted_address);
        handlePlaceSelect(place);
        setError(null);
      } else {
        console.error('Invalid location entered.');
        setError('Invalid location entered.');
      }
    } else {
      console.error('Autocomplete is not loaded yet.');
      setError('Autocomplete is not loaded yet.');
    }
  };

  const handleInputChange = (event) => {
    setInputValue(event.target.value);
    setError(null);
  };

  return (
    <div className='flex flex-column'>
      <label>{label}</label>
      <Autocomplete onLoad={onLoad} onPlaceChanged={onPlaceChanged}>
        <input value={inputValue} onChange={handleInputChange} />
      </Autocomplete>
      {error && <p style={{ color: 'red', fontSize: '12px', margin: '0', marginLeft: '5px' }}>{error}</p>}
    </div>
  );
};