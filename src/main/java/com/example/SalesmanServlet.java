package com.example;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.security.SecureRandom;

@WebServlet(urlPatterns = "/salesman", name = "SalesmanServlet")
public class SalesmanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/your_database_name"; 
        String jdbcUser = "root";
        String jdbcPassword = "password";
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        switch (action) {
            case "getOrders":
              getOrders(response);
              break;
            case "getOrderDetails":
              getOrderDetails(request, response);
              break;
            default:
              response.getWriter().write("{\"error\": \"Invalid action\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        String action = jsonObject.get("action").getAsString();

        switch (action) {
            case "add":
              addOrder(jsonObject, response);
              break;
            case "update":
              updateOrder(jsonObject, response);
              break;
            case "delete":
              deleteOrder(jsonObject, response);
              break;
            case "addCustomer":
              addCustomer(jsonObject, response);
              break;
            default:
              response.getWriter().write("{\"error\": \"Invalid action\"}");
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

    private void getOrders(HttpServletResponse response) throws IOException {
        try {
            JsonArray jsonOrders = MySQLDataStoreUtilities.getAllOrders();
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("orders", jsonOrders);
            response.getWriter().write(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Error fetching orders.\"}");
        }
    }

    private void getOrderDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String confirmationNumber = request.getParameter("confirmationNumber");

        if (confirmationNumber == null || confirmationNumber.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Confirmation number is required\"}");
            return;
        }

        try {
            JsonObject jsonOrderDetails = MySQLDataStoreUtilities.getOrderDetailsByConfirmationNumber(confirmationNumber);
            response.getWriter().write(jsonOrderDetails.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Error fetching order details.\"}");
        }
    }

    private void addOrder(JsonObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.get("username").getAsString();
        String product = jsonObject.get("product").getAsString();
        double discount = jsonObject.has("discount") ? jsonObject.get("discount").getAsDouble() : 0;
        String deliveryOption = jsonObject.get("deliveryOption").getAsString();

        Product productInfo = MySQLDataStoreUtilities.getProductByName(product);
        if (productInfo == null) {
            response.getWriter().write("{\"error\": \"Product not found\"}");
            return;
        }

        double finalPrice = productInfo.getPrice() - discount;

        String confirmationNumber = generateConfirmationNumber();

        MySQLDataStoreUtilities.addOrder(username, confirmationNumber, productInfo.getName(), finalPrice, discount, deliveryOption);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("confirmationNumber", confirmationNumber);
        response.getWriter().write(jsonResponse.toString());
    }


    private void updateOrder(JsonObject jsonObject, HttpServletResponse response) throws IOException {
        try {
            MySQLDataStoreUtilities.updateOrder(jsonObject);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Order updated successfully.");
            response.getWriter().write(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Error updating order.\"}");
        }
    }

    private void deleteOrder(JsonObject jsonObject, HttpServletResponse response) throws IOException {
        String confirmationNumber = jsonObject.get("confirmationNumber").getAsString();

        try {
            boolean deleted = MySQLDataStoreUtilities.deleteOrder(confirmationNumber);
            JsonObject jsonResponse = new JsonObject();
            if (deleted) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Order deleted successfully.");
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Order not found.");
            }
            response.getWriter().write(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Error deleting order.\"}");
        }
    }

    private void addCustomer(JsonObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        JsonObject jsonResponse = new JsonObject();
        try {
            boolean isAdded = MySQLDataStoreUtilities.addCustomer(username, password);
            if (isAdded) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Customer added successfully.");
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Failed to add customer.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Database error: " + e.getMessage());
        }
        response.getWriter().write(jsonResponse.toString());
    }

    private String generateConfirmationNumber() {
      String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      int CONFIRMATION_NUMBER_LENGTH = 10;
      SecureRandom random = new SecureRandom();
      StringBuilder sb = new StringBuilder(CONFIRMATION_NUMBER_LENGTH);
      for (int i = 0; i < CONFIRMATION_NUMBER_LENGTH; i++) {
          int index = random.nextInt(CHARACTERS.length());
          sb.append(CHARACTERS.charAt(index));
      }
      return sb.toString();
    }

}
