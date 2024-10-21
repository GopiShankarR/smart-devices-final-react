import React, { useState, useEffect } from 'react';
import './../Checkout.css';
import { useLocation, useNavigate } from "react-router-dom";
import $ from 'jquery';

const CheckoutPage = ({ username }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const cartItems = location.state?.cartItems || [];
  const totalPrice = cartItems.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2);
  const [deliveryOption, setDeliveryOption] = useState(null);
  const [confirmationNumber, setConfirmationNumber] = useState('');
  const [deliveryDate, setDeliveryDate] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [homeAddress, setHomeAddress] = useState({
    name: '',
    street: '',
    city: '',
    state: '',
    zip_code: ''
  });
  const [selectedStore, setSelectedStore] = useState('');

  const handleDeliveryOptionChange = (e) => {
    setDeliveryOption(e.target.value);
  };

  const handleHomeAddressChange = (e) => {
    setHomeAddress({ ...homeAddress, [e.target.name]: e.target.value });
  };

  const handleCardNumberChange = (e) => {
    const value = e.target.value;
    if (value.length <= 16) {
      setCardNumber(value);
    }
  };

  useEffect(() => {
    console.log(homeAddress);
  }, [homeAddress]);

  const convertStoreToAddress = (store) => {
    if (!store) return {}; 
    
    return {
      name: store.store_name || '', 
      street: store.street || '', 
      city: store.city || '', 
      state: store.state || '', 
      zip: store.zip_code || ''
    };
  };

  const formatDate = (isoDate) => {
    const date = new Date(isoDate);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`; 
  };

  const calculateDeliveryDate = (orderPlacedDate) => {
    const date = new Date(orderPlacedDate);
    date.setDate(date.getDate() + 14);
    return formatDate(date.toISOString());
  };

  const address = deliveryOption === 'homeDelivery'
    ? homeAddress
    : convertStoreToAddress(selectedStore);

  const completeCheckout = () => {
    const formattedOrderPlacedDate = formatDate(new Date().toISOString());
    const calculatedDeliveryDate = calculateDeliveryDate(formattedOrderPlacedDate);

    // Prepare the request data
    const requestData = {
      cartItems: cartItems.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity
      })),
      deliveryOption: deliveryOption,
      storeId: selectedStore?.store_id || null,
      address: address, 
      orderPlacedDate: formattedOrderPlacedDate,
      orderDeliveryDate: calculatedDeliveryDate,
      status: "orderPlaced",
      totalPrice: totalPrice,
      cardNumber: cardNumber,
      customerAddress: homeAddress
    };
    console.log("requestData",requestData);

    $.ajax({
      type: "POST",
      url: `http://localhost:8080/backend/checkout?username=${username}`,
      contentType: "application/json",
      data: JSON.stringify(requestData),
      success: (response) => {
        const { confirmationNumber, deliveryDate } = response;
        setConfirmationNumber(confirmationNumber);
        setDeliveryDate(deliveryDate);
        navigate('/checkout-success', {
          state: {
            confirmationNumber,
            deliveryDate,
            deliveryOption,
            address
          }
        });
      },
      error: (xhr, status, error) => {
        if (xhr.status === 400) {
          const response = JSON.parse(xhr.responseText);
          alert(`Error: ${response.error} - Product Name: ${response.productName}`);
        } else {
          alert(`Error: ${status} - ${error}`);
        }
      }
    });
};
  

  return (
    <div className="checkout-page">
      <div className="form-section">
        <CustomerInfo />
        <PaymentInfo cardNumber={cardNumber} onCardNumberChange={handleCardNumberChange}/>
        <div className="section">
          <h2>Delivery Options</h2>
          <div className="input-group">
            <label>
              <input
                type="radio"
                name="deliveryOption"
                value="storePickup"
                checked={deliveryOption === 'storePickup'}
                onChange={handleDeliveryOptionChange}
              />
              Store Pickup
            </label>
            <label>
              <input
                type="radio"
                name="deliveryOption"
                value="homeDelivery"
                checked={deliveryOption === 'homeDelivery'}
                onChange={handleDeliveryOptionChange}
              />
              Home Delivery
            </label>
          </div>
        </div>

        {deliveryOption === 'storePickup' && (
          <StorePickup selectedStore={selectedStore} setSelectedStore={setSelectedStore} />
        )}
        {deliveryOption === 'homeDelivery' && (
          <HomeDelivery address={homeAddress} onAddressChange={handleHomeAddressChange} />
        )}

      </div>

      <div className="cart-summary">
        <CartSummary cartItems={cartItems} totalPrice={totalPrice} deliveryOption={deliveryOption} />
      </div>

      <div className="checkout-button" onClick={completeCheckout}>
        <button className="checkout-btn">Complete Checkout and Pay</button>
      </div>
    </div>
  );
};

const StorePickup = ({ selectedStore, setSelectedStore }) => {
  const [stores, setStores] = useState([]);

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: "http://localhost:8080/backend/checkout",
      contentType: "application/json",
      success: (response) => {
        setStores(response);
      },
      error: (xhr, status, error) => {
        console.error("Error fetching store locations:", status, error);
      }
    });
  }, []);

  const handleStoreChange = (e) => {
    const store = stores.find(store => store.store_id === parseInt(e.target.value)); // Find the selected store
    setSelectedStore(store); // Store the selected store object
  };

  return (
    <div className="section">
      <h3>Pick a Store</h3>
      <select value={selectedStore?.store_id || ''} onChange={handleStoreChange}>
        {stores.length > 0 ? (
          stores.map((store, index) => (
            <option key={index} value={store.store_id}>  {/* Use store_id */}
              {store.store_name}, {store.street}, {store.city}, {store.state} {store.zip_code}
            </option>
          ))
        ) : (
          <option>Loading stores...</option>
        )}
      </select>
    </div>
  );
};

const HomeDelivery = ({ address, onAddressChange }) => (
  <div className="section">
    <h3>Shipping Address</h3>
    <input
      type="text"
      name="name"
      placeholder="Name"
      value={address.name}
      onChange={onAddressChange}
    />
    <input
      type="text"
      name="street"
      placeholder="Street Address"
      value={address.street}
      onChange={onAddressChange}
    />
    <div className="input-group">
      <input
        type="text"
        name="city"
        placeholder="City"
        value={address.city}
        onChange={onAddressChange}
      />
      <input
        type="text"
        name="state"
        placeholder="State"
        value={address.state}
        onChange={onAddressChange}
      />
      <input
        type="text"
        name="zip_code"
        placeholder="Zip Code"
        value={address.zip_code}
        onChange={onAddressChange}
      />
    </div>
  </div>
);

const CustomerInfo = () => (
  <div className="section">
    <h2>Customer Info</h2>
    <div className="input-group">
      <input type="text" placeholder="First Name" />
      <input type="text" placeholder="Last Name" />
    </div>
    <input type="email" placeholder="Email" />
    <input type="text" placeholder="Address" />
    <div className="input-group">
      <input type="text" placeholder="City" />
      <input type="text" placeholder="State" />
    </div>
  </div>
);

const PaymentInfo = ({ cardNumber, onCardNumberChange }) => {
  const [month, setMonth] = useState('');
  const [year, setYear] = useState('');
  const [cvc, setCVC] = useState('');

  const handleMonthChange = (e) => {
    const value = e.target.value;
    if (value.length <= 2 && Number(value) >= 0 && Number(value) <= 12) {
      setMonth(value);
    }
  };

  const handleYearChange = (e) => {
    const value = e.target.value;
    if (value.length <= 4) {
      setYear(value);
    }
  };

  const handleCVCChange = (e) => {
    const value = e.target.value;
    if (value.length <= 3) {
      setCVC(value);
    }
  };

  return (
    <div className="section">
      <h2>Payment Info</h2>
      <div className="payment-methods">
        <label className="left-align">Credit Card Details</label>
      </div>
      <input
        type="number"
        placeholder="Credit Card Number"
        value={cardNumber}
        onChange={onCardNumberChange}
        required
      />
      <div className="input-group">
        <input
          type="number"
          placeholder="MM"
          value={month}
          onChange={handleMonthChange}
          required
        />
        <input
          type="number"
          placeholder="YYYY"
          value={year}
          onChange={handleYearChange}
          required
        />
        <input
          type="number"
          placeholder="CVC"
          value={cvc}
          onChange={handleCVCChange}
          required
        />
      </div>
    </div>
  );
};

const shippingCost = 10;
const CartSummary = ({ cartItems, totalPrice, deliveryOption }) => (
  <div className="section">
    <h2>Current Cart</h2>
    <div className="cart-items">
      {cartItems.map((item, index) => (
        <div key={index} className="cart-item">
          <span>{item.name}</span>
          <span>${item.price}</span>
        </div>
      ))}
    </div>
    {deliveryOption === 'homeDelivery' && (<div className="total">
      <h3>Shipping Cost</h3>
      <span>${shippingCost}</span>
    </div>)}
    <div className="total">
      <h3>Cart Totals</h3>
      {deliveryOption === 'homeDelivery' ? (<span>Total: ${Number(totalPrice) + Number(shippingCost)}</span>) : (<span>Total: ${totalPrice}</span>)}
    </div>
  </div>
);

export default CheckoutPage;
