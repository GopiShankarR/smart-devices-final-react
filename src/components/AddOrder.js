import React, { useState, useEffect } from 'react';
import $ from 'jquery';
import { useNavigate } from 'react-router-dom';
import './../AddOrder.css';

const AddOrder = () => {
  const [newOrder, setNewOrder] = useState({
    username: '',
    discount: '',
    deliveryOption: 'homeDelivery', 
    name: '',
    street: '',
    city: '',
    state: '',
    zip: ''
  });
  
  const [selectedProduct, setSelectedProduct] = useState('');
  const [products, setProducts] = useState([]); // Initialize as an empty array
  const navigate = useNavigate();

  // Fetch the product names when the component mounts
  useEffect(() => {
    $.ajax({
      type: 'GET',
      url: 'http://localhost:8080/backend/product', // Fetch products list
      success: (response) => {
        if (response.products && Array.isArray(response.products)) {
          setProducts(response);
        } else {
          console.error("Unexpected response format:", response);
        }
      },
      error: (xhr, status, error) => {
        console.error('Error fetching products:', status, error);
      }
    });
  }, []);

  const handleAddOrder = () => {
    if (isNaN(newOrder.discount)) {
      alert('Please enter a valid discount number.');
      return;
    }

    const requestData = {
      ...newOrder,
      product: selectedProduct, 
    };

    $.ajax({
      type: 'POST',
      url: 'http://localhost:8080/backend/salesman',
      contentType: 'application/json',
      data: JSON.stringify({ action: 'addOrder', ...requestData }),
      success: (response) => {
        if (response.success) {
          alert('Order added successfully.');
          navigate('/salesman');
        } else {
          alert('Error adding order.');
        }
      },
      error: (xhr, status, error) => {
        console.error('Error adding order:', status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  return (
    <div className="add-order-container">
      <h2>Add Order</h2>
      <form className="add-order-form">
        <label>Customer Username:</label>
        <input
          type="text"
          placeholder="Enter customer username"
          value={newOrder.username}
          onChange={(e) => setNewOrder({ ...newOrder, username: e.target.value })}
          required
        />

        <label>Product:</label>
        <select
          value={selectedProduct}
          onChange={(e) => setSelectedProduct(e.target.value)}
          required
        >
          <option value="">--Select Product--</option>
          {products.length > 0 ? (
            products.map((product, index) => (
              <option key={index} value={product.productName}>
                {product.productName} - ${product.productPrice}
              </option>
            ))
          ) : (
            <option disabled>Loading products...</option>
          )}
        </select>

        <label>Discount (if any):</label>
        <input
          type="number"
          placeholder="Enter discount (numbers only)"
          value={newOrder.discount}
          onChange={(e) => setNewOrder({ ...newOrder, discount: e.target.value })}
          required
        />

        <h3>Shipping Address</h3>
        <input
          type="text"
          placeholder="Name"
          value={newOrder.name}
          onChange={(e) => setNewOrder({ ...newOrder, name: e.target.value })}
          required
        />
        <input
          type="text"
          placeholder="Street Address"
          value={newOrder.street}
          onChange={(e) => setNewOrder({ ...newOrder, street: e.target.value })}
          required
        />
        <input
          type="text"
          placeholder="City"
          value={newOrder.city}
          onChange={(e) => setNewOrder({ ...newOrder, city: e.target.value })}
          required
        />
        <input
          type="text"
          placeholder="State"
          value={newOrder.state}
          onChange={(e) => setNewOrder({ ...newOrder, state: e.target.value })}
          required
        />
        <input
          type="text"
          placeholder="Zip Code"
          value={newOrder.zip}
          onChange={(e) => setNewOrder({ ...newOrder, zip: e.target.value })}
          required
        />

        <button type="button" onClick={handleAddOrder} className="add-order-btn">Add Order</button>
      </form>
    </div>
  );
};

export default AddOrder;
