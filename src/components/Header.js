import React, { useState } from "react";
import { HashLink as Link } from 'react-router-hash-link';
import { useNavigate } from 'react-router-dom';
import './../Header.css';
import $ from 'jquery';

const Header = ({ isLoggedIn, userType, username, onLogout }) => {
  const navigate = useNavigate();
  const [suggestions, setSuggestions] = useState([]);
  const [query, setQuery] = useState("");

  const handleCart = () => {
    navigate("/cart", { state: { username } });
  };

  const handleViewOrders = () => {
    navigate(`/orders/${username}`, { state: { username } });
  };

  const logoutFunction = () => {
    onLogout();
    navigate("/");
  };

  const handleSearch = (e) => {
    e.preventDefault();
    navigate(`/search?q=${query}`);
  };

  const fetchSuggestions = (query) => {
    $.ajax({
      type: 'GET',
      url: `http://localhost:8080/backend/autocomplete?query=${query}`,
      success: (response) => {
        setSuggestions(response);
      },
      error: (xhr, status, error) => {
        console.error('Error fetching suggestions:', status, error);
      }
    });
  };

  const handleInputChange = (e) => {
    const value = e.target.value;
    setQuery(value);

    if (value.length > 1) {
      fetchSuggestions(value);
    } else {
      setSuggestions([]);
    }
  };

  const handleSuggestionClick = (productName) => {

    $.ajax({
      type: 'GET',
      url: `http://localhost:8080/backend/product?name=${productName}`,
      success: (response) => {
        navigate(`/product-details`, { state: { product: response } });
      },
      error: (xhr, status, error) => {
        console.error('Error fetching product details:', status, error);
        alert('Failed to load product details.');
      }
    });
  };

  return (
    <header className="header">
      <div className="left-section">
        <Link smooth to="/home" className="logo">SmartHomes</Link>
        {isLoggedIn && (
          <nav className="nav-links">
            <div className="nav-item products-dropdown">
              <Link smooth to="#">Products</Link>
              <ul className="dropdown-menu">
                <li><Link smooth to="/doorbells">Doorbells</Link></li>
                <li><Link smooth to="/door-locks">Door Locks</Link></li>
                <li><Link smooth to="/lightings">Lightings</Link></li>
                <li><Link smooth to="/speakers">Speakers</Link></li>
                <li><Link smooth to="/climate-control">Thermostats</Link></li>
              </ul>
          </div>

            <div className="nav-item trending">
              <Link smooth to="/trending">Trending</Link>
            </div>

            <div className="nav-item service">
              <Link smooth to="/customer-service">Customer Service</Link>
            </div>

            <div className="nav-item reviews">
              <Link smooth to="/search-reviews">Search Reviews</Link>
            </div>

            <div className="nav-item recommend">
              <Link smooth to="/recommend-product">Recommend Product</Link>
            </div>

            {userType === "Store Manager" && (
              <>
                <div className="nav-item inventory-report">
                  <Link smooth to="/inventory-report">Inventory Report</Link>
                </div>

                <div className="nav-item sales-report">
                  <Link smooth to="/sales-report">Sales Report</Link>
                </div>
              </>
            )}
          </nav>
        )}
      </div>

      {isLoggedIn ? (
        <div className="header-actions">
          <form onSubmit={handleSearch} className="search-form">
            <input
              type="text"
              name="search"
              placeholder="Search products..."
              className="search-input"
              value={query}
              onChange={handleInputChange}
            />
            {suggestions.length > 0 && (
              <ul className="suggestions-list">
                {suggestions.map((suggestion, index) => (
                  <li key={index} onClick={() => handleSuggestionClick(suggestion)}>
                    {suggestion}
                  </li>
                ))}
              </ul>
            )}
          </form>

          {userType === "Customer" && (
            <>
              <button onClick={handleCart} className="cart-button">Cart</button>
              <button onClick={handleViewOrders} className="view-orders-button">View Orders</button>
            </>
          )}

          <button className="login-link" onClick={logoutFunction}>Logout</button>
        </div>
      ) : (
        <Link to="/" className="login-link">Login</Link>
      )}
    </header>
  );
};

export default Header;
