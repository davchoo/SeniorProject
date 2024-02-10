import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaEye, FaEyeSlash, FaLuggageCart, FaCar } from 'react-icons/fa';

const Login = () => {
  const [passwordVisible, setPasswordVisible] = useState(false);

  const togglePasswordVisibility = () => {
    setPasswordVisible((prevVisible) => !prevVisible);
  };

  const inputType = passwordVisible ? 'text' : 'password';

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
        <form>
          <div className="mb-4">
            <p className="font-notosansjp text-custom-black whitespace-pre-line mb-5">
              Login to continue your planning journey.
            </p>
            <label htmlFor="username" className="block text-sm font-medium text-grey">
              Username
            </label>
            <div className="relative">
              <input
                type="text"
                id="username"
                placeholder="Your username"
                className="input-field"
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="password" className="block text-sm font-medium text-grey">
              Password
            </label>
            <div className="relative">
              <input
                type={inputType}
                id="password"
                placeholder="Your password"
                className="input-field pr-10"
                required
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute inset-y-0 ml-56 flex px-3 text-grey"
              >
                {passwordVisible ? <FaEye size={20} /> : <FaEyeSlash size={20} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2 px-4 bg-custom-green3 text-custom-black font-semibold rounded-lg shadow-md hover:bg-custom-green hover:text-white focus:outline-none focus:bg-custom-green focus:text-white"
          >
            Login
          </button>
        </form>

        <div className="mt-4 text-sm text-grey">
          <p>
            Don't have an account?{' '}
            <Link to="/signup" className="font-medium text-custom-green hover:underline">
              Sign Up Here.
            </Link>
          </p>
          <p className="text-grey">
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
