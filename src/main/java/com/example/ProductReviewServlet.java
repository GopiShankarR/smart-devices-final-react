package com.example;

import java.sql.SQLException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

@WebServlet(urlPatterns = "/review", name = "ProductReviewServlet")
public class ProductReviewServlet extends HttpServlet {
  private static final long serialVersionUID = 1;

  private MongoClient mongoClient;
  private MongoDatabase database;
  private MongoCollection<Document> reviewsCollection;

  @Override
  public void init() throws ServletException {
    super.init();
    mongoClient = MongoClients.create("mongodb://localhost:27017");
    database = mongoClient.getDatabase("enterprise");
    reviewsCollection = database.getCollection("product_reviews");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String productId = request.getParameter("productId");
    String username = request.getParameter("username");
    String confirmationNumber = request.getParameter("confirmationNumber");

    if (productId == null || username == null || confirmationNumber == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Missing required parameters");
        return;
    }

    try {
        JsonObject productReviewData = MySQLDataStoreUtilities.getProductReviewData(productId, username, confirmationNumber);

        if (productReviewData != null) {
            response.setContentType("application/json");
            response.getWriter().write(productReviewData.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No matching order or product found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Error retrieving product information.");
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

    String productId = jsonObject.get("product_id").getAsString();
    String reviewer = jsonObject.get("reviewer").getAsString();
    double rating = jsonObject.get("rating").getAsDouble();
    String comment = jsonObject.get("comment").getAsString();

    Document reviewDocument = new Document("product_id", productId)
                              .append("reviewer", reviewer)
                              .append("rating", rating)
                              .append("comment", comment)
                              .append("date", new Date());
    reviewsCollection.insertOne(reviewDocument);

    JsonObject responseJson = new JsonObject();
    responseJson.addProperty("success", true);
    response.setContentType("application/json");
    response.getWriter().write(responseJson.toString());
  }

  @Override
  protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
    response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    response.addHeader("Access-Control-Max-Age", "3600");
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  public void destroy() {
    mongoClient.close();
  }
}