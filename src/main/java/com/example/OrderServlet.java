package com.example;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/order", name = "OrderServlet")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        String action = jsonObject.get("action").getAsString();
        String confirmationNumber = jsonObject.get("confirmationNumber").getAsString();

        if ("cancelOrder".equals(action)) {
            try {
                cancelOrder(confirmationNumber, response);
            } catch (SQLException e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel order.");
                e.printStackTrace();
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String username = request.getParameter("username");

    if (username == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    try {
        // Now we're fetching orders with associated product names
        List<OrderInfo> orders = MySQLDataStoreUtilities.getOrdersWithProductsByUsername(username);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("orders", gson.toJsonTree(orders));
        response.getWriter().write(gson.toJson(jsonResponse));
    } catch (SQLException e) {
        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch orders.");
        e.printStackTrace();
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

    private void cancelOrder(String confirmationNumber, HttpServletResponse response) throws IOException, SQLException {
        boolean success = MySQLDataStoreUtilities.cancelOrder(confirmationNumber);
        if (success) {
            sendSuccessResponse(response, "Order canceled successfully.");
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", message);
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
