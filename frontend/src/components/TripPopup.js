import React, { useState, useEffect } from 'react';
import ReactStars from "react-rating-stars-component";

export const TripPopup = ({ isVisible, trip, setIsVisible }) => {

  const [expandedReviews, setExpandedReviews] = useState({});

  const toggleReviews = (stationId) => {
    setExpandedReviews((prev) => ({
      ...prev,
      [stationId]: !prev[stationId],
    }));
  };

  const handleClose = (e) => {
    if (e.target.id === 'wrapper') setIsVisible(false);
  };

  if (!isVisible) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-25 backdrop-blur-sm flex items-center justify-center z-20" id="wrapper" onClick={(e) => handleClose(e)}>
      <div className="bg-white p-8 overflow-auto max-h-[70vh] max-w-[80vw] rounded-lg ">
        <button className="text-2xl absolute top-2 right-2 text-red-500 font-bold" onClick={() => setIsVisible(false)}>
          X
        </button>
        <div>
          <h1 className="text-xl font-bold mb-2">Trip Details</h1>
          <div className="flex flex-col">
            <p className="mb-2">
              <span className="font-bold">Origin:</span> {trip.origin}
            </p>
            <p className="mb-2">
              <span className="font-bold">Destination:</span> {trip.destination}
            </p>
            <p className="mb-2">
              <span className="font-bold">Distance in Miles:</span> {trip.distance}
            </p>
            <p className="mb-2">
              <span className="font-bold">Duration:</span> {trip.duration}
            </p>
          </div>
        </div>
        <div className="border-t border-gray-200 ">
          <div className="flex flex-col">
            <p className="mb-2">
              <span className="font-bold">Car:</span> {trip.car.year} {trip.car.make} {trip.car.model}
            </p>
            <p className="mb-2">
              <span className="font-bold">Calculated MPG:</span> {trip.car.milesPerGallon}
            </p>
            <p className="mb-2">
              <span className="font-bold">Calculated Tank Size:</span> {trip.car.tankSizeInGallons}
            </p>
            <p className="mb-2">
              <span className="font-bold">Fuel Type:</span> {trip.car.fuelType}
            </p>
            <p className="mb-2">
              <span className="font-bold">Total Trip Price:</span> 💲{trip.totalTripGasPrice * trip.car.tankSizeInGallons}
            </p>
          </div>
        </div>
        <div className="border-t border-gray-200 pt-2">
          <h2 className="text-lg font-bold mb-2">Cheapest Gas Stations</h2>
          <div className="flex flex-col">
            {trip.gasStations.map((station, index) => (
              <div key={index} className="mb-4">
                <p className="font-bold text-lg">⛽️{station.name}⛽️</p>
                <ReactStars value={station.rating} count={5} activeColor="#ffd700" size={16} edit={false} />
                <p>{station.formattedAddress}</p>
                {station.reviews && station.reviews.length > 0 ? (
                expandedReviews[station.id] ? (
                  <>
                    {station.reviews.map((review, reviewIndex) => (
                      <div key={reviewIndex} className="mb-2">
                        Rating: <ReactStars value={review.rating} count={5} activeColor="#ffd700" size={12} edit={false} />
                        <p>{review.text?.text}</p>
                      </div>
                    ))}
                    <button className="text-blue-500" onClick={() => toggleReviews(station.id)}>Show Less</button>
                  </>
                ) : (
                  <button className="text-blue-500" onClick={() => toggleReviews(station.id)}>Show Reviews</button>
                )) : <p><strong>There are no reviews for this gas station yet.</strong></p>}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
    
}
