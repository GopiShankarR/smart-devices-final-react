import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';

const OrderDetails = () => {
  const { confirmationNumber } = useParams();
  const [order, setOrder] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  
  const initialOrder = location.state?.order;

  useEffect(() => {
    if (initialOrder) {
      setOrder(initialOrder);
    }
  }, [initialOrder]);

  return (
    <div style={{ textAlign: "center", margin: "20px" }}>
      <h2>Order Details</h2>
      {order ? (
        <div>
          <p><strong>Customer Name:</strong> {order.username}</p>
          <p><strong>Confirmation Number:</strong> {order.confirmationNumber}</p>
          <p><strong>Delivery Date:</strong> {order.deliveryDate}</p>
          <p><strong>Order Placed Date:</strong> {order.orderPlacedDate}</p>
          <p><strong>Items:</strong> {order.items}</p>
          <p><strong>Status:</strong> {order.status === "orderPlaced" ? "Order Placed" : "Order Canceled"}</p>
          <button onClick={() => navigate('/salesman')}>Back to All Orders</button>
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};

export default OrderDetails;
