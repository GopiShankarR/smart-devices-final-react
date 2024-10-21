import React, { useEffect, useState } from "react";
import $ from "jquery";
import './../SalesReport.css';
import { Chart } from "react-google-charts";

const SalesReport = () => {
  const [salesData, setSalesData] = useState({
    productSalesData: [],
    dailySalesData: []
  });

  useEffect(() => {
    $.ajax({
      type: "GET",
      url: "http://localhost:8080/backend/sales-report",
      success: (response) => {
        console.log(response);
        setSalesData(response);
      },
      error: (xhr, status, error) => {
        console.error("Error fetching sales data:", error);
      }
    });
  }, []);

  const barChartData = [
    ["Product", "Total Sales"],
    ...salesData.productSalesData.map(data => [data.productName, data.totalSales]),
  ];

  const chartHeight = salesData.productSalesData.length * 40;

  const chartOptions = {
    title: "Total Sales by Product",
    chartArea: { width: "60%" },
    hAxis: { title: "Total Sales ($)", minValue: 0 },
    vAxis: {
      title: "Product",
      textStyle: { fontSize: 12 }
    },
    height: chartHeight,
    bar: { groupWidth: "75%" },
    legend: { position: "top", alignment: "end" }
  };

  return (
    <div className="sales-report">
      <h1>Sales Report</h1>

      <div className="sales-section">
        <h2>Product Sales Data</h2>
        <table className="sales-table">
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Product Price</th>
              <th>Items Sold</th>
              <th>Total Sales</th>
            </tr>
          </thead>
          <tbody>
            {salesData.productSalesData.map((data, index) => (
              <tr key={index}>
                <td>{data.productName}</td>
                <td>${data.productPrice.toFixed(2)}</td>
                <td>{data.itemsSold}</td>
                <td>${data.totalSales.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="sales-section">
        <h2>Total Sales by Product</h2>
        <Chart
          chartType="BarChart"
          width="100%"
          height={`${chartHeight}px`}
          data={barChartData}
          options={chartOptions}
        />
      </div>

      <div className="sales-section">
        <h2>Daily Sales Transactions</h2>
        <table className="sales-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Total Sales</th>
            </tr>
          </thead>
          <tbody>
            {salesData.dailySalesData.map((data, index) => (
              <tr key={index}>
                <td>{data.date}</td>
                <td>${data.totalSales.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default SalesReport;
