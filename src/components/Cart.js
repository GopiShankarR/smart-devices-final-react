import React, { useEffect, useState } from "react";
import $ from 'jquery';
import { FaTrashAlt } from 'react-icons/fa';
import './../Cart.css';
import { useNavigate } from 'react-router-dom';

const Cart = ({ username }) => {
  const [cartItems, setCartItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const navigate = useNavigate();

  const aggregateCartItems = (items) => {
    const aggregatedItems = {};
    let total = 0;

    items.forEach((item) => {
      if (aggregatedItems[item.id]) {
        aggregatedItems[item.id].quantity += item.quantity;
      } else {
        aggregatedItems[item.id] = { ...item };
      }
      total += item.price * item.quantity;
    });

    setTotalPrice(total);
    setCartItems(Object.values(aggregatedItems)); 
  };

  const handleDecreaseQuantity = (itemId, itemName, itemPrice, itemImage) => {
    if (cartItems.find(item => item.id === itemId).quantity > 1) {
      updateQuantity(itemId, itemName, itemPrice, itemImage, "decrease");
    } else {
      deleteItem(itemId, itemName, itemPrice, itemImage);
    }
  };
  
  const handleIncreaseQuantity = (itemId, itemName, itemPrice, itemImage) => {
    updateQuantity(itemId, itemName, itemPrice, itemImage, "increase");
  };

  useEffect(() => {
    console.log("Fetching cart items for user:", username);
    const urlEndPoint = `http://localhost:8080/backend/cart?username=${username}`;

    $.ajax({
      type: "GET",
      url: urlEndPoint,
      contentType: "application/json",
      success: (response) => {
        console.log("Response from server:", response);

        if (response && Array.isArray(response.items) && typeof response.totalPrice === "number") {
          aggregateCartItems(response.items);
        } else {
          console.error('Unexpected response format:', response);
          alert('Failed to load cart items.');
        }
      },
      error: (xhr, status, error) => {
        console.error("GET request failed:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  }, [username]);

  const deleteItem = (itemId, itemName, itemPrice, itemImage) => {
    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/cart?username=${username}`,
      contentType: "application/json",
      data: JSON.stringify({
        id: itemId,
        name: itemName,
        image: itemImage,  // Include image and price
        price: itemPrice,
        action: "delete"
      }),
      success: (response) => {
        if (response.success) {
          const updatedItems = cartItems.filter(item => item.id !== itemId);
          setCartItems(updatedItems);

          const updatedTotalPrice = updatedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
          setTotalPrice(updatedTotalPrice);
        } else {
          alert('Error deleting the item.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error deleting cart item:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  const updateQuantity = (itemId, itemName, itemPrice, itemImage, action) => {
    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/cart?username=${username}`,
      contentType: "application/json",
      data: JSON.stringify({
        id: itemId,
        name: itemName,
        image: itemImage,  // Include image and price
        price: itemPrice,
        action: action === "increase" ? "increaseQuantity" : "decreaseQuantity"
      }),
      success: (response) => {
        if (response.success) {
          const updatedItems = cartItems.map(item => {
            if (item.id === itemId) {
              return {
                ...item,
                quantity: action === "increase" ? item.quantity + 1 : item.quantity - 1
              };
            }
            return item;
          });
          setCartItems(updatedItems);

          // Recalculate total price
          const updatedTotalPrice = updatedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
          setTotalPrice(updatedTotalPrice);
        } else {
          alert('Failed to update quantity.');
        }
      },
      error: (xhr, status, error) => {
        console.error("Error updating quantity:", status, error);
        alert(`Error: ${status} - ${error}`);
      }
    });
  };

  const handleCheckout = () => {
    navigate("/checkout", { state: { cartItems } });
  }

  return (
    <div className="cart-container">
      <h1>Cart Info</h1>
      {cartItems.length === 0 ? (
        <p>No items in your cart</p>
      ) : (
        <>
          <table className="product-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Product Name</th>
                <th>Product Image</th>
                <th>Price</th>
                <th>Quantity</th> 
                <th>Delete</th>
              </tr>
            </thead>
            <tbody>
              {cartItems.map((item) => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.name}</td>
                  <td>
                    <img src={item.image} alt={item.name} width="50" height="50" />
                  </td>
                  <td>${item.price}</td>
                  <td>
                    <button onClick={() => handleDecreaseQuantity(item.id, item.name, item.price, item.image)}>-</button>
                    <span>{item.quantity}</span>
                    <button onClick={() => handleIncreaseQuantity(item.id, item.name, item.price, item.image)}>+</button>
                  </td>
                  <td>
                    <button className="delete-btn" onClick={() => deleteItem(item.id, item.name, item.price, item.image)}>
                      <FaTrashAlt />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="total-price">
            <h2>Total Price: ${totalPrice.toFixed(2)}</h2>
          </div>
          <button className="checkout-btn" onClick={handleCheckout}>Checkout</button>
        </>
      )}
    </div>
  );
};

export default Cart;
