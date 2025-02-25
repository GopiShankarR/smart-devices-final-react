import React from 'react';
import { useNavigate } from 'react-router-dom';
import './../CustomerService.css';

const CustomerService = ({username}) => {
  const navigate = useNavigate();

  const handleOpenTicket = () => {
    console.log(username);
    navigate("/customer-service/open-ticket", { state: { username: username } });
  };

  const handleCheckStatus = () => {
    navigate("/customer-service/status");
  };

  return (
    <div className="customer-service-container">
      <h2>Customer Service</h2>
      <div className="customer-service-options">
        <button className="service-button" onClick={handleOpenTicket}>
          Open a Ticket
        </button>
        <button className="service-button" onClick={handleCheckStatus}>
          Status of a Ticket
        </button>
      </div>
    </div>
  );
};

export default CustomerService;
