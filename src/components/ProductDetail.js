import React, { useEffect, useState } from "react";
import { useParams, useLocation } from "react-router-dom";
import $ from 'jquery';
import './../ProductDetail.css';
import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css'

const ProductDetail = () => {
  const { id } = useParams();
  const [accessories, setAccessories] = useState([]);
  const location = useLocation();
  const { productList} = location.state || {};
  const product = productList?.filter((p) => p.id === id)[0];
  console.log(productList);
  console.log(product);
  var category = "";

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: `http://localhost:8080/backend/product?id=${id}&category=${category}`,
      dataType: "json",
      success: (data) => {
        console.log(data);
        if (data && data.length > 0 && data[0].aids) {
          console.log(data);
          setAccessories(data[0].aids);
        } else {
          setAccessories([]); // No accessories found, setting an empty array
        }
  
      },
      error: (xhr, status, error) => {
        console.error("Error fetching product details:", status, error);
      }
    });
  }, [id]);

  if (!product) {
    return <p>Loading...</p>;
  }

  return (
    <div className="product-detail">
      <div className="image-slider">
        <Carousel infiniteLoop showDots={false} responsive={responsive}>
          <img style={{ height: "210px", }} src={product.imageUrl} alt={product.name} />
        </Carousel>
      </div>
      <h1>{product.name}</h1>
      <p>{product.description}</p>
      <p>${product.price}</p>
      <h2>Accessories</h2>
      <ul>
        {accessories.length > 0 ? (
          accessories.map((accessory) => (
            <li key={accessory.aid}>
              <div className="accessory-card">
                <img src={accessory.imageURL} alt={accessory.name} style={{ height: "100px", width: "100px", }} />
                <h3>{accessory.name}</h3>
              </div>
            </li>
          ))
        ) : (
          <li>No accessories available</li>
        )}
      </ul>
    </div>
  );
};

const responsive = {
  superLargeDesktop: {
    breakpoint: { max: 4000, min: 3000 },
    items: 5
  },
  desktop: {
    breakpoint: { max: 3000, min: 1024 },
    items: 3
  },
  tablet: {
    breakpoint: { max: 1024, min: 464 },
    items: 2
  },
  mobile: {
    breakpoint: { max: 464, min: 0 },
    items: 1
  }
};

export default ProductDetail;
