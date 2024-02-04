import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Icon } from 'react-icons-kit';
import { eyeOff } from 'react-icons-kit/feather/eyeOff';
import { eye } from 'react-icons-kit/feather/eye';
import zion from '../images/zion.png';
import hawaii from '../images/hawaii.png';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [loading, setLoading] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible((prevVisible) => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  const handleUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    setLoading(true);

    // In a real application, you would typically perform the actual login here.
    setLoading(false);
  };

  return (
    <div className="flex flex-col min-h-screen bg-custom-green m-10">
      <div className="flex-grow center-content relative flex flex-col md:flex-row items-center">
        {/* Move the images to the right */}
        <div className="hidden md:flex flex-col items-end md:mr-5">
          <img src={zion} alt="zion" className="w-full md:w-3/4 md:h-auto mx-auto rounded-md shadow-md" />
          <img src={hawaii} alt="hawaii" className="w-full md:w-3/4 md:h-auto mx-auto rounded-md shadow-md mt-5" />
        </div>

        <div className="text-center md:text-left md:ml-10 md:mr-10 mt-10 md:mt-0">
          <h1 className="text-4xl font-bold mt-20 mb-10 text-custom-black">Welcome Back!</h1>
          <p className="font-notosansjp text-custom-black mb-5">
            Log in to continue your planning journey.
          </p>

          {/* Login Section */}
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
                className={`button ${loading && 'opacity-50 cursor-not-allowed'}`}
                disabled={loading}
              >
                {loading ? 'Logging In...' : 'Log In'}
              </button>
            </form>
            <p className="text-sm text-custom-black mt-4">
              Don't have an account?{' '}
              <Link to="/signup" className="text-custom-green hover:underline">
                Sign Up Here
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

          {/* Featured Content Section */}
          <div className="mt-10">
            <h2 className="text-2xl font-bold mb-4 text-custom-black">Featured Content</h2>
            <p className="text-custom-black">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi.
              Sed ac lacus quam. Proin nec risus vel arcu ultricies luctus.
            </p>
            {/* Add more content as needed */}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
