import React, { useState } from "react";
import $ from "jquery";
import "./../SearchReviews.css";

const SearchReviews = () => {
  const [query, setQuery] = useState("");
  const [reviews, setReviews] = useState([]);
  const [error, setError] = useState("");

  const handleSearch = () => {
    setError("");
    setReviews([]);

    $.ajax({
      url: "http://localhost:8080/backend/search-reviews",
      type: "POST",
      contentType: "application/json",
      dataType: "json",
      data: JSON.stringify({ query }),
      success: (data) => {
        console.log("Received data:", data);
        if (data.reviews && data.reviews.length > 0) {
          setReviews(data.reviews);
        } else {
          setError("No reviews found.");
        }
      },
      error: (jqXHR, textStatus, errorThrown) => {
        console.error("AJAX Error:", textStatus, errorThrown);
        const errorMessage = jqXHR.responseJSON?.error || "Failed to fetch reviews.";
        setError(errorMessage);
      },
    });
  };

  return (
    <div className="search-reviews-container">
      <h2>Search Reviews</h2>
      <form onSubmit={(e) => e.preventDefault()} className="search-reviews-form">
        <div className="form-group">
          <input
            type="text"
            id="query"
            name="query"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Enter search text..."
            required
            className="search-input"
          />
        </div>
        <button type="button" onClick={handleSearch} className="submit-button">
          Search Reviews
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}

      {reviews.length > 0 && (
        <div className="search-results-container">
          <h3>Search Results:</h3>
          <table className="reviews-table">
            <thead>
              <tr>
                <th>Review Text</th>
                <th>Rating</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {reviews.map((review, index) => (
                <tr key={index}>
                  <td>{review.reviewText}</td>
                  <td>{review.reviewRating}</td>
                  <td>{review.reviewDate}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default SearchReviews;
