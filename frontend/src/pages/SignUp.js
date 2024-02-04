import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Icon } from 'react-icons-kit';
import { eyeOff } from 'react-icons-kit/feather/eyeOff';
import { eye } from 'react-icons-kit/feather/eye';
import profile from '../images/profile.png';
import ca from '../images/ca.png';

const SignUp = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [termsAndConditions, setTermsAndConditions] = useState(false);
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [loading, setLoading] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible((prevVisible) => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  const handleUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handleTermsAndConditionsChange = () => {
    setTermsAndConditions((prevValue) => !prevValue);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    setLoading(true);

    // In a real application, you would typically perform the actual sign-up logic here.
    setLoading(false);
  };

  return (
    <div className="flex flex-col min-h-screen bg-custom-green m-10">
      <div className="flex-grow center-content relative flex flex-col md:flex-row items-center">
        <div className="hidden md:flex flex-col items-end md:mr-5 md:w-1/2">
          <div className="relative w-1/2 h-2/3 mx-auto mb-5 overflow-hidden">
            <img src={profile} alt="logo" className="w-full h-full object-cover rounded-full" />
          </div>
          <img src={ca} alt="ca" className="w-full h-auto mx-auto rounded-md shadow-md" />
        </div>

        {/* Sign Up Section */}
        <div className="text-center md:text-left md:ml-10 md:mr-10 mt-10 md:mt-0 md:w-1/2">
          <h1 className="text-4xl font-bold mt-20 mb-10 text-custom-black">Thank You for Choosing TripEase!</h1>
          <p className="font-notosansjp text-custom-black mb-5">
            Create An Account
          </p>

          <div className="mt-10">
            <p className="text-custom-black">
              By creating an account with us, you can access ...
            </p>
            {/* Add more content as needed */}
          </div>

          {/* Sign Up Form */}
          <div className="bg-white p-8 rounded-md shadow-md">
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="relative flex items-center">
                <label htmlFor="username" className="sr-only">
                  Username
                </label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={username}
                  onChange={handleUsernameChange}
                  placeholder="Username"
                  className="input-field"
                />
              </div>

              <div className="relative flex items-center">
                <label htmlFor="email" className="sr-only">
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={email}
                  onChange={handleEmailChange}
                  placeholder="Email Address"
                  className="input-field"
                />
              </div>

              <div className="relative flex items-center">
                <label htmlFor="password" className="sr-only">
                  Password
                </label>
                <input
                  type={inputType}
                  id="password"
                  name="password"
                  value={password}
                  onChange={handlePasswordChange}
                  placeholder="Password"
                  className="input-field"
                />
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="ml-2 focus:outline-none"
                >
                  <Icon icon={passwordVisible ? eye : eyeOff} size={25} />
                </button>
              </div>

              <button
                type="submit"
                className={`button-primary ${loading && 'opacity-50 cursor-not-allowed'}`}
                disabled={loading || !termsAndConditions}
              >
                {loading ? 'Signing Up...' : 'Sign Up'}
              </button>
            </form>
            <p className="text-sm text-custom-black mt-4">
              Already have an account?{' '}
              <Link to="/login" className="text-custom-green hover:underline">
                Log In Here
              </Link>
            </p>

            {/* Additional Message or Link */}
            <p className="text-sm text-gray-600 mt-2">
              Forgot your password?{' '}
              <Link to="/forgot-password" className="text-custom-green hover:underline">
                Reset it here.
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignUp;
