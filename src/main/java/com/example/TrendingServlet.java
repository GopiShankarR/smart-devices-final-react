package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;

@WebServlet(urlPatterns = "/trending", name = "TrendingServlet")
public class TrendingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> reviewsCollection;

    @Override
    public void init() throws ServletException {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("enterprise");
        reviewsCollection = database.getCollection("product_reviews");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        System.out.println("Inside here");

        JsonObject trendingData = new JsonObject();

        List<Document> topLikedProducts = reviewsCollection.aggregate(Arrays.asList(
                Aggregates.group("$productModelName", Accumulators.avg("avgRating", "$reviewRating")),
                Aggregates.sort(Sorts.descending("avgRating")),
                Aggregates.limit(5)
        )).into(new ArrayList<>());

        JsonArray topLikedProductsArray = new JsonArray();
        for (Document product : topLikedProducts) {
            JsonObject productJson = new JsonObject();
            String productModelName = product.getString("_id");
            productJson.addProperty("productModelName", productModelName);
            productJson.addProperty("avgRating", product.getDouble("avgRating"));
            
            String imageUrl = MySQLDataStoreUtilities.fetchProductImageFromMySQL(productModelName);
            productJson.addProperty("imageUrl", imageUrl);
            topLikedProductsArray.add(productJson);
        }
        trendingData.add("topLikedProducts", topLikedProductsArray);

        List<Document> topZipCodes = reviewsCollection.aggregate(Arrays.asList(
                Aggregates.group("$storeZip", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(6)
        )).into(new ArrayList<>());

        JsonArray topZipCodesArray = new JsonArray();
        for (Document zip : topZipCodes) {
            JsonObject zipJson = new JsonObject();
            zipJson.addProperty("zip", zip.getString("_id"));
            zipJson.addProperty("count", zip.getInteger("count"));
            topZipCodesArray.add(zipJson);
        }
        trendingData.add("topZipCodes", topZipCodesArray);

        // Get top 5 most sold products
        List<Document> topSoldProducts = reviewsCollection.aggregate(Arrays.asList(
                Aggregates.group("$productModelName", Accumulators.sum("soldCount", 1)),
                Aggregates.sort(Sorts.descending("soldCount")),
                Aggregates.limit(5)
        )).into(new ArrayList<>());

        JsonArray topSoldProductsArray = new JsonArray();
        for (Document product : topSoldProducts) {
            JsonObject productJson = new JsonObject();
            String productModelName = product.getString("_id");
            productJson.addProperty("productModelName", productModelName);
            productJson.addProperty("soldCount", product.getInteger("soldCount"));

            String imageUrl = MySQLDataStoreUtilities.fetchProductImageFromMySQL(productModelName);
            productJson.addProperty("imageUrl", imageUrl);
            topSoldProductsArray.add(productJson);
        }
        trendingData.add("topSoldProducts", topSoldProductsArray);

        response.getWriter().write(trendingData.toString());
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
