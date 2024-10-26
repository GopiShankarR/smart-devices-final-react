import React, { useState } from 'react';
import './../TicketStatus.css';

const TicketStatus = () => {
  const [ticketNumber, setTicketNumber] = useState("");

  const handleTicketNumberChange = (e) => {
    setTicketNumber(e.target.value);
  };

  const handleCheckStatus = (e) => {
    e.preventDefault();
    // Here you would send the ticket number to the server to check its status
    console.log("Checking status for Ticket Number:", ticketNumber);
    alert(`Checking status for Ticket Number: ${ticketNumber}`);
    setTicketNumber(""); // Clear the input after submission
  };

  return (
    <div className="ticket-status-container">
      <h2>Check Ticket Status</h2>
      <form onSubmit={handleCheckStatus} className="ticket-status-form">
        <div className="form-group">
          <label htmlFor="ticket-number">Enter Ticket Number:</label>
          <input
            type="text"
            id="ticket-number"
            name="ticket-number"
            value={ticketNumber}
            onChange={handleTicketNumberChange}
            placeholder="e.g., TKT123456"
            required
          />
        </div>
        <button type="submit" className="check-status-button">Check Status</button>
      </form>
    </div>
  );
};

export default TicketStatus;
