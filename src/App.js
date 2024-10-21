import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import './App.css';
import React, { useState, useEffect } from 'react';
// import CommunicationForm from './CommunicationForm';
import Login from './components/Login';
import Signup from './components/Signup';
import Home from './components/Home';
import Header from './components/Header';
import ProductDetail from './components/ProductDetail';
import ProtectedRoute from './components/ProtectedRoute';
import Cart from './components/Cart';
import CheckoutPage from './components/CheckoutPage';
import CheckoutSuccessPage from './components/CheckoutSuccessPage';
import ProductCategory from './components/ProductCategory';
import StoreManager from './components/StoreManager';
import AddProduct from './components/AddProduct';
import UpdateProduct from './components/UpdateProduct';
import Salesman from './components/Salesman';
import AddCustomer from './components/AddCustomer';
import AddOrder from './components/AddOrder';
import OrderDetails from './components/OrderDetails';
import OrdersPage from './components/OrdersPage';
import ProductReviewPage from './components/ProductReviewPage';
import TrendingPage from './components/TrendingPage';
import InventoryReport from './components/InventoryReport';
import SalesReport from './components/SalesReport';
import ProductDetails from './components/ProductDetails';


function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userType, setUserType] = useState("");
  const [username, setUsername] = useState("");

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUserType("");
    setUsername("");
  }

  return (
    <Router>
      <Header isLoggedIn={isLoggedIn} userType={userType} username={username} onLogout={handleLogout} />
      <div className="App">
        <Routes>
          <Route exact="true" path="/" element={<Login setIsLoggedIn={setIsLoggedIn} setUserType={setUserType} setUsername={setUsername} />} />
          <Route path="/store-manager" element={<StoreManager isLoggedIn={isLoggedIn} userType="Store Manager" />}/>
          <Route path="/add-product" element={<AddProduct />} />
          <Route path="/salesman" element={<Salesman isLoggedIn={isLoggedIn} userType="Salesman" />} />
          <Route path="/orders/:username" element={<OrdersPage />} />
          <Route path="/add-customer" element={<AddCustomer />} />
          <Route path="/add-order" element={<AddOrder />} />
          <Route path="/product/:id" element={<ProductDetail />} />
          <Route path="/product-details" element={<ProductDetails />} />
          <Route path="/order/:confirmation-number" element={<OrderDetails />} />
          <Route path="/update-product" element={<UpdateProduct />} />
          <Route path="/signup" element={<Signup setIsLoggedIn={setIsLoggedIn} setUserType={setUserType} setUsername={setUsername} />} />
          <Route path="/home" element={<ProtectedRoute isLoggedIn={isLoggedIn}><Home /></ProtectedRoute>} />
          <Route path="/door-locks" element={<ProductCategory category="smart doorlocks" username={username} />} />
          <Route path="/doorbells" element={<ProductCategory category="smart doorbells" username={username} />} />
          <Route path="/climate-control" element={<ProductCategory category="climate control" username={username}/>} />
          <Route path="/lightings" element={<ProductCategory category="smart lights" username={username} />} />
          <Route path="/speakers" element={<ProductCategory category="speakers" username={username} />} />
          <Route path="/cart" element={<Cart username={username} />} />
          <Route path="/checkout" element={<CheckoutPage username={username} />} />
          <Route path="/inventory-report" element={<InventoryReport />} />
          <Route path="/sales-report" element={<SalesReport />} />
          <Route path="/add-product-review" element={<ProductReviewPage />} />
          <Route path="/trending" element={<TrendingPage />} />
          <Route path="/checkout-success" element={<CheckoutSuccessPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
