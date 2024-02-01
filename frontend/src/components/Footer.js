import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-custom-green2 text-custom-black p-6">
      <div className="w-full mx-auto max-w-screen-xl flex flex-col md:flex-row md:items-center md:justify-between">
        <div className="text-sm mb-4 md:mb-0">
          <span> 2024 TripEase</span>
        </div>
        <div className="flex flex-wrap items-center text-sm font-medium space-x-4 md:space-x-8">
          <a href="/" className="hover:underline">
            About
          </a>
          <a href="/privacy-policy" className="hover:underline">
            Privacy Policy
          </a>
          <a href="/contact" className="hover:underline">
            Contact
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
