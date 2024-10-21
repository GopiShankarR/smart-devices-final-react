import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { HashLink as Link } from 'react-router-hash-link';
import './../App.css';
import $ from 'jquery';

const Login = ({ setIsLoggedIn, setUserType, setUsername }) => {
  const [usernameInput, setUsernameInput] = useState("");
  const [password, setPassword] = useState("");
  const [selectedValue, setSelectedValue] = useState("");
  const navigate = useNavigate();

  const setOnOptionalValue = (e) => {
    console.log(e.target.value);
    setSelectedValue(e.target.value);
  };

  const dropdownOptions = [
    {value: 'Store Manager', label: 'Store Manager'},
    {value: 'Customer', label: 'Customer'},
    {value: 'Salesman', label: 'Salesman'}
  ];

  const handleLogin = (e) => {
    e.preventDefault();
    console.log(selectedValue);

      const urlEndPoint = 'http://localhost:8080/backend/login';
      $.ajax({
        type: "POST",
        url: urlEndPoint,
        contentType: "application/json",
        data: JSON.stringify({ username: usernameInput, password: password, userType: selectedValue }),
        success: (response) => {
          console.log(response);
          if (response.success) {
            setIsLoggedIn(true);
            setSelectedValue(selectedValue);
            setUserType(selectedValue);
            setUsername(usernameInput);
            if (response.redirect) {
              console.log(response.direct);
              navigate(response.redirect);
            }
          } else {
            alert("Login failed - User not registered");
          }
        },
        error: (xhr, status, error) => {
          console.error("POST request failed:", status, error);
          alert("Login failed - User not registered")
        }
      });
  }

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
      <label className="left-align">Username:</label>
        <input type="text"
          placeholder="Username"
          value={usernameInput}
          onChange={(e) => setUsernameInput(e.target.value)}
          required 
        />
        <label className="left-align">Password:</label>
        <input type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <br></br>
        <label className="left-align">User Type:</label>
        <select value={selectedValue}
          onChange={(e) => setOnOptionalValue(e)}
        >
        <option>Select a role</option>
          {dropdownOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        <br></br>
         <button type="submit">Login</button>
      </form>
      <p>User not registered? <Link to="/signup">Register Here</Link></p>
    </div>
  )
};

export default Login;