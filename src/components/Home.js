import React from "react";
import { Link } from "react-router-dom";

const Home = () => {
  const imageUrl = "https://www.cnet.com/a/img/resize/80161ffbe9d40cf8bb877242efc50a9d669e0819/hub/2021/11/11/95b342b3-bc11-4694-b1dd-b96f8bc6de37/amazon-thermostat-2.jpg?auto=webp&fit=crop&height=360&width=640";

  return (
    <div className="container">
      <Link to="/camera/model1">
        <img src={imageUrl} alt="Centered" className="centered-image" />
      </Link>
    </div>
  );
};

export default Home;
