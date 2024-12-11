package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = "/product", name = "ProductServlet")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductCatalogUtility productCatalogUtility;
    private String openAiApiKey;

    @Override
    public void init() throws ServletException {
        super.init();

        // Initialize Product Catalog Utility and MySQL Driver
        try {
            String xmlFilePath = getServletContext().getInitParameter("ProductCatalogXMLPath");
            if (xmlFilePath == null) {
                throw new ServletException("ProductCatalogXMLPath not configured in context parameters.");
            }
            productCatalogUtility = new ProductCatalogUtility(xmlFilePath);
            getServletContext().setAttribute("productCatalogUtility", productCatalogUtility);

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new ServletException("Initialization error", e);
        }

        // Load OpenAI API Key
        ServletContext context = getServletContext();
        Properties properties = new Properties();
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            properties.load(input);
            openAiApiKey = properties.getProperty("OPEN_AI_API_KEY");
            System.out.println("OpenAI API Key loaded: " + openAiApiKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to load OpenAI API key.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String category = request.getParameter("category");
        String id = request.getParameter("id");
        String name = request.getParameter("name");

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            List<Product> products;

            if (name != null) {
                Product product = MySQLDataStoreUtilities.getProductByNameWithConnection(conn, name);
                if (product != null) {
                    JsonObject jsonProduct = new JsonObject();
                    jsonProduct.addProperty("id", product.getId());
                    jsonProduct.addProperty("name", product.getName());
                    jsonProduct.addProperty("price", product.getPrice());
                    jsonProduct.addProperty("description", product.getDescription());
                    jsonProduct.addProperty("manufacturer", product.getManufacturer());
                    jsonProduct.addProperty("imageUrl", product.getImageUrl());
                    jsonProduct.addProperty("category", product.getCategory());
                    jsonProduct.addProperty("onSale", product.isOnSale());
                    jsonProduct.addProperty("manufacturerRebate", product.hasManufacturerRebate());
                    jsonProduct.addProperty("productQuantity", product.getProductQuantity());

                    response.getWriter().write(jsonProduct.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }


            if (category == null && id == null) {
                products = MySQLDataStoreUtilities.getAllProducts(conn);
            } else if (category != null && id == null) {
                products = MySQLDataStoreUtilities.getProductsByCategory(conn, category);
            } else if (category != null && id != null) {
                products = MySQLDataStoreUtilities.getProductByIdAndCategory(conn, Integer.parseInt(id), category);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            JsonArray jsonArray = new JsonArray();
            for (Product product : products) {
                JsonObject jsonProduct = new JsonObject();
                jsonProduct.addProperty("id", product.getId());
                jsonProduct.addProperty("name", product.getName());
                jsonProduct.addProperty("price", product.getPrice());
                jsonProduct.addProperty("description", product.getDescription());
                jsonProduct.addProperty("manufacturer", product.getManufacturer());
                jsonProduct.addProperty("imageUrl", product.getImageUrl());
                jsonProduct.addProperty("category", product.getCategory());
                jsonProduct.addProperty("onSale", product.isOnSale());
                jsonProduct.addProperty("manufacturerRebate", product.hasManufacturerRebate());
                jsonProduct.addProperty("productQuantity", product.getProductQuantity());
                jsonArray.add(jsonProduct);
            }

            response.getWriter().write(jsonArray.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        String action = jsonObject.has("action") ? jsonObject.get("action").getAsString() : "add";

        try {
            JsonObject jsonResponse = new JsonObject();
            boolean success = false;

            productCatalogUtility = (ProductCatalogUtility) getServletContext().getAttribute("productCatalogUtility");

            if (productCatalogUtility == null) {
                String xmlFilePath = getServletContext().getInitParameter("ProductCatalogXMLPath");
                productCatalogUtility = new ProductCatalogUtility(xmlFilePath);
                getServletContext().setAttribute("productCatalogUtility", productCatalogUtility);
            }

            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

            if ("add".equals(action)) {
                int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : 0;
                String name = jsonObject.get("name").getAsString();
                double price = jsonObject.get("price").getAsDouble();
                String description = jsonObject.get("description").getAsString();
                String manufacturer = jsonObject.get("manufacturer").getAsString();
                String imageUrl = jsonObject.get("imageUrl").getAsString();
                String category = jsonObject.get("category").getAsString();
                JsonArray aidsArray = jsonObject.has("aids") ? jsonObject.getAsJsonArray("aids") : null;
                int productQuantity = jsonObject.has("productQuantity") ? jsonObject.get("productQuantity").getAsInt() : 0;
                boolean onSale = jsonObject.has("onSale") ? jsonObject.get("onSale").getAsBoolean() : false;
                boolean manufacturerRebate = jsonObject.has("manufacturerRebate") ? jsonObject.get("manufacturerRebate").getAsBoolean() : false;

                if (id == 0) {
                    id = productCatalogUtility.generateNewProductId();
                }

                Product product = new Product(id, name, price, description, manufacturer, imageUrl, category, productQuantity, onSale, manufacturerRebate);

                // Generate embedding for description
                List<Double> descriptionEmbedding = generateEmbedding(description);

                if (descriptionEmbedding != null) {
                    // Add product to MySQL, including embedding
                    success = dbUtil.addProductWithId(String.valueOf(id), name, price, description, manufacturer, imageUrl,
                                                    category, aidsArray, productQuantity, onSale, manufacturerRebate, descriptionEmbedding);

                    if (success) {
                        productCatalogUtility.addProductToXML(product);
                        productCatalogUtility.addProductToMap(product);

                        // Add product to Elasticsearch
                        pushProductToElasticsearch(jsonObject, descriptionEmbedding);
                    }
                }
            }

            jsonResponse.addProperty("success", success);
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

        // Create JSON payload
        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("input", text);

        // Send request payload
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        // Parse response
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            List<Double> embedding = new ArrayList<>();
            
            // Extract embedding array from response
            response.getAsJsonArray("data")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("embedding")
                    .forEach(e -> embedding.add(e.getAsDouble()));

            return embedding;
        }
    }

    private void pushProductToElasticsearch(JsonObject productJson, List<Double> embedding) throws IOException {
        try (RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build()) {
            Request request = new Request("POST", "/products/_doc");

            JsonObject elasticDocument = new JsonObject();

            // Fetch the product ID either from productJson or database
            int productId = 0; // Default value if not found
            if (productJson.has("id") && !productJson.get("id").isJsonNull()) {
                productId = productJson.get("id").getAsInt();
            } else if (productJson.has("name") && !productJson.get("name").isJsonNull()) {
                String productModelName = productJson.get("name").getAsString();
                Integer fetchedId = MySQLDataStoreUtilities.fetchProductIdByModelName(productModelName);
                productId = fetchedId != null ? fetchedId : 0; // Use the fetched ID or default to 0
            }

            // Populate the Elasticsearch document
            elasticDocument.addProperty("product_id", productId);
            elasticDocument.addProperty("product_name", productJson.has("name") && !productJson.get("name").isJsonNull() ? productJson.get("name").getAsString() : "Unknown");
            elasticDocument.addProperty("price", productJson.has("price") && !productJson.get("price").isJsonNull() ? productJson.get("price").getAsDouble() : 0.0);
            elasticDocument.addProperty("description", productJson.has("description") && !productJson.get("description").isJsonNull() ? productJson.get("description").getAsString() : "No description available");
            elasticDocument.addProperty("manufacturer", productJson.has("manufacturer") && !productJson.get("manufacturer").isJsonNull() ? productJson.get("manufacturer").getAsString() : "Unknown");
            elasticDocument.addProperty("category", productJson.has("category") && !productJson.get("category").isJsonNull() ? productJson.get("category").getAsString() : "Uncategorized");
            elasticDocument.addProperty("imageurl", productJson.has("imageUrl") && !productJson.get("imageUrl").isJsonNull() ? productJson.get("imageUrl").getAsString() : "No image URL");
            elasticDocument.addProperty("product_quantity", productJson.has("productQuantity") && !productJson.get("productQuantity").isJsonNull() ? productJson.get("productQuantity").getAsInt() : 0);
            elasticDocument.addProperty("on_sale", productJson.has("onSale") && !productJson.get("onSale").isJsonNull() ? (productJson.get("onSale").getAsBoolean() ? 1 : 0) : 0);
            elasticDocument.addProperty("manufacturer_rebate", productJson.has("manufacturerRebate") && !productJson.get("manufacturerRebate").isJsonNull() ? (productJson.get("manufacturerRebate").getAsBoolean() ? 1 : 0) : 0);
            elasticDocument.add("product_embedding", JsonParser.parseString(embedding.toString()));

            // Push to Elasticsearch
            request.setJsonEntity(elasticDocument.toString());
            restClient.performRequest(request);
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
