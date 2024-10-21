import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { HashLink as Link } from 'react-router-hash-link';
import './../App.css';
import $ from 'jquery';

const Signup = ({ setIsLoggedIn, setUserType, setUsername }) => {
  const [usernameInput, setUsernameInput] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [selectedValue, setSelectedValue] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const navigate = useNavigate();

  const setOnOptionalValue = (e) => {
    console.log(e.target.value);
    setSelectedValue(e.target.value);
  };

  const handleSignup = (e) => {
    e.preventDefault();
    console.log(selectedValue);

    const urlEndPoint = 'http://localhost:8080/backend/signup';
    $.ajax({
      type: "POST",
      url: urlEndPoint,
      contentType: "application/json",
      data: JSON.stringify({ username: usernameInput, password: password, userType: selectedValue }),
      success: (response) => {
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
          alert("Login failed");
        }
      },
      error: (xhr, status, error) => {
        console.error("POST request failed:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  }

  const dropdownOptions = [
    {value: 'Store Manager', label: 'Store Manager'},
    {value: 'Customer', label: 'Customer'},
    {value: 'Salesman', label: 'Salesman'}
  ];

  return (
    <div>
      <h2>Signup</h2>
      <form onSubmit={handleSignup}>
        <input type="text"
          placeholder="Username"
          value={usernameInput}
          onChange={(e) => setUsernameInput(e.target.value)}
          required 
        />
        <input type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <input type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />
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
        <button type="submit">Signup</button>
        {errorMsg && <p style={{ color: 'red' }}>{errorMsg}</p>}
        {successMsg && <p style={{ color: 'green' }}>{successMsg}</p>}
        <p>Already a registered user? <Link to="/">Login Here</Link></p>
      </form>
    </div>
  )
};

export default Signup;