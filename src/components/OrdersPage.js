import React, { useEffect, useState } from 'react';
import $ from 'jquery';
import { useLocation, useNavigate } from 'react-router-dom';
import './../Orders.css';

const OrdersPage = () => {
  const location = useLocation();
  const { username } = location.state || {};
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: `http://localhost:8080/backend/order?username=${username}`,
      contentType: "application/json",
      success: (response) => {
        console.log("response:", response);
        setOrders(response.orders); // Assuming orders are in response.orders
        setLoading(false);
      },
      error: (xhr, status, error) => {
        console.error("Error fetching orders:", error);
        setLoading(false);
      }
    });
  }, [username]);
  
  const formatDateToMMDDYYYY = (dateString) => {
    const [year, month, day] = dateString.split('-');
    return `${month}-${day}-${year}`; // MM-DD-YYYY format
  };

  const handleAddReview = (userId, productId, productName, username, confirmationNumber) => {
    console.log(productId, username, userId, confirmationNumber);
    navigate('/add-product-review', { 
      state: { 
        userId, 
        productId, 
        productName, 
        username, 
        confirmationNumber 
      } 
    });
  };

  // Handle canceling an order
  const handleCancelOrder = (confirmationNumber) => {
    $.ajax({
      type: "POST",
      url: "http://localhost:8080/backend/order",
      contentType: "application/json",
      data: JSON.stringify({ action: "cancelOrder", confirmationNumber }),
      success: (response) => {
        alert("Order canceled successfully.");
      },
      error: (xhr, status, error) => {
        console.error("Error canceling order:", error);
      }
    });
  };

  if (loading) {
    return <p className="loading">Loading...</p>;
  }

  return (
    <div>
      <h1 style={{ textAlign: "center", margin: "20px" }}>Your Orders</h1>
      <div style={{ textAlign: "center", margin: "20px" }}>
        {orders.length ? (
          <table className="order-table">
            <thead>
              <tr>
                <th>Confirmation Number</th>
                <th>Delivery Option</th>
                <th>Delivery Date</th>
                <th>Order Placed Date</th>
                <th>Status</th>
                <th>Actions</th>
                <th>Review</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order => {
                // Format delivery date and order placed date to MM-DD-YYYY
                const formattedDeliveryDate = formatDateToMMDDYYYY(order.deliveryDate);
                const formattedOrderPlacedDate = formatDateToMMDDYYYY(order.orderPlacedDate);

                const deliveryDate = new Date(order.deliveryDate); // Convert to Date object
                const orderPlacedDate = new Date(order.orderPlacedDate); // Convert to Date object
                
                const timeDifference = deliveryDate - orderPlacedDate;
                const daysDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));

                return (
                  <tr key={order.confirmationNumber}>
                    <td>{order.confirmationNumber}</td>
                    <td>{order.deliveryOption === "homeDelivery" ? "Home Delivery" : "Store Pickup"}</td>
                    <td>{formattedDeliveryDate}</td>
                    <td>{formattedOrderPlacedDate}</td>
                    <td>
                      {order.status === "orderPlaced"
                        ? "Order Placed"
                        : order.status === "delivered"
                        ? "Order Delivered"
                        : "Order Canceled"}
                    </td>
                    <td>
                      {order.status === "orderPlaced" && daysDifference > 5 && (
                        <button onClick={() => handleCancelOrder(order.confirmationNumber)}>
                          Cancel
                        </button>
                      )}
                    </td>
                    <td>
                      {order.status === "delivered" && (
                        <div>
                          {order.products.map((product, index) => (
                            <div key={index}>
                              <a href="#" onClick={() => handleAddReview(order.userId, product.productId, product.productName, username, order.confirmationNumber)}>
                                Add a product review for {product.productName}
                              </a>
                            </div>
                          ))}
                        </div>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        ) : (
          <p>No orders found.</p>
        )}
      </div>
    </div>
  );
};

export default OrdersPage;
