import React, { useState } from "react";
import $ from "jquery";
import { useNavigate } from "react-router-dom";
import "./../AddProduct.css";

const AddProduct = () => {
  const [productName, setProductName] = useState("");
  const [productPrice, setProductPrice] = useState("");
  const [description, setDescription] = useState("");
  const [manufacturer, setManufacturer] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [type, setType] = useState("");
  const [productQuantity, setProductQuantity] = useState("");
  const [onSale, setOnSale] = useState(false);
  const [manufacturerRebate, setManufacturerRebate] = useState(false);
  const navigate = useNavigate();

  const handleAddProduct = () => {
    const newProduct = {
      name: productName,
      price: productPrice,
      description: description,
      manufacturer: manufacturer,
      imageUrl: imageUrl,
      category: type,
      productQuantity: productQuantity,
      onSale: onSale,
      manufacturerRebate: manufacturerRebate
    };

    console.log(newProduct);

    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/product?category=${type}`,
      contentType: "application/json",
      data: JSON.stringify({ ...newProduct, action: "add"}),
      success: (response) => {
        if (response.success) {
          alert("Product added successfully!");
          navigate("/store-manager");
        } else {
          alert("Failed to add product.");
        }
      },
      error: (xhr, status, error) => {
        console.error("Error adding product:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  return (
    <div className="add-product-container">
      <h1>Add New Product</h1>
      <form className="add-product-form" onSubmit={(e) => { e.preventDefault(); handleAddProduct(); }}>
        <label>
          Product Name:
          <input 
            type="text" 
            value={productName} 
            onChange={(e) => setProductName(e.target.value)} 
            required 
          />
        </label>
        <label>
          Product Price:
          <input 
            type="number" 
            value={productPrice} 
            onChange={(e) => setProductPrice(e.target.value)} 
            required 
          />
        </label>
        <label>
          Description:
          <textarea 
            value={description} 
            onChange={(e) => setDescription(e.target.value)} 
            required 
          />
        </label>
        <label>
          Manufacturer:
          <input 
            type="text" 
            value={manufacturer} 
            onChange={(e) => setManufacturer(e.target.value)} 
            required 
          />
        </label>
        <label>
          Image URL:
          <input 
            type="url" 
            value={imageUrl} 
            onChange={(e) => setImageUrl(e.target.value)} 
            required 
          />
        </label>
        <label>
          Type:
          <input 
            type="text" 
            value={type} 
            onChange={(e) => setType(e.target.value)} 
            required 
          />
        </label>
        <label>
          Product Quantity:
          <input
            type="number"
            value={productQuantity}
            onChange={(e) => setProductQuantity(e.target.value)}
            required
          />
        </label>
        <label>
          On Sale:
          <input
            type="checkbox"
            checked={onSale}
            onChange={(e) => setOnSale(e.target.checked)}
          />
        </label>
        <label>
          Manufacturer Rebate:
          <input
            type="checkbox"
            checked={manufacturerRebate}
            onChange={(e) => setManufacturerRebate(e.target.checked)}
          />
        </label>
        <button type="submit" className="submit-button">Add Product</button>
      </form>
    </div>
  );
};

export default AddProduct;
