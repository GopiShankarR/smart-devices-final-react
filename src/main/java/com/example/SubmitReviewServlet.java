package com.example;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bson.Document;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


@WebServlet(urlPatterns = "/submitReview", name = "SubmitReviewServlet")
public class SubmitReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String openAiApiKey;

    @Override
    public void init() throws ServletException {
        super.init();

        // Initialize MongoDB Connection
        MongoDBDataStoreUtilities.initMongoDB();

        // Load OpenAI API Key
        ServletContext context = getServletContext();
        Properties properties = new Properties();
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            if (input == null) {
                throw new IOException("Properties file not found in /WEB-INF/config.properties");
            }
            properties.load(input);

            // Get the OpenAI API key from properties file
            openAiApiKey = properties.getProperty("OPEN_AI_API_KEY");
            if (openAiApiKey == null || openAiApiKey.isEmpty()) {
                throw new IOException("OpenAI API Key not found in properties file");
            }
            System.out.println("OpenAI API Key loaded: " + openAiApiKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to initialize OpenAI API Key", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse input JSON
            JsonObject jsonObject = JsonParser.parseReader(request.getReader()).getAsJsonObject();
            String reviewText = jsonObject.get("reviewText").getAsString();

            // Generate Embedding for Review Text
            List<Double> reviewEmbedding = generateEmbedding(reviewText);

            // Create Review Document for MongoDB
            Document reviewDocument = new Document("productModelName", jsonObject.get("productModelName").getAsString())
                    .append("productCategory", jsonObject.get("productCategory").getAsString())
                    .append("productPrice", jsonObject.get("productPrice").getAsDouble())
                    .append("storeId", jsonObject.get("storeId").getAsString())
                    .append("storeZip", jsonObject.get("storeZip").getAsString())
                    .append("storeCity", jsonObject.get("storeCity").getAsString())
                    .append("storeState", jsonObject.get("storeState").getAsString())
                    .append("productOnSale", jsonObject.get("productOnSale").getAsBoolean())
                    .append("manufacturerName", jsonObject.get("manufacturerName").getAsString())
                    .append("manufacturerRebate", jsonObject.get("manufacturerRebate").getAsBoolean())
                    .append("user", new Document("userId", jsonObject.get("userId").getAsString())
                            .append("userAge", jsonObject.get("userAge").getAsInt())
                            .append("userGender", jsonObject.get("userGender").getAsString())
                            .append("userOccupation", jsonObject.get("userOccupation").getAsString()))
                    .append("reviewRating", jsonObject.get("reviewRating").getAsInt())
                    .append("reviewDate", jsonObject.get("reviewDate").getAsString())
                    .append("reviewText", reviewText)
                    .append("reviewEmbedding", reviewEmbedding);

            // Save Review to MongoDB
            MongoDBDataStoreUtilities.insertReview(reviewDocument);

            // Push Review Data to Elasticsearch
            pushReviewToElasticsearch(jsonObject, reviewEmbedding);

            // Response
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "An error occurred while processing the review.");
            response.getWriter().write(errorResponse.toString());
        }
    }

    private List<Double> generateEmbedding(String text) throws IOException {
        String apiUrl = "https://api.openai.com/v1/embeddings";
        String model = "text-embedding-3-small";

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("input", text);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        JsonObject response = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
        List<Double> embedding = new ArrayList<>();
        response.getAsJsonArray("data").get(0).getAsJsonObject().getAsJsonArray("embedding").forEach(e -> embedding.add(e.getAsDouble()));

        return embedding;
    }

    private void pushReviewToElasticsearch(JsonObject reviewJson, List<Double> embedding) throws IOException {
        // Fetch productId using productModelName from MySQL
        String productModelName = reviewJson.get("productModelName").getAsString();
        Integer productId = MySQLDataStoreUtilities.fetchProductIdByModelName(productModelName);

        if (productId == null) {
            System.err.println("Product ID not found for model name: " + productModelName + ". Skipping...");
            return; // Skip pushing to Elasticsearch if productId is not found
        }

        try (RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build()) {
            Request request = new Request("POST", "/reviews/_doc");
            
            JsonObject elasticDocument = new JsonObject();
            elasticDocument.addProperty("productId", productId);
            elasticDocument.addProperty("reviewRating", reviewJson.get("reviewRating").getAsInt());
            elasticDocument.addProperty("reviewDate", reviewJson.get("reviewDate").getAsString());
            elasticDocument.addProperty("reviewText", reviewJson.get("reviewText").getAsString());
            elasticDocument.add("reviewEmbedding", JsonParser.parseString(embedding.toString()));

            request.setJsonEntity(elasticDocument.toString());
            restClient.performRequest(request);
        }
    }

    @Override
    public void destroy() {
        MongoDBDataStoreUtilities.closeMongoDB();
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
