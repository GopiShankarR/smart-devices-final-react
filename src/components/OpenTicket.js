import React, { useState } from 'react';
import './../OpenTicket.css';

const OpenTicket = () => {
  const [description, setDescription] = useState("");

  const handleDescriptionChange = (e) => {
    setDescription(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Here you would send the description to the server to create a ticket
    console.log("Ticket Description:", description);
    alert("Your ticket has been submitted!");
    setDescription(""); // Clear the input after submission
  };

  return (
    <div className="open-ticket-container">
      <h2>Open a Ticket</h2>
      <form onSubmit={handleSubmit} className="open-ticket-form">
        <div className="form-group">
          <label htmlFor="description">Describe the issue:</label>
          <textarea
            id="description"
            name="description"
            rows="4"
            value={description}
            onChange={handleDescriptionChange}
            placeholder="Describe the issue with your received shipment..."
            required
          />
        </div>
        <button type="submit" className="submit-button">Submit Ticket</button>
      </form>
    </div>
  );
};

export default OpenTicket;
