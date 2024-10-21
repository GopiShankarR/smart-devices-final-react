import React from "react";
import { Slide } from "react-slideshow-image";
import './../ProductList.css';

const products = [
  {
    "id": "1",
    "name": "Smart Thermostat",
    "description": "A smart thermostat that adjusts the temperature based on your preferences and habits.",
    "price": "$149.99",
    "imageUrl1": "https://cdn.thewirecutter.com/wp-content/media/2022/11/smartthermostats-2048px-3105.jpg",
    "imageUrl2": "https://multimedia.bbycastatic.ca/multimedia/products/500x500/103/10389/10389044.jpg",
    "category": "Climate Control",
    "company": "Nest"
  },
  {
    "id": "2",
    "name": "Smart Security Camera",
    "description": "HD security camera with night vision and motion detection capabilities.",
    "price": "$89.99",
    "imageUrl1": "https://m.media-amazon.com/images/I/41TupDmo90L.jpg",
    "imageUrl2": "https://m.media-amazon.com/images/I/51zwd1FSSIL._AC_UF894,1000_QL80_.jpg",
    "category": "Camera",
    "company": "Nest"
  },
  {
    "id": "3",
    "name": "Smart Light Bulb",
    "description": "Energy-efficient LED light bulb with adjustable color and brightness settings.",
    "price": "$19.99",
    "imageUrl1": "https://images.ctfassets.net/a3peezndovsu/variant-15022247379033/adafcde7f95ae0bc9f6745783f30d6cb/variant-15022247379033.jpg",
    "imageUrl2": "https://m.media-amazon.com/images/I/31Mre8GwRpL._SY1000_.jpg",
    "category": "SmartLights",
    "company": "ring"
  },
  {
    "id": "4",
    "name": "Smart Lock",
    "description": "Keyless smart lock with remote access and security alerts.",
    "price": "$199.99",
    "imageUrl1": "https://images.thdstatic.com/productImages/7296aa42-bbb5-4bff-894f-a379efd247d5/svn/eufy-security-electronic-deadbolts-t8520j11-fa_600.jpg",
    "imageUrl2": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjitqy06P75xhK989jRUOcgcKzNbm11f_sFPuKeK4A52NYZrNzmVn6rdMQqsLwagqJn4I&usqp=CAU",
    "category": "Security",
    "company": "eufy"
  },
  {
    "id": "5",
    "name": "Smart Speakers",
    "description": "",
    "price": "$29.99",
    "imageUrl1": "https://i.pcmag.com/imagery/reviews/00EU3U5rRoe9swRlkJE2yDa-1..v1601052236.jpg",
    "imageUrl2": "https://media.cnn.com/api/v1/images/stellar/prod/mw-rh-mz-iz-mj-et-cb608419687.jpg?c=16x9",
    "category": "Automation",
    "company": "Amazon"
  }
];

const ProductList = () => {
  return (
    <div className="product-list">
      {products.map((product) => (
        <div key={product.id} className="product-card">
          <div className="image-slider">
            <Slide>
              <div className="each-slide">
                <img src={product.imageUrl1} alt={product.name} />
              </div>
              <div className="each-slide">
                <img src={product.imageUrl2} alt={product.name} />
              </div>
            </Slide>
          </div>
          <h2>{product.name}</h2>
          <p>{product.description}</p>
          <p>{product.price}</p>
        </div>
      ))}
    </div>
  );
};

export default ProductList;