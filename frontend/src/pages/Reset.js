import React from 'react';

const ResetPassword = () => {
  return (
    <div className="flex flex-col min-h-screen justify-center items-center bg-custom-green p-5 m-11">
      <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
        <h1 className="text-3xl font-notosansjp font-bold text-custom-black mb-4">Reset Password</h1>
        <form className="space-y-4">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email Address
            </label>
            <input
              type="email"
              id="email"
              name="email"
              className="mt-1 p-2 block w-full rounded-md border-gray-300 focus:border-custom-green focus:ring focus:ring-custom-green focus:ring-opacity-50"
              placeholder="Your email address"
            />
          </div>
           <button className="w-full bg-custom-green3 font-notosansjp font-bold border border-custom-green2 rounded-full px-6 py-2 md:px-8 md:py-3 hover:bg-custom-green hover:text-white transition duration-300 ease-in-out">
           Reset Password
          </button>
        </form>
      </div>
    </div>
  );
};

export default ResetPassword;
