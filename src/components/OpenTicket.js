import React, { useState, useEffect } from 'react';
import './../OpenTicket.css';
import { useLocation, useNavigate } from 'react-router-dom';
import $ from 'jquery';

const OpenTicket = () => {
  const [orders, setOrders] = useState([]);
  const location = useLocation();
  const [selectedOrder, setSelectedOrder] = useState("");
  const [description, setDescription] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [confirmationNumber, setConfirmationNumber] = useState("");
  const { username } = location.state || {};

  const handleDescriptionChange = (e) => {
    setDescription(e.target.value);
  };

  const handleImageUrlChange = (e) => {
    setImageUrl(e.target.value);
  };

  const handleSelectedOrder = (e) => {
    console.log(e.target.value);
    setSelectedOrder(e.target.value);
    setConfirmationNumber(e.target.value);
  }

  useEffect(() => {
    $.ajax({
      type: 'GET',
      url: `http://localhost:8080/backend/customer-orders`,
      data: { username: username },
      success: (response) => {
        setOrders(response);
      },
      error: (xhr, status, error) => {
        console.error('Error fetching orders:', status, error);
      }
    });
  }, [username]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = JSON.stringify({
      description: description,
      imageUrl: imageUrl,
      username: username,
      confirmationNumber: confirmationNumber
    });
    console.log(data);
    $.ajax({
      type: 'POST',
      url: 'http://localhost:8080/backend/customer-service',
      data: data,
      success: (response) => {
        alert(`Your ticket has been submitted! Ticket Number: ${response.ticketNumber}`);
        setDescription("");
        setImageUrl("");
      },
      error: (xhr, status, error) => {
        console.error('Error submitting ticket:', status, error);
        alert('There was an error submitting your ticket. Please try again.');
      }
    });
  };

  return (
    <div className="open-ticket-container">
      <h2>Open a Ticket</h2>
      <form onSubmit={handleSubmit} className="open-ticket-form">
        <div className="form-group">
          <label htmlFor="order">Select an Order:</label>
          <select
            id="order"
            name="order"
            value={selectedOrder}
            onChange={handleSelectedOrder}
            required
          >
            <option value="">-- Select an Order --</option>
            {orders.map((order) => (
              <option key={order.orderId} value={order.confirmationNumber}>
                {order.confirmationNumber} - {order.productName} - {new Date(order.orderDate).toLocaleDateString()}
              </option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="description">Describe the issue:</label>
          <textarea
            id="description"
            name="description"
            rows="4"
            value={description}
            onChange={handleDescriptionChange}
            placeholder="Describe the issue with your order..."
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="imageUrl">Enter image URL:</label>
          <input
            type="url"
            id="imageUrl"
            name="imageUrl"
            value={imageUrl}
            onChange={handleImageUrlChange}
            placeholder="https://example.com/image.jpg"
            required
          />
        </div>
        <button type="submit" className="submit-button">Submit Ticket</button>
      </form>
    </div>
  );
};

export default OpenTicket;
