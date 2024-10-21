import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import $ from 'jquery';
import './../ProductReviewPage.css';

const ProductReviewPage = () => {
  const location = useLocation();
  const { userId, productId, productName, username, confirmationNumber } = location.state || {};

  const [reviewData, setReviewData] = useState({
    productModelName: productName || '',
    productCategory: '',
    productPrice: '',
    storeId: '',
    storeZip: '',
    storeCity: '',
    storeState: '',
    productOnSale: 'Yes',
    manufacturerName: '',
    manufacturerRebate: 'Yes',
    userId: userId || '',
    userAge: '',
    userGender: 'Male',
    userOccupation: '',
    reviewRating: '',
    reviewDate: new Date().toISOString().split('T')[0],
    reviewText: ''
  });

  useEffect(() => {
    console.log(productId, username, confirmationNumber);
  }, []);
  

  useEffect(() => {
    if(productId && username && confirmationNumber) {
      $.ajax({
        type: "GET",
        url: `http://localhost:8080/backend/review?productId=${productId}&username=${username}&confirmationNumber=${confirmationNumber}`,
        success: (response) => {
          setReviewData({
            ...reviewData,
            productModelName: response.productName,
            productCategory: response.productCategory,
            productPrice: response.productPrice,
            manufacturerName: response.manufacturer,
            storeId: response.storeId || 'Not available for home delivery orders',
            storeZip: response.storeZip || 'Not available for home delivery orders',
            storeCity: response.storeCity || 'Not available for home delivery orders',
            storeState: response.storeState || 'Not available for home delivery orders',
          });
        },
        error: (xhr, status, error) => {
          console.error("Error fetching product details:", error);
        }
      });
    }
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setReviewData({ ...reviewData, [name]: value });
  };

  const handleSubmitReview = (e) => {
    e.preventDefault();
  
    $.ajax({
      type: 'POST',
      url: 'http://localhost:8080/backend/submitReview',
      contentType: 'application/json',
      data: JSON.stringify(reviewData),
      success: (response) => {
        if (response.success) {
          alert('Review submitted successfully');
        }
      },
      error: (error) => {
        console.error('Error submitting review', error);
      }
    });
  };

  return (
    <div>
      <h1>Write a Review for {reviewData.productModelName}</h1>
      <form onSubmit={handleSubmitReview}>
        <label>
          Product Model Name:
          <input
            type="text"
            name="productModelName"
            value={reviewData.productModelName}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          Product Category:
          <input
            type="text"
            name="productCategory"
            value={reviewData.productCategory}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          Product Price:
          <input
            type="number"
            name="productPrice"
            value={reviewData.productPrice}
            readOnly
            className="centered-input"
          />
        </label>

        {/* Store Information with conditional red text */}
        <label>
          Store ID:
          <input
            type="text"
            name="storeId"
            value={reviewData.storeId}
            readOnly
            className="centered-input"
            style={{ color: reviewData.storeId.includes('Not available') ? 'red' : 'black' }}
          />
        </label>
        <label>
          Store Zip:
          <input
            type="text"
            name="storeZip"
            value={reviewData.storeZip}
            readOnly
            className="centered-input"
            style={{ color: reviewData.storeZip.includes('Not available') ? 'red' : 'black' }}
          />
        </label>
        <label>
          Store City:
          <input
            type="text"
            name="storeCity"
            value={reviewData.storeCity}
            readOnly
            className="centered-input"
            style={{ color: reviewData.storeCity.includes('Not available') ? 'red' : 'black' }}
          />
        </label>
        <label>
          Store State:
          <input
            type="text"
            name="storeState"
            value={reviewData.storeState}
            readOnly
            className="centered-input"
            style={{ color: reviewData.storeState.includes('Not available') ? 'red' : 'black' }}
          />
        </label>

        <label>
          Product On Sale:
          <input
            type="text"
            name="productOnSale"
            value={reviewData.productOnSale ? 'Yes' : 'No'}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          Manufacturer Name:
          <input
            type="text"
            name="manufacturerName"
            value={reviewData.manufacturerName}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          Manufacturer Rebate:
          <input
            type="text"
            name="manufacturerRebate"
            value={reviewData.manufacturerRebate ? 'Yes' : 'No'}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          User ID:
          <input
            type="text"
            name="userId"
            value={reviewData.userId}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          User Age:
          <input
            type="number"
            name="userAge"
            value={reviewData.userAge}
            onChange={handleInputChange}
            required
            className="centered-input"
          />
        </label>
        <label>
          User Gender:
          <div>
            <select
              name="userGender"
              value={reviewData.userGender}
              onChange={handleInputChange}
              className="centered-input"
              required
            >
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </label>
        <label>
          User Occupation:
          <input
            type="text"
            name="userOccupation"
            value={reviewData.userOccupation}
            onChange={handleInputChange}
            required
            className="centered-input"
          />
        </label>
        <label>
          Review Rating:
          <input
            type="number"
            name="reviewRating"
            value={reviewData.reviewRating}
            onChange={handleInputChange}
            required
            max="5"
            min="1"
            className="centered-input"
          />
        </label>
        <label>
          Review Date:
          <input
            type="date"
            name="reviewDate"
            value={reviewData.reviewDate}
            readOnly
            className="centered-input"
          />
        </label>
        <label>
          Review Text:
          <div>
            <textarea
              name="reviewText"
              value={reviewData.reviewText}
              onChange={handleInputChange}
              required
              className="centered-input"
            />
          </div>
        </label>
        <button type="submit">Submit Review</button>
      </form>
    </div>
  );
};

export default ProductReviewPage;
