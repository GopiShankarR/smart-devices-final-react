import React, { useState } from 'react';
import $ from 'jquery';

const CommunicationForm = () => {
  const [servletGetResponse, setServletGetResponse] = useState('');
  const [servletPostResponse, setServletPostResponse] = useState('');

  const sendHttpPostRequest = () => {
    const urlEndPoint = 'http://localhost:8080/my-servlet-app/firstServiceCall';
    $.ajax({
      type: "POST",
      url: urlEndPoint,
      success: (response) => {
        setServletPostResponse(response);
      },
      error: (xhr, status, error) => {
        console.error("POST request failed:", status, error);
        setServletPostResponse(`Error: ${status} - ${error}`);
      }
    });
  };

  const sendHttpGetRequest = () => {
    const urlEndPoint = 'http://localhost:8080/my-servlet-app/firstServiceCall';
    $.ajax({
      type: "GET",
      url: urlEndPoint,
      success: (response) => {
        setServletGetResponse(response);
      },
      error: (xhr, status, error) => {
        console.error("GET request failed:", status, error);
        setServletGetResponse(`Error: ${status} - ${error}`);
      }
    });
  };

  return (
    <div>
      <h1>Communication Form</h1>
      <form>
        <button type="button" onClick={sendHttpPostRequest}>
          HTTP Post Request
        </button>
        <textarea value={servletPostResponse} readOnly />

        <button type="button" onClick={sendHttpGetRequest}>
          HTTP Get Request
        </button>
        <textarea value={servletGetResponse} readOnly />
      </form>
    </div>
  );
};

export default CommunicationForm;
