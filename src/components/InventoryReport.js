import React, { useEffect, useState } from "react";
import $ from "jquery";
import './../InventoryReport.css';
import { Chart } from "react-google-charts";

const InventoryReport = () => {
  const [inventoryData, setInventoryData] = useState({
    productInventories: [],
    productsOnSale: [],
    productsWithRebates: []
  });

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: "http://localhost:8080/backend/inventory-report",
      success: (response) => {
        console.log(response);
        setInventoryData(response);
      },
      error: (xhr, status, error) => {
        console.error("Error fetching inventory data:", error);
      }
    });
  }, []);

  const SALE_DISCOUNT_PERCENTAGE = 0.10; 
  const REBATE_PERCENTAGE = 0.05;

  const getEffectivePrice = (product) => {
    let price = product.price;

    if (product.onSale) {
      price = price - (price * SALE_DISCOUNT_PERCENTAGE);
    }

    if (product.manufacturerRebate) {
      price = price - (price * REBATE_PERCENTAGE);
    }

    return price.toFixed(2);
  };

  const barChartData = [
    ["Product", "Total Quantity"],
    ...inventoryData.productInventories.map(product => [product.productName, product.totalQuantity]),
  ];

  // Set chart height dynamically based on the number of products
  const chartHeight = inventoryData.productInventories.length * 40; // Adjust 40 to control row height

  const chartOptions = {
    title: "Inventory Levels by Product",
    chartArea: { width: "60%" },
    hAxis: { title: "Total Quantity", minValue: 0 },
    vAxis: {
      title: "Product",
      textStyle: { fontSize: 12 }
    },
    height: chartHeight,
    bar: { groupWidth: "75%" },
    legend: { position: "top", alignment: "end" }
  };

  return (
    <div className="inventory-report">
      <h1>Inventory Report</h1>

      {/* Product Inventory Table */}
      <div className="inventory-section">
        <h2>Product Inventory</h2>
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Price</th>
              <th>Available Quantity</th>
            </tr>
          </thead>
          <tbody>
            {inventoryData.productInventories.map((product, index) => (
              <tr key={index}>
                <td>{product.productName}</td>
                <td>${getEffectivePrice(product)}</td>
                <td>{product.totalQuantity}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Inventory Levels Bar Chart */}
      <div className="inventory-section">
        <h2>Inventory Levels Bar Chart</h2>
        <Chart
          chartType="BarChart"
          width="100%"
          height={`${chartHeight}px`}
          data={barChartData}
          options={chartOptions}
        />
      </div>

      {/* Products on Sale Table */}
      <div className="inventory-section">
        <h2>Products on Sale</h2>
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Original Price</th>
              <th>Sale Price</th>
            </tr>
          </thead>
          <tbody>
            {inventoryData.productsOnSale.map((product, index) => (
              <tr key={index}>
                <td>{product.productName}</td>
                <td>${product.price.toFixed(2)}</td>
                <td>${(product.price - (product.price * SALE_DISCOUNT_PERCENTAGE)).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Products with Manufacturer Rebates Table */}
      <div className="inventory-section">
        <h2>Products with Manufacturer Rebates</h2>
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Price</th>
              <th>Rebate Amount</th>
            </tr>
          </thead>
          <tbody>
            {inventoryData.productsWithRebates.map((product, index) => (
              <tr key={index}>
                <td>{product.productName}</td>
                <td>${product.price.toFixed(2)}</td>
                <td>${(product.price * REBATE_PERCENTAGE).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InventoryReport;
