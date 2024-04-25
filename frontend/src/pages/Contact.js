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
            <li className="text-lg"> Emma Zimmerman (<a href="https://github.com/EmmaZim">GitHub</a>, <a href="https://www.linkedin.com/in/emma-zimmerman-4aa317267/">LinkedIn</a>)</li>
              <li className="text-lg"> David Choo (<a href="https://github.com/davchoo">GitHub</a>, <a href="https://www.linkedin.com/in/choo-david">LinkedIn</a>)</li>
              <li className="text-lg"> Lukas DeLoach (<a href="https://github.com/lukasdeloach">GitHub</a>, <a href="https://www.linkedin.com/in/lukas-deloach/">LinkedIn</a>)</li>
              <li className="text-lg"> Kaan Kayis (<a href="https://github.com/kaankayis22">GitHub</a>, <a href="https://www.linkedin.com/in/kaan-kayis-b6b708266/">LinkedIn</a>)</li>
              <li className="text-lg"> Yekaterina Saburova (<a href="https://github.com/KatSaburova">GitHub</a>, <a href="https://www.linkedin.com/in/yekaterina-saburova-0aa257242/">LinkedIn</a>)</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Contact;
