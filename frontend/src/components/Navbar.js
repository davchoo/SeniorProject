import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { GiCompass } from "react-icons/gi";
import { logout } from '../AuthContext';
import { checkIsLoggedIn } from '../AuthContext';

// Import statements...

// Import statements...

const Navbar = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLoggedInStatus = async () => {
      try {
        const isLoggedIn = await checkIsLoggedIn();
        console.log("isLoggedIn:", isLoggedIn);
        setLoggedIn(isLoggedIn);
      } catch (error) {
        console.error('Error checking login status:', error);
      } finally {
        setLoading(false);
      }
    };
  
    fetchLoggedInStatus();
  }, []);

  const isLoggedIn = () => {
    return loggedIn;
  }

  if (loading || loggedIn===undefined) {
    return <div>Loading...</div>;
  }

  return (
    <nav className="rounded-sm shadow bg-custom-green2">
      <div className="w-full mx-auto max-w-screen-xl p-6 flex flex-col md:flex-row md:items-center md:justify-between">
        {/* Left aligned */}
        <div className="md:flex md:items-center">
          <GiCompass className="text-custom-black text-2xl" />
          <span className="ml-2 text-md font-courgette text-custom-black sm:text-center">
            TripEase
          </span>
        </div>

        {/* Center aligned */}
        <ul className="flex md:flex-row md:justify-center mt-3 text-sm font-notosansjp font-bold text-custom-black list-none space-x-8">
        <li>
            <Link to="/" className="hover:underline">
              Home
            </Link>
          </li>
          <li>
            <Link to="/about" className="hover:underline">
              About
            </Link>
          </li>
          <li>
            <Link to="/plan" className="hover:underline">
              Plan Your Trip
            </Link>
          </li>
          <li>
            <Link to="/contact" className="hover:underline">
              Contact
            </Link>
          </li>
        </ul>

        {/* Right aligned */}
        {!loggedIn ? (
         
          <div className="md:flex mt-3 text-sm font-notosansjp font-bold text-custom-black space-x-4 md:space-x-8">
            <Link to="/login" className="hover:underline">
              <button className="bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
                Login
              </button>
            </Link>
            <Link to="/signup" className="hover:underline">
              <button className="bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
                Sign Up
              </button>
            </Link>
          </div>
        ) : (
          <div className="md:flex mt-3 text-sm font-notosansjp font-extrabold text-custom-black space-x-4 md:space-x-8">
            

            <text className=' font-large'>Welcome {loggedIn.firstName + " " + loggedIn.lastName}!</text>
            
          <button
            onClick={() => {
              logout();
              setLoggedIn(false);
            }}
            className="bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out"
          >
            Logout
          </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
export const loggedIn = Navbar.isLoggedIn;
