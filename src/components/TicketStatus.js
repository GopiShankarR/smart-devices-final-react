import React, { useState } from 'react';
import './../TicketStatus.css';
import $ from 'jquery';

const TicketStatus = () => {
  const [ticketNumber, setTicketNumber] = useState("");
  const [statusMessage, setStatusMessage] = useState("");

  const handleTicketNumberChange = (e) => {
    setTicketNumber(e.target.value);
  };

  const handleCheckStatus = (e) => {
    e.preventDefault();

    $.ajax({
      type: 'GET',
      url: `http://localhost:8080/backend/customer-service`,
      data: { ticketNumber: ticketNumber },
      success: (response) => {
        console.log(response);
        if (response.status) {
          // Customize messages based on the status
          if (response.status === "Escalate") {
            setStatusMessage("A human agent is looking at your issue. Please check back later, or we’ll provide an update shortly.");
          } else if (response.status === "Replace") {
            setStatusMessage("Your request has been approved for a replacement. A new order will be dispatched to you shortly.");
          } else if (response.status === "Refund" && response.amount) {
            setStatusMessage(`Your request for a refund has been approved. The refund amount of: $${response.amount} will be processed shortly.`);
          } else {
            setStatusMessage(`Ticket Status: ${response.status}`);
          }
        } else if (response.error) {
          setStatusMessage("Ticket not found.");
        }
      },
      error: (xhr, status, error) => {
        console.error('Error checking ticket status:', status, error);
        setStatusMessage("An error occurred while checking the ticket status.");
      }
    });

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
            placeholder="e.g., TICKET-123456789"
            required
          />
        </div>
        <button type="submit" className="check-status-button">Check Status</button>
      </form>
      {statusMessage && <p className="status-message">{statusMessage}</p>}
    </div>
  );
};

export default TicketStatus;
