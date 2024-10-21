import React, { useEffect, useState } from "react";
import $ from 'jquery';
import './../Salesman.css';
import { Navigate, useNavigate } from 'react-router-dom';

const Salesman = ({ isLoggedIn, userType }) => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoggedIn || userType !== 'Salesman') {
      return <Navigate to="/login" />;
    }
    fetchOrders();
  }, [isLoggedIn, userType]);

  const fetchOrders = () => {
    $.ajax({
      type: "GET",
      url: 'http://localhost:8080/backend/salesman?action=getOrders',
      contentType: "application/json",
      success: (response) => {
        if (response.orders && Array.isArray(response.orders)) {
          console.log(response.orders);
          setOrders(response.orders);
        } else {
          console.error('Unexpected response format:', response);
          alert('Failed to load orders.');
        }
        setLoading(false);
      },
      error: (xhr, status, error) => {
        console.error("GET request failed:", status, error);
        alert(`Error: ${status} - ${error}`);
        setLoading(false);
      }
    });
  };

  const handleUpdateOrder = (order) => {
    $.ajax({
      type: "POST",
      url: 'http://localhost:8080/backend/salesman',
      contentType: "application/json",
      data: JSON.stringify({ action: "update", ...order }),
      success: (response) => {
        if (response.success) {
          fetchOrders();
          alert('Order updated successfully.');
        } else {
          alert('Error updating order.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error updating order:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  const handleDeleteOrder = (confirmationNumber) => {
    $.ajax({
      type: "POST",
      url: 'http://localhost:8080/backend/salesman',
      contentType: "application/json",
      data: JSON.stringify({ action: "delete", confirmationNumber }),
      success: (response) => {
        if (response.success) {
          fetchOrders();
          alert('Order deleted successfully.');
        } else {
          alert('Error deleting order.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error deleting order:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h1>Manage Customers and Orders</h1>
      <div style={{ marginBottom: "20px" }}>
        <button onClick={() => navigate('/add-customer')}>Add Customer</button>
        <button onClick={() => navigate('/add-order')}>Add Order</button>
      </div>
      <h2>Customer Orders</h2>
      <table className="order-table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Confirmation Number</th>
            <th>Order Placed Date</th>
            <th>Delivery Date</th>
            <th>Order Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <tr key={order.confirmationNumber}>
              <td>{order.username}</td>
              <td>{order.confirmationNumber}</td>
              <td>{order.orderPlacedDate}</td>
              <td>{order.deliveryDate}</td>
              <td>{order.status === "orderPlaced" ? "Order Placed" : "Order Canceled"}</td>
              <td>
                <button onClick={() => navigate(`/order/${order.confirmationNumber}`, { state: { order }})}>View</button>
                <button onClick={() => handleUpdateOrder(order)}>Update</button>
                <button onClick={() => handleDeleteOrder(order.confirmationNumber)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Salesman;
