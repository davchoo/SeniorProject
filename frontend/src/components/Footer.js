// components/Footer.js
import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-custom-green2 text-custom-black p-6">
      <div className="w-full mx-auto max-w-screen-xl flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="text-sm font-courgette mb-4 md:mb-0">
          <span> 2024 TripEase</span>
        </div>
        <div className="flex flex-wrap items-center text-sm font-notosansjp font-bold space-x-4 md:space-x-8">
          <Link to="/about" className="hover:underline">
            About
          </Link>
          <Link to="/policy" className="hover:underline">
            Privacy Policy
          </Link>
          <Link to="/contact" className="hover:underline">
            Contact
          </Link>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
