import React from 'react';
import { GiHamburgerMenu } from 'react-icons/gi';
import { ImCross } from 'react-icons/im';

const Sidebar = ({ isOpen, onClose, children }) => {
  return (
    <div className={`fixed top-0 left-0 h-full ${isOpen ? 'w-64' : 'w-0'} transition-all overflow-hidden bg-white bg-opacity-80 z-10`}>
      <button onClick={onClose} className="absolute top-4 right-4 text-xl">
        {isOpen ? <ImCross className="text-red-500" /> : <GiHamburgerMenu />}
      </button>
      {children}
    </div>
  );
};

export default Sidebar;

