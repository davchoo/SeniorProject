import React, { useState } from 'react';
import { PiUserRectangleDuotone } from "react-icons/pi";
import { FaEye, FaEyeSlash } from 'react-icons/fa';
import { ImCross, ImCheckmark } from 'react-icons/im';
import { signup } from '../AuthContext';
import { useNavigate } from 'react-router-dom';

const SignUp = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [passwordStrength, setPasswordStrength] = useState({ isValid: null, message: '' });
  const navigate = useNavigate();

  const togglePasswordVisibility = () => {
    setPasswordVisible(prevVisible => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  const checkPasswordStrength = (password) => {
    const passwordPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/;
    
    console.log('Password pattern:', passwordPattern); 
  
    const isValid = passwordPattern.test(password);
    console.log('Password is valid:', isValid); 
    
    setPasswordStrength({
      isValid: isValid,
      message: isValid ? '' : 'Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.'
    });
  };
  
  const handlePasswordChange = (e) => {
    console.log('Password changed:', e.target.value);
    const newPassword = e.target.value;
    setPassword(newPassword);
    checkPasswordStrength(newPassword);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (passwordStrength.isValid) {
      signup(firstName, lastName, username, password, navigate);
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-custom-green justify-center items-center m-10 relative">
      <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
        <h1 className="text-4xl md:text-5xl font-notosansjp font-bold text-center text-custom-black md:mb-5">Sign Up</h1>
        <div className="text-center mb-5">
          <PiUserRectangleDuotone size={80} color="black" />
        </div>
        <h2 className="text-3xl font-notosansjp font-extrabold text-center mt-0 mb-10 text-custom-black">Thank You for Choosing TripEase!</h2>
        <p className="font-notosansjp text-custom-black mb-5">Create An Account</p>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <input type="text" id="firstName" name="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} placeholder="First Name" required />
          <input type="text" id="lastName" name="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} placeholder="Last Name" className="input-field" required />
          <input type="text" id="username" name="username" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" className="input-field" required />
          <div className="relative flex items-center">
            <input type={inputType} id="password" name="password" value={password} onChange={handlePasswordChange} placeholder="Password" required />
            {passwordStrength.isValid !== null && (
              <div className="absolute inset-y right-0 flex items-center pr-3">
                {passwordStrength.isValid ? (
                  <ImCheckmark className="text-green-500" />
                ) : (
                  <ImCross className="text-red-500" />
                )}
              </div>
            )}
            <button type="button" onClick={togglePasswordVisibility} className="absolute inset-y ml-44 flex px-3 text-grey">
              {passwordVisible ? <FaEye size={20} /> : <FaEyeSlash size={20} />}
            </button>
          </div>
          {passwordStrength.message && (
            <div className="text-xs text-red-500 mt-1">{passwordStrength.message}</div>
          )}
          <button className={`w-full py-2 px-4 bg-custom-green3 text-custom-black font-notosansjp font-bold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white ${passwordStrength.isValid === false ? 'opacity-50 cursor-not-allowed' : ''}`} type="submit" disabled={passwordStrength.isValid === false}>
            Register
          </button>
        </form>
      </div>
    </div>
  );
};

export default SignUp;
