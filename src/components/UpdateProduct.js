import React, { useState } from "react";
import $ from "jquery";
import { useLocation, useNavigate } from "react-router-dom";
import "./../AddProduct.css";

const UpdateProduct = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const product = location.state?.product;
  console.log(product);

  const [productName, setProductName] = useState(product?.name || "");
  const [productPrice, setProductPrice] = useState(product?.price || "");
  const [description, setDescription] = useState(product?.description || "");
  const [manufacturer, setManufacturer] = useState(product?.manufacturer || "");
  const [imageUrl, setImageUrl] = useState(product?.imageUrl || "");
  const [category, setCategory] = useState(product?.category || "");
  const [productQuantity, setProductQuantity] = useState(product?.productQuantity ?? 0);
  const [onSale, setOnSale] = useState(product?.onSale || false);
  const [manufacturerRebate, setManufacturerRebate] = useState(product?.manufacturerRebate || false);

  const handleUpdateProduct = () => {
    const updatedProduct = {
      id: product.id,
      name: productName,
      price: productPrice,
      description: description,
      manufacturer: manufacturer,
      imageUrl: imageUrl,
      category: category,
      productQuantity: productQuantity,
      onSale: onSale,
      manufacturerRebate: manufacturerRebate,
    };

    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/product?id=${product.id}`,
      contentType: "application/json",
      data: JSON.stringify({ ...updatedProduct, action: "update" }),
      success: (response) => {
        if (response.success) {
          alert("Product updated successfully!");
          navigate("/store-manager");
        } else {
          alert("Failed to update product.");
        }
      },
      error: (xhr, status, error) => {
        console.error("Error updating product:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  return (
    <div className="add-product-container">
      <h1>Update Product</h1>
      <form className="add-product-form" onSubmit={(e) => { e.preventDefault(); handleUpdateProduct(); }}>
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
            value={category} 
            onChange={(e) => setCategory(e.target.value)} 
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
        <button type="submit" className="submit-button">Update Product</button>
      </form>
    </div>
  );
};

export default UpdateProduct;
