package com.example;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet(urlPatterns = "/product", name = "ProductServlet")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductCatalogUtility productCatalogUtility;

    @Override
    public void init() throws ServletException {
        super.init();
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
                // Search by product name
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

            if ("delete".equals(action)) {
                String idToDelete = jsonObject.get("id").getAsString();
                success = dbUtil.deleteProduct(idToDelete);

                if (success) {
                    int productId = Integer.parseInt(idToDelete);
                    productCatalogUtility.deleteProductFromXML(productId);
                    productCatalogUtility.deleteProductFromMapById(productId);
                }

            } else {
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

                if ("add".equals(action) && id == 0) {
                    id = productCatalogUtility.generateNewProductId();
                }

                Product product = new Product(id, name, price, description, manufacturer, imageUrl, category, productQuantity, onSale, manufacturerRebate);

                if ("update".equals(action)) {
                    success = dbUtil.updateProduct(String.valueOf(id), name, price, description, manufacturer, imageUrl, category, productQuantity, onSale, manufacturerRebate);
                    if (success) {
                        // Update XML and HashMap
                        productCatalogUtility.updateProductInXML(product);
                        productCatalogUtility.updateProductInMap(product);
                    }
                } else {
                    success = dbUtil.addProductWithId(String.valueOf(id), name, price, description, manufacturer, imageUrl, category, aidsArray, productQuantity, onSale, manufacturerRebate);
                    if (success) {
                        // Add to XML and HashMap
                        productCatalogUtility.addProductToXML(product);
                        productCatalogUtility.addProductToMap(product);
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

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
