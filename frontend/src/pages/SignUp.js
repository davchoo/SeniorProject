import React, { useState } from 'react';
import { PiUserRectangleDuotone } from "react-icons/pi";
import { FaEye, FaEyeSlash, } from 'react-icons/fa';

const SignUp = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible(prevVisible => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  return (
    <div className="flex flex-col min-h-screen bg-custom-green justify-center items-center m-10 relative">
      <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
        <h1 className="text-4xl md:text-5xl font-notosansjp font-bold text-center text-custom-black md:mb-5">Sign Up</h1>
        <div className="text-center mb-5">
          <PiUserRectangleDuotone size={80} color="black" /> 
        </div>
        <h2 className="text-3xl font-notosansjp font-extrabold text-center mt-0 mb-10 text-custom-black">Thank You for Choosing TripEase!</h2>
        <p className="font-notosansjp text-custom-black mb-5">
          Create An Account
        </p>
        <form className="space-y-4">
          <div className="grid grid-cols-2 mr-24">
            <div>
              <label htmlFor="firstName" className="sr-only">First Name</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={e => setFirstName(e.target.value)}
                placeholder="First Name"
                className="input-field"
                required
              />
            </div>
            <div className="md:ml-2"> {/* Adjusted margin */}
              <label htmlFor="lastName" className="sr-only">Last Name</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={e => setLastName(e.target.value)}
                placeholder="Last Name"
                className="input-field"
                required
              />
            </div>
          </div>

          <div className="relative">
            <label htmlFor="username" className="sr-only">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="Username"
              className="input-field"
              required
            />
          </div>

          <div className="relative flex items-center">
            <label htmlFor="password" className="sr-only">Password</label>
            <input
              type={inputType}
              id="password"
              name="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="Password"
              className="input-field"
              required
            />
            <button
              type="button"
              onClick={togglePasswordVisibility}
              className="absolute inset-y ml-44 flex px-3 text-grey"
            >
             {passwordVisible ? <FaEye size={20} /> : <FaEyeSlash size={20} />}
            </button>
          </div>

          <button
            type="submit"
            className="w-full py-2 px-4 bg-custom-green3 text-custom-black font-notosansjp font-bold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white"
          >
          Register
          </button>
        </form>
        
      </div>
    </div>
  );
};

export default SignUp;
