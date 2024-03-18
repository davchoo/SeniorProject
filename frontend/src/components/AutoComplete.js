import React, { useRef, useState } from 'react';
import { Autocomplete } from '@react-google-maps/api';

export const AutoComplete = ({ handlePlaceSelect, label }) => {
  const [inputValue, setInputValue] = useState('');
  const [error, setError] = useState(null);
  const [validInput, setValidInput] = useState(false);

  const autoCompleteRef = useRef(null);

  const onLoad = (autocomplete) => {
    autoCompleteRef.current = autocomplete;
  };

  const validatePlace = (place) => {
    if (!place || !place.formatted_address) {
      console.error('Invalid location entered.');
      setError('Invalid location entered.');
      setValidInput(false);
      return;
    }

    setInputValue(place.formatted_address);
    handlePlaceSelect(place);
    setError(null);
    setValidInput(true);
  };

  const handleInputChange = (event) => {
    setInputValue(event.target.value);
    setError(null); 
    setValidInput(false); 
  };

  const handlePlaceChanged = () => {
    const place = autoCompleteRef.current.getPlace();
    validatePlace(place);
  };

  const handleInputBlur = () => {
    if (inputValue.trim() !== '' && !validInput) {
      setError('Invalid location entered.');
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      if (inputValue.trim() === '') {
        setError('Invalid location entered.');
      } else {
        const place = autoCompleteRef.current.getPlace();
        validatePlace(place);
      }
    }
  };

  return (
    <div className='flex flex-column'>
      <label>{label}</label>
      <Autocomplete onLoad={onLoad} onPlaceChanged={handlePlaceChanged}>
        <input
          value={inputValue}
          onChange={handleInputChange}
          onBlur={handleInputBlur}
          onKeyPress={handleKeyPress}
        />
      </Autocomplete>
      {!validInput && error && (
        <p style={{ color: 'red', fontSize: '12px', margin: '0', marginLeft: '5px' }}>{error}</p>
      )}
    </div>
  );
};
