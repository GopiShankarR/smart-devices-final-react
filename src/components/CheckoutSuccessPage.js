import React from 'react';
import { useLocation } from 'react-router-dom';

const CheckoutSuccessPage = () => {
  const location = useLocation();
  const { confirmationNumber, deliveryDate, address } = location.state || {};

  return (
    <div className="checkout-success-page">
      <h1>Order Successful!</h1>
      <p>Your order has been placed successfully.</p>
      <p><strong>Confirmation Number:</strong> {confirmationNumber}</p>
      <p><strong>Delivery Date:</strong> {deliveryDate}</p>
      <p><strong>Delivery Address:</strong></p>
      {typeof address === 'string' ? (
        <p>{address}</p>
      ) : (
        <div>
          <p>{address.name}</p>
          <p>{address.street}</p>
          <p>{address.city}, {address.state} {address.zip}</p>
        </div>
      )}
      <p>Thank you for shopping with us!</p>
    </div>
  );
};

export default CheckoutSuccessPage;
