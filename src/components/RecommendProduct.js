import React, { useState } from "react";
import $ from "jquery";
import "./../SearchReviews.css";

const RecommendProduct = () => {
  const [inputText, setInputText] = useState("");
  const [recommendedProducts, setRecommendedProducts] = useState([]);
  const [error, setError] = useState("");
  const [inputError, setInputError] = useState("");

  const handleInputChange = (e) => {
    setInputText(e.target.value);

    // Clear input error message if the character count becomes valid
    if (e.target.value.length >= 100) {
      setInputError("");
    }
  };

  const handleRecommendProduct = () => {
    setError("");
    setRecommendedProducts([]);

    // Check if inputText meets the minimum character requirement
    if (inputText.length < 100) {
      setInputError("A minimum of 100 characters is required.");
      return;
    }

    $.ajax({
      type: "POST",
      url: "http://localhost:8080/backend/recommend-product",
      contentType: "application/json",
      data: JSON.stringify({ query: inputText }),
      success: (response) => {
        console.log(response);
        if (response.recommended_products && response.recommended_products.length > 0) {
          setRecommendedProducts(response.recommended_products);
        } else {
          setError("No similar products found.");
        }
      },
      error: (xhr, status, error) => {
        console.error("Error fetching recommendations:", status, error);
        setError("An error occurred while fetching the recommendations.");
      },
    });
  };

  return (
    <div className="search-reviews-container">
      <h2>Find Product Recommendations</h2>
      <form onSubmit={(e) => e.preventDefault()} className="search-reviews-form">
        <div className="form-group">
          <textarea
            id="inputText"
            name="inputText"
            value={inputText}
            onChange={handleInputChange}
            placeholder="Enter product description (minimum 100 characters)..."
            required
            className="search-input"
          />
          {inputError && <p className="input-error-message">{inputError}</p>}
        </div>
        <button type="button" onClick={handleRecommendProduct} className="submit-button">
          Recommend Product
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}

      {recommendedProducts.length > 0 && (
        <div className="recommendations-table-container">
          <table className="recommendations-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Category</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              {recommendedProducts.map((product, index) => (
                <tr key={index}>
                  <td>{product.product_name}</td>
                  <td>${product.price}</td>
                  <td>{product.category}</td>
                  <td>{product.description}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default RecommendProduct;
