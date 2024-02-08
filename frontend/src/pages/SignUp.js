import React, { useState } from 'react';
import { PiUserRectangleDuotone } from "react-icons/pi";
import { FaEye, FaEyeSlash, } from 'react-icons/fa';

const SignUp = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [loading, setLoading] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible(prevVisible => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  const handleFirstNameChange = e => {
    setFirstName(e.target.value);
  };

  const handleLastNameChange = e => {
    setLastName(e.target.value);
  };

  const handleUsernameChange = e => {
    setUsername(e.target.value);
  };

  const handleEmailChange = e => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = e => {
    setPassword(e.target.value);
  };

  const handleSubmit = e => {
    e.preventDefault();

    setLoading(true);

    setLoading(false);
  };

  return (
    <div className="flex flex-col min-h-screen bg-custom-green justify-center items-center m-10 relative">
      <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
        <h1 className="text-3xl md:text-5xl font-notosansjp font-bold text-center text-custom-black md:mb-5">Sign Up</h1>
        <div className="text-center mb-5">
          <PiUserRectangleDuotone  size={80} color="black" /> 
        </div>
        <h1 className="text-4xl font-notosansjp font-extrabold text-center mt-0 mb-10 text-custom-black">Thank You for Choosing TripEase!</h1>
        <p className="font-notosansjp text-custom-black mb-5">
          Create An Account
        </p>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="flex flex-col md:flex-row md:space-x-2">
            <div className="w-full md:w-1/2">
              <label htmlFor="firstName" className="sr-only">First Name</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={handleFirstNameChange}
                placeholder="First Name"
                className="input-field"
                required
              />
            </div>
            <div className="w-full md:w-1/2">
              <label htmlFor="lastName" className="sr-only">Last Name</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={handleLastNameChange}
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
              onChange={handleUsernameChange}
              placeholder="Username"
              className="input-field"
              required
            />
          </div>

          <div className="relative">
            <label htmlFor="email" className="sr-only">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={email}
              onChange={handleEmailChange}
              placeholder="Email Address"
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
              onChange={handlePasswordChange}
              placeholder="Password"
              className="input-field"
              required
            />
            <button
              type="button"
              onClick={togglePasswordVisibility}
              className="ml-2 focus:outline-none"
            >
             {passwordVisible ? <FaEyeSlash size={20} /> : <FaEye size={20} />}
            </button>
          </div>

          <button
            type="submit"
            className={`w-full py-2 px-4 bg-custom-green3 text-custom-black font-notosansjp font-semibold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white ${
              loading && 'opacity-50 cursor-not-allowed'
            }`}
            disabled={loading}
          >
            {loading ? 'Signing Up...' : 'Sign Up'}
          </button>
        </form>
        
      </div>
    </div>
  );
};

export default SignUp;
