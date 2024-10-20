package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/sales-report", name = "SalesReportServlet")
public class SalesReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            List<SalesData> salesDataList = MySQLDataStoreUtilities.getSalesDataPerProduct(conn);
            List<DailySalesData> dailySalesDataList = MySQLDataStoreUtilities.getDailySalesData(conn);

            JsonObject responseJson = new JsonObject();

            JsonArray salesDataArray = new JsonArray();
            for (SalesData salesData : salesDataList) {
                JsonObject jsonSalesData = new JsonObject();
                jsonSalesData.addProperty("productName", salesData.getProductName());
                jsonSalesData.addProperty("productPrice", salesData.getProductPrice());
                jsonSalesData.addProperty("itemsSold", salesData.getItemsSold());
                jsonSalesData.addProperty("totalSales", salesData.getTotalSales());
                salesDataArray.add(jsonSalesData);
            }

            JsonArray dailySalesArray = new JsonArray();
            for (DailySalesData dailySales : dailySalesDataList) {
                JsonObject jsonDailySales = new JsonObject();
                jsonDailySales.addProperty("date", dailySales.getDate());
                jsonDailySales.addProperty("totalSales", dailySales.getTotalSales());
                dailySalesArray.add(jsonDailySales);
            }

            responseJson.add("productSalesData", salesDataArray);
            responseJson.add("dailySalesData", dailySalesArray);

            response.getWriter().write(responseJson.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
