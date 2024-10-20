package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@WebServlet(urlPatterns = "/checkout", name = "CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private ProductCatalogUtility productCatalogUtility;

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

    if (username == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    String confirmationNumber = generateConfirmationNumber();

    BufferedReader reader = request.getReader();
    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
    JsonArray itemsArray = jsonObject.getAsJsonArray("cartItems");

    // Initialize productCatalogUtility if not already done
    productCatalogUtility = (ProductCatalogUtility) getServletContext().getAttribute("productCatalogUtility");
    if (productCatalogUtility == null) {
        String xmlFilePath = getServletContext().getInitParameter("ProductCatalogXMLPath");
        productCatalogUtility = new ProductCatalogUtility(xmlFilePath);
        getServletContext().setAttribute("productCatalogUtility", productCatalogUtility);
    }

    try {
        // Check product availability
        for (int i = 0; i < itemsArray.size(); i++) {
            JsonObject item = itemsArray.get(i).getAsJsonObject();
            int productId = item.get("id").getAsInt();
            int orderedQuantity = item.get("quantity").getAsInt();

            int availableQuantity = MySQLDataStoreUtilities.getProductQuantity(productId);
            if (availableQuantity < orderedQuantity) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("error", "Insufficient quantity available for product ID: " + productId);
                jsonResponse.addProperty("productName", item.get("name").getAsString());
                response.getWriter().write(jsonResponse.toString());
                return;
            }
        }

        // Proceed with saving the order if quantities are sufficient
        String deliveryOption = jsonObject.get("deliveryOption").getAsString();
        JsonObject addressObject = jsonObject.get("address").getAsJsonObject();
        String orderPlacedDate = jsonObject.get("orderPlacedDate").getAsString();
        String orderDeliveryDate = jsonObject.get("orderDeliveryDate").getAsString();
        double totalPrice = jsonObject.get("totalPrice").getAsDouble();
        String creditCardNumber = jsonObject.get("cardNumber").getAsString();
        JsonElement storeIdElement = jsonObject.get("storeId");
        int storeId = (storeIdElement != null && !storeIdElement.isJsonNull()) ? storeIdElement.getAsInt() : -1;
        JsonObject customerAddressObject = jsonObject.get("customerAddress").getAsJsonObject();
        String status = "orderPlaced";

        int orderId = MySQLDataStoreUtilities.saveOrder(username, confirmationNumber, totalPrice, deliveryOption, addressObject, orderPlacedDate, orderDeliveryDate, status, creditCardNumber, storeId, customerAddressObject);
        MySQLDataStoreUtilities.saveOrderItems(orderId, itemsArray);
        MySQLDataStoreUtilities.removeItemsFromCart(username);

        // Update product quantities in both the database and XML
        for (int i = 0; i < itemsArray.size(); i++) {
            JsonObject item = itemsArray.get(i).getAsJsonObject();
            int productId = item.get("id").getAsInt();
            int orderedQuantity = item.get("quantity").getAsInt();

            // Update product quantity in the database
            boolean quantityUpdated = MySQLDataStoreUtilities.updateProductQuantity(String.valueOf(productId), orderedQuantity);
            if (quantityUpdated) {
                try {
                    productCatalogUtility.updateProductQuantityInXML(String.valueOf(productId), orderedQuantity);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }
        }

        // Send response back with confirmation number and delivery date
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("confirmationNumber", confirmationNumber);
        jsonResponse.addProperty("deliveryDate", orderDeliveryDate);
        response.getWriter().write(jsonResponse.toString());

    } catch (SQLException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Store> stores = MySQLDataStoreUtilities.getStoreLocations();

            JsonArray jsonArray = new JsonArray();
            for (Store store : stores) {
                JsonObject jsonStore = new JsonObject();
                jsonStore.addProperty("store_id", store.getStoreId());
                jsonStore.addProperty("store_name", store.getStoreName());
                jsonStore.addProperty("street", store.getStreet());
                jsonStore.addProperty("city", store.getCity());
                jsonStore.addProperty("state", store.getState());
                jsonStore.addProperty("zip_code", store.getZipCode());
                jsonArray.add(jsonStore);
            }

            response.getWriter().write(jsonArray.toString());
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
