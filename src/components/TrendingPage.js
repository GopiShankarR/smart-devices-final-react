import React, { useEffect, useState } from "react";
import $ from "jquery";
import './../TrendingPage.css';

const TrendingPage = () => {
  const [trendingData, setTrendingData] = useState({
    topLikedProducts: [],
    topZipCodes: [],
    topSoldProducts: []
  });

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: "http://localhost:8080/backend/trending",
      success: (response) => {
        console.log(response);
        setTrendingData(response);
      },
      error: (xhr, status, error) => {
        console.error("Error fetching trending data:", error);
      }
    });
  }, []);

  return (
    <div className="trending-page">
      <h1>Trending Products</h1>
  
      {/* Top Liked Products Section */}
      <div className="trending-section">
        <h2>Top 5 Most Liked Products</h2>
        <div className="product-card-container">
          {trendingData.topLikedProducts.map((product, index) => (
            <div key={index} className="product-card">
              <img src={product.imageUrl} alt={product.productModelName} className="product-image" />
              <h3>{product.productModelName}</h3>
              <p>Rating: {product.avgRating}</p>
              
            </div>
          ))}
        </div>
      </div>
  
      {/* Top Zip Codes Section */}
      <div className="trending-section">
  <h2>Top 5 Zip Codes with Maximum Products Sold</h2>
  <table className="zip-table">
    <thead>
      <tr>
        <th>Zip Code</th>
        <th>Products Sold</th>
      </tr>
    </thead>
    <tbody>
      {trendingData.topZipCodes
        .filter((zipCode) => zipCode.zip !== "Not available for home delivery orders")
        .slice(0, 5) // Ensure that only the top 5 are shown after filtering
        .map((zipCode, index) => (
          <tr key={index}>
            <td>{zipCode.zip}</td>
            <td>{zipCode.count}</td>
          </tr>
        ))}
    </tbody>
  </table>
</div>
  
      {/* Top Sold Products Section */}
      <div className="trending-section">
        <h2>Top 5 Most Sold Products</h2>
        <div className="product-card-container">
          {trendingData.topSoldProducts.map((product, index) => (
            <div key={index} className="product-card">
              <img src={product.imageUrl} alt={product.productModelName} className="product-image" />
              <h3>{product.productModelName}</h3>
              <p>Sold: {product.soldCount}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TrendingPage;
