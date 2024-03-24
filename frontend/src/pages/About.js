import React from 'react';

function About() {
  return (
    <div className="flex justify-center items-center h-full">
      <div className="max-w-screen-md mx-auto p-8 bg-custom-green m-5"> 
        <h2 className="font-notosansjp font-bold text-center mb-12"> About TripEase</h2>
        <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
          <p className="text-lg leading-relaxed">
            TripEase is a web application tailored for individuals at all levels of travel experience to streamline the process of planning any trip. The anticipated sign-up process will guide users through the journey planning process, simplifying tasks from selecting departure and destination points to finalizing itineraries within an intuitive web interface. 
          </p>
          <p className="text-lg leading-relaxed">
          For budget-conscious travelers, TripEase will locate convenient and cost-effective gas stops along their route. The application will inform users about unpredictable weather conditions through real-time weather updates.
          </p>
          <p className="text-lg leading-relaxed">
          TripEase's integration with various APIs guarantees a seamless flow of current information, encompassing weather updates and gas prices. This comprehensive integration will enhance the efficiency of the web application, ensuring users have accurate and up-to-date insights for a more streamlined trip-planning experience.
          </p>
        </div>
      </div>
    </div>
  );
}

export default About;
