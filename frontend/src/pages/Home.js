import React from 'react';
import { Link } from 'react-router-dom';
import acadia from '../images/acadia.png';

const Home = () => {
  return (
    <div className="flex flex-col min-h-screen bg-custom-green m-10">
      <div className="flex-grow center-content relative flex flex-col md:flex-row justify-center items-center md:items-start">
        <div className="text-center md:text-left md:ml-10 md:mr-60 mb-10 md:mb-0 mt-20">
          <h1 className="mb-50 font-courgette">TripEase</h1>
          <p className="font-notosansjp">Simplify Your Journey with Effortless Trip Planning.</p>
          <div className="mt-0">
          <Link to="/plan" className="hover:underline">
            <button className="bg-custom-green3 border border-custom-green2 rounded-full px-6 py-2 md:px-8 md:py-3 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
              Start Planning Your Trip Today!
            </button>
            </Link>
          </div>
        </div>
        <img
          src={acadia}
          alt="acadia"
          className="w-full md:w-1/2 md:h-auto mx-auto mt-3 md:mx-0"
        />
      </div>

      {/* Why Section */}
<div className="bg-white py-10 mt-4 ml-4 mr-4">
  <div className="container mx-auto">
    <h2 className="text-3xl font-semibold mb-6 ml-2">Why Choose TripEase?</h2>
    <div className="flex flex-wrap justify-around">
      {/* First Reason */}
      <div className="w-full md:w-1/3 mb-8">
        <h3 className="text-xl font-semibold mb-4 ml-2">Efficient Planning</h3>
        <p className="text-gray-600 ml-2"> Easily Plan your trip with our user-friendly tools and features.</p>
      </div>
      
    </div>
  </div>
</div>

      {/* Testimonials */}
      <div className="bg-custom-green py-10 text-white">
        <div className="container mx-auto">
          <h2 className="text-3xl font-semibold mb-6 ml-4">What Our Users Say</h2>
          <div className="flex flex-wrap justify-around">
            {/* First Testimonial */}
            <div className="w-full md:w-1/2 mb-8">
              <blockquote className="italic mb-4">
                "TripEase made our vacation planning really easy and enjoyable!"
              </blockquote>
              <p className="font-semibold"> - Happy Traveler</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
