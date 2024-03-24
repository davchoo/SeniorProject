import React from 'react';

function Contact() {
  return (
    <div className="flex justify-center items-center h-full">
      <div className="max-w-screen-md mx-auto p-8 bg-custom-green m-5"> 
        <h2 className="font-notosansjp text-2xl text-center font-bold mb-12">Contact Information</h2>
        <div className="max-w-md w-full px-8 py-6 bg-white rounded-lg shadow-md">
          <p className="text-lg leading-relaxed text-gray-700"> 
            Thank you for your interest in TripEase! If you have any questions or need assistance, feel free to reach out to us using the contact information below:
          </p>

          <div className="mt-4">
            <ul className="list-disc">
              <li className="text-lg"> Emma Zimmerman (<a href="https://github.com/EmmaZim">GitHub</a>, <a href="mailto:emma.ar.zimmerman@gmail.com">emma.ar.zimmerman@gmail.com</a>)</li>
              <li className="text-lg"> David Choo (<a href="https://github.com/davchoo">GitHub</a>)</li>
              <li className="text-lg"> Lukas DeLoach (<a href="https://github.com/lukasdeloach">GitHub</a>)</li>
              <li className="text-lg"> Kaan Kayis (<a href="https://github.com/kaankayis22">GitHub</a>)</li>
              <li className="text-lg"> Yekaterina Saburova (<a href="https://github.com/KatSaburova">GitHub</a>, <a href="mailto:yeksbr@gmail.com">yeksbr@gmail.com</a>)</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Contact;
