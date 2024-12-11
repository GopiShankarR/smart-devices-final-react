package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.*;
import java.util.stream.Collectors;

import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet(urlPatterns = "/recommend-product", name = "RecommendProductServlet")
public class RecommendProductServlet extends HttpServlet {

    private String openAiApiKey;
    private RestClient elasticSearchClient;

    @Override
    public void init() {
        // Load OpenAI API Key
        ServletContext context = getServletContext();
        Properties properties = new Properties();
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            properties.load(input);
            openAiApiKey = properties.getProperty("OPEN_AI_API_KEY");
            System.out.println("OpenAI API Key loaded: " + openAiApiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize ElasticSearch client
        elasticSearchClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        JsonObject requestBody = JsonParser.parseReader(reader).getAsJsonObject();
        String queryText = requestBody.get("query").getAsString();

        try {
            // Generate the embedding for the input query
            JsonArray queryEmbedding = getEmbedding(queryText);

            if (queryEmbedding == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to generate embedding.\"}");
                return;
            }

            // Search for similar products in Elasticsearch
            JsonArray recommendedProducts = searchProductsInElasticSearch(queryEmbedding);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("recommended_products", recommendedProducts);
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An error occurred while processing the request.\"}");
        }
    }

    private JsonArray getEmbedding(String inputText) throws IOException {
        String apiUrl = "https://api.openai.com/v1/embeddings";
        String model = "text-embedding-3-small";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Create request payload for OpenAI API
        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("input", inputText);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        return jsonResponse.getAsJsonArray("data").get(0).getAsJsonObject().get("embedding").getAsJsonArray();
    }

    private JsonArray searchProductsInElasticSearch(JsonArray queryEmbedding) throws IOException {
        String apiUrl = "http://localhost:9200/products/_search";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Elasticsearch query for cosine similarity
        String elasticSearchQuery = "{"
            + "  \"query\": {"
            + "    \"script_score\": {"
            + "      \"query\": { \"match_all\": {} },"
            + "      \"script\": {"
            + "        \"source\": \"cosineSimilarity(params.query_vector, 'product_embedding') + 1.0\","
            + "        \"params\": {"
            + "          \"query_vector\": " + queryEmbedding.toString()
            + "        }"
            + "      }"
            + "    }"
            + "  },"
            + "  \"_source\": [\"product_name\", \"price\", \"category\", \"description\"],"
            + "  \"size\": 10,"
            + "  \"sort\": ["
            + "    { \"_score\": { \"order\": \"desc\" } }"
            + "  ]"
            + "}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = elasticSearchQuery.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Parse and return the products from Elasticsearch
        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray hits = jsonResponse.getAsJsonObject("hits").getAsJsonArray("hits");

        JsonArray products = new JsonArray();
        for (int i = 0; i < hits.size(); i++) {
            JsonObject source = hits.get(i).getAsJsonObject().getAsJsonObject("_source");
            products.add(source);
        }

        return products;
    }

    @Override
    public void destroy() {
        try {
            elasticSearchClient.close();
        } catch (IOException e) {
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
}
