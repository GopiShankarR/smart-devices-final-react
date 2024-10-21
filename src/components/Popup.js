import React, { useEffect } from 'react';
import './../Popup.css';

const Popup = ({ isOpen, onClose, features }) => {

  useEffect(() => {
    if (isOpen) {
      document.body.classList.add('no-scroll');
    } else {
      document.body.classList.remove('no-scroll');
    }

    return () => {
      document.body.classList.remove('no-scroll');
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className={`popup-overlay ${isOpen ? 'show' : ''}`}>
      <div className="popup-content">
        <button className="close-button" onClick={onClose}>Close</button>
        <h3>Key Features</h3>
        <ul>
          {features.map((feature, index) => (
            <li key={index}>
              <strong>{Object.keys(feature)[0]}</strong>: {Object.values(feature)[0]}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Popup;
