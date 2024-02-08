import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaEye, FaEyeSlash, FaLuggageCart, FaCar } from 'react-icons/fa';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [loading, setLoading] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible(prevVisible => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

  const handleUsernameChange = e => {
    setUsername(e.target.value);
  };

  const handlePasswordChange = e => {
    setPassword(e.target.value);
  };

  const handleSubmit = e => {
    e.preventDefault();
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
    }, 2000);
  };

  return (
    <div className="flex flex-col min-h-screen bg-custom-green justify-center items-center m-10 relative">
      <div className="absolute bottom-10 right-20 mb-4 mr-2">
        <FaCar size={100} color="white" />
      </div>
      <div className="absolute bottom-10 left-20 mb-4 ml-4">
        <FaLuggageCart size={100} color="white" />
      </div>

      <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
        <h1 className="text-3xl md:text-5xl font-notosansjp font-bold text-center text-custom-black mb-6 md:mb-16">Login</h1>
        <h1 className="text-4xl font-notosansjp font-extrabold mt-20 mb-10 text-custom-black">Welcome Back!</h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <p className="font-notosansjp text-custom-black whitespace-pre-line mb-5">
              Login to continue your planning journey.
            </p>
            <label htmlFor="username" className="block text-sm font-medium text-gray-700">
              Username
            </label>
            <div className="relative">
              <input
                type="text"
                id="username"
                name="username"
                value={username}
                onChange={handleUsernameChange}
                placeholder="Your username"
                className="input-field"
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <div className="relative">
              <input
                type={inputType}
                id="password"
                name="password"
                value={password}
                onChange={handlePasswordChange}
                placeholder="Your password"
                className="input-field pr-10"
                required
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute inset-y-0 right-0 flex items-center px-3 text-gray-600"
              >
                {passwordVisible ? <FaEyeSlash size={20} /> : <FaEye size={20} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            className={`w-full py-2 px-4 bg-custom-green3 text-custom-black font-semibold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white ${
              loading && 'opacity-50 cursor-not-allowed'
            }`}
            disabled={loading}
          >
            {loading ? 'Logging In...' : 'Log In'}
          </button>
        </form>

        <div className="mt-4 text-sm">
          <p>
            Don't have an account?{' '}
            <Link to="/signup" className="font-medium text-custom-green hover:underline">
              Sign Up Here.
            </Link>
          </p>
          <p className="text-gray-600">
            Forgot your password?{' '}
            <Link to="/reset" className="font-medium text-custom-green hover:underline">
              Reset Password.
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
