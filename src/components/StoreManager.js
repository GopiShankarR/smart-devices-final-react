import React, { useEffect, useState } from "react";
import $ from 'jquery';
import './../StoreManager.css';
import { Navigate, useNavigate } from 'react-router-dom';

const StoreManager = ({isLoggedIn, userType}) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const urlEndPoint = 'http://localhost:8080/backend/product';
    $.ajax({
      type: "GET",
      url: urlEndPoint,
      contentType: "application/json",
      success: (response) => {
        if (Array.isArray(response)) {
          setProducts(response);
          console.log(response);
        } else {
          console.error('Unexpected response format:', response);
          alert('Failed to load products.');
        }
        setLoading(false);
      },
      error: (xhr, status, error) => {
        console.error("GET request failed:", status, error);
        alert(`Error: ${status} - ${error}`);
        setLoading(false);
      }
    });
  }, []);

  const handleUpdate = (product) => {
    navigate('/update-product', { state: { product } });
  };

  const handleDelete = (id, name) => {
    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/product?id=${id}`,
      contentType: "application/json",
      data: JSON.stringify({ id: id, name: name, action: "delete" }),
      success: (response) => {
        if (response.success) {
          const updatedProducts = products.filter(product => product.id !== id);
          setProducts(updatedProducts);
        } else {
          alert('Error deleting the product.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error deleting product:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h1>Manage Products</h1>

      <div style={{ textAlign: "center", marginBottom: "20px" }}>
        <button onClick={() => navigate('/add-product')}>Add Product</button>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Price</th>
            <th>Manufacturer</th>
            <th>Description</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map(product => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>${product.price}</td>
              <td>{product.manufacturer}</td>
              <td>{product.description}</td>
              <td>
                <button onClick={() => handleUpdate(product)}>Update</button>
                <button onClick={() => handleDelete(product.id, product.name)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default StoreManager;
