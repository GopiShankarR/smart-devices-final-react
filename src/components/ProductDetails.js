import React from "react";
import { useLocation } from "react-router-dom";

const ProductDetails = () => {
  const location = useLocation();
  const product = location.state?.product;

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
    <div className="product-details">
      <h1>{product.name}</h1>
      <img src={product.imageUrl} alt={product.name} />
      <p>{product.description}</p>
      <p>Price: ${product.price}</p>
      <p>Manufacturer: {product.manufacturer}</p>
    </div>
  );
};

export default ProductDetails;
