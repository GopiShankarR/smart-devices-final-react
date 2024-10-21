import React, { useState } from 'react';
import $ from 'jquery';
import { useNavigate } from 'react-router-dom';
import './../AddCustomer.css'; // Include the CSS file

const AddCustomer = () => {
  const [newCustomer, setNewCustomer] = useState({ username: '', password: '' });
  const navigate = useNavigate();

  const handleAddCustomer = () => {
    $.ajax({
      type: "POST",
      url: 'http://localhost:8080/backend/salesman',
      contentType: "application/json",
      data: JSON.stringify({ action: "addCustomer", ...newCustomer }),
      success: (response) => {
        if (response.success) {
          alert('Customer added successfully.');
          navigate('/salesman');
        } else {
          alert('Error adding customer.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error adding customer:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  return (
    <div className="add-customer-container">
      <div className="add-customer-form">
        <h2 className="form-title">Add Customer</h2>
        <input
          type="text"
          placeholder="Username"
          value={newCustomer.username}
          onChange={(e) => setNewCustomer({ ...newCustomer, username: e.target.value })}
          className="form-input"
        />
        <input
          type="password"
          placeholder="Password"
          value={newCustomer.password}
          onChange={(e) => setNewCustomer({ ...newCustomer, password: e.target.value })}
          className="form-input"
        />
        <button onClick={handleAddCustomer} className="form-button">Add Customer</button>
      </div>
    </div>
  );
};

export default AddCustomer;
