import React from 'react';
import { Link } from 'react-router-dom';
import acadia from '../images/acadia.png';

const Home = () => {
  return (
    <div className="flex flex-col min-h-screen bg-custom-green m-10">
      <div className="flex-grow center-content relative flex flex-col md:flex-row justify-center items-center md:items-start">
        <div className="text-center md:text-left md:ml-10 md:mr-20 mb-10 md:mb-0 mt-20">
          <h1 className="mb-8 md:mb-10 mt-16 font-courgette text-6xl md:text-7xl text-black outlined-text">TripEase</h1>
          <p className="font-notosansjp font-bold mb-8 mt-6 text-xl md:text-2xl text-custom-white">Simplify Your Journey with Effortless Trip Planning.</p>
          <div className="mt-4">
            <Link to="/plan" className="hover:underline">
              <button className="bg-custom-green3 font-notosansjp font-bold border border-custom-green2 rounded-full px-6 py-2 mt-6 md:px-8 md:py-3 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
                Start Planning Your Trip Today!
              </button>
            </Link>
          </div>
        </div>
        <img
          src={acadia}
          alt="Acadia"
          className="w-full md:w-1/2 md:h-auto mx-auto mt-3 md:mx-0"
        />
      </div>

        {/* Why Section */}
        <div className="bg-white py-15 mt-4 ml-4 mr-4">
        <div className="container mx-auto">
          <h2 className="text-3xl font-notosansjp mb-6 ml-2">Why Choose TripEase?</h2>
          <div className="flex flex-wrap justify-around">
            {/* First */}
            <div className="w-full md:w-1/3 mb-8">
              <h3 className="text-xl font-notosansjp font-bold mb-4 ml-2">Efficient Planning</h3>
              <p className="text-grey ml-2">
                Easily plan your trip with our user-friendly tools and features. This application is for individuals at all levels of travel experience.
              </p>
            </div>
            {/* Second */}
            <div className="w-full md:w-1/3 mb-8">
              <h3 className="text-xl font-notosansjp font-bold mb-4 ml-2">Streamlined Services</h3>
              <p className="text-grey ml-2 font-roboto mr-2">
                TripEase integrates with various APIs to provide users with convenient access to options within their budget. TripEase offers real-time weather updates and locates cost-effective gas stops along the route, empowering users to make informed decisions without sacrificing convenience.
              </p>
            </div>
            {/* Third */}
            <div className="w-full md:w-1/3 mb-8">
              <h3 className="text-xl font-notosansjp font-bold mb-4 ml-2">Travel Assistance</h3>
              <p className="text-grey ml-2 font-roboto mr-4">
                TripEase provides real-time weather updates and identifies cost-effective gas stops along your route. Stay informed about unpredictable weather conditions, and plan your journey efficiently by choosing budget-friendly gas stations. Our aim is to empower users with the information they need to make informed decisions without sacrificing convenience.
              </p>
            </div>
          </div>
          <div className="text-center font-notosansjp font-bold mt-4">
            <div className="mx-auto max-w-xs">
              <Link to="/about" className="hover:underline">
                <button className="w-full bg-custom-green3 font-notosansjp font-bold border border-custom-green2 px-6 py-2 md:px-8 md:py-3 mb-4 mt-2 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
                  Read More
                </button>
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Testimonials */}
      <div className="bg-custom-green py-10 text-custom-white">
        <div className="container mx-auto">
          <h2 className="text-3xl font-notosansjp font-bold mt-0 mb-6 ml-4">What Our Users Say</h2>
          <div className="flex flex-wrap justify-around">
            {/* First Testimonial */}
            <div className="w-full md:w-1/2 mb-8">
              <blockquote className="italic mb-4">
                "TripEase made our vacation planning really easy and enjoyable!"
              </blockquote>
              <p className="font-notosansjp ml-2"> - Happy Traveler</p>
            </div>
            {/* Second Testimonial */}
            <div className="w-full md:w-1/2 mb-8">
              <blockquote className="italic mb-4">
                "This is the best travel planning app I've ever used. It saved me so much time!"
              </blockquote>
              <p className="font-notosansjp ml-2"> - Satisfied Explorer</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
