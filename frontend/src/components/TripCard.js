import React from 'react'

export const TripCard = ({ trip, setOpen, setTrip }) => {
  const handleCardClick = () => {
    setTrip(trip);
    setOpen(true);
  };
  return (
    <div className="border border-gray-400 rounded-lg shadow-md p-4 m-2 bg-white" onClick={handleCardClick} >
      <div className="flex flex-col">
        <p className="text-md font-semibold mb-2">Trip {trip.id} Details</p>
        <p className="mb-1 text-xs"><strong>Origin:</strong> {trip.origin}</p>
        <p className="mb-1 text-xs"><strong>Destination:</strong> {trip.destination}</p>
      </div>
    </div>
  )
}
