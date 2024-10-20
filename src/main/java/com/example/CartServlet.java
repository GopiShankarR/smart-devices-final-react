package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet(urlPatterns = "/cart", name = "CartServlet")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.addHeader("Access-Control-Allow-Origin", "*");
      response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
      response.addHeader("Access-Control-Allow-Headers", "Content-Type");
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      HttpSession session = request.getSession();
      String username = request.getParameter("username");
      session.setAttribute("username", username);
      
      if (username == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
      }

      try {
          List<CartItem> cartItems = MySQLDataStoreUtilities.getCartItems(username);
          double totalPrice = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

          JsonArray jsonArray = new JsonArray();
          for (CartItem item : cartItems) {
              JsonObject jsonItem = new JsonObject();
              jsonItem.addProperty("username", item.getUsername());
              jsonItem.addProperty("id", item.getId());
              jsonItem.addProperty("name", item.getName());
              jsonItem.addProperty("image", item.getImage());
              jsonItem.addProperty("price", item.getPrice());
              jsonItem.addProperty("quantity", item.getQuantity());
              jsonArray.add(jsonItem);
          }

          JsonObject responseJson = new JsonObject();
          responseJson.add("items", jsonArray);
          responseJson.addProperty("totalPrice", totalPrice);
          response.getWriter().write(responseJson.toString());

      } catch (SQLException e) {
          e.printStackTrace();
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        session.setAttribute("username", username);

        BufferedReader reader = request.getReader();
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        String action = jsonObject.get("action").getAsString();
        String itemId = jsonObject.get("id").getAsString();
        String imageUrl = "";
    if (jsonObject.has("image") && !jsonObject.get("image").isJsonNull()) {
        imageUrl = jsonObject.get("image").getAsString();
    } else {
        System.out.println("Image URL not provided in the request");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("error", "Image URL is missing from the request");
        response.getWriter().write(jsonResponse.toString());
        return;
    }

    double itemPrice = 0.0;
    if (jsonObject.has("price") && !jsonObject.get("price").isJsonNull()) {
        itemPrice = jsonObject.get("price").getAsDouble();
    } else {
        System.out.println("Price not provided in the request");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("error", "Price is missing from the request");
        response.getWriter().write(jsonResponse.toString());
        return;
    }

    int quantity = jsonObject.has("quantity") && !jsonObject.get("quantity").isJsonNull() 
                   ? jsonObject.get("quantity").getAsInt() 
                   : 1;  // Default to 1 if quantity is not provided


        try {
            int cartId = MySQLDataStoreUtilities.getOrCreateCartId(username);

            if ("increaseQuantity".equals(action) || "add".equals(action)) {
                MySQLDataStoreUtilities.addItemToCart(cartId, itemId, itemPrice, 1, imageUrl);
            } else if ("decreaseQuantity".equals(action)) {
                MySQLDataStoreUtilities.decreaseItemQuantity(cartId, itemId);
            } else if ("delete".equals(action)) {
                MySQLDataStoreUtilities.deleteItemFromCart(cartId, itemId);
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            response.getWriter().write(jsonResponse.toString());

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
