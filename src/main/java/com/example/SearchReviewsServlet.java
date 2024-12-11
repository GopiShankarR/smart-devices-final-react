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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

@WebServlet(urlPatterns = "/search-reviews", name = "SearchReviewsServlet")
public class SearchReviewsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String openAiApiKey;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        Properties properties = new Properties();
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            properties.load(input);
            openAiApiKey = properties.getProperty("OPEN_AI_API_KEY");
            System.out.println("OpenAI API Key loaded: " + openAiApiKey);
        } catch (Exception e) {
            e.printStackTrace();
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

            // Search for similar reviews in Elasticsearch
            JsonArray topReviews = searchReviewsInElasticSearch(queryEmbedding);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("reviews", topReviews);
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

    private JsonArray searchReviewsInElasticSearch(JsonArray queryEmbedding) throws IOException {
        String apiUrl = "http://localhost:9200/reviews/_search";
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
            + "        \"source\": \"cosineSimilarity(params.query_vector, 'reviewEmbedding') + 1.0\","
            + "        \"params\": {"
            + "          \"query_vector\": " + queryEmbedding.toString()
            + "        }"
            + "      }"
            + "    }"
            + "  },"
            + "  \"_source\": [\"productId\", \"reviewText\", \"reviewRating\", \"reviewDate\"],"
            + "  \"size\": 5,"
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

        // Parse and return the reviews from Elasticsearch
        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray hits = jsonResponse.getAsJsonObject("hits").getAsJsonArray("hits");

        JsonArray reviews = new JsonArray();
        for (int i = 0; i < hits.size(); i++) {
            JsonObject source = hits.get(i).getAsJsonObject().getAsJsonObject("_source");
            reviews.add(source);
        }

        return reviews;
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
