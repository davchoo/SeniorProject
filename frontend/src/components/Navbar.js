import React from 'react';

const Navbar = () => {
  return (
    <nav className="rounded-sm shadow bg-custom-green2">
      <div className="w-full mx-auto max-w-screen-xl p-6 md:flex md:items-center md:justify-between">
        <span className="text-sm text-custom-black sm:text-center dark:text-custom-grain">
          TripEase
        </span>

        {/* Center aligned */}
        <ul className="flex md:flex-row mt-3 text-sm font-medium text-custom-black dark:text-custom-grain list-none space-x-8">
          <li>
            <a href="/" className="hover:underline">
              Home
            </a>
          </li>
          <li>
            <a href="/" className="hover:underline">
              About
            </a>
          </li>
          <li>
            <a href="/" className="hover:underline">
              Plan Your Trip
            </a>
          </li>
          <li>
            <a href="/" className="hover:underline">
              Contact
            </a>
          </li>
        </ul>

        {/* Right aligned */}
        <ul className="hidden md:flex flex-wrap items-center mt-3 text-sm font-medium text-custom-black dark:text-custom-grain list-none space-x-4 md:space-x-8">
          <li>
            <button className="bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
              Login
            </button>
          </li>
          <li>
            <button className="bg-custom-green3 text-custom-green3 border border-custom-green2 rounded-md px-3 py-1 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
              Sign Up
            </button>
          </li>
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;
