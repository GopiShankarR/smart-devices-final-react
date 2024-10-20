package com.example;

import com.example.Product;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonArray;

@WebServlet(urlPatterns = "/autocomplete", name = "AutoCompleteServlet")
public class AutoCompleteServlet extends HttpServlet {

    private ProductCatalogUtility productCatalogUtility;

    @Override
    public void init() throws ServletException {
        super.init();
        // Retrieve the shared ProductCatalogUtility instance
        productCatalogUtility = (ProductCatalogUtility) getServletContext().getAttribute("productCatalogUtility");
        if (productCatalogUtility == null) {
            String xmlFilePath = getServletContext().getInitParameter("ProductCatalogXMLPath");
            productCatalogUtility = new ProductCatalogUtility(xmlFilePath);
            getServletContext().setAttribute("productCatalogUtility", productCatalogUtility);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("query");

        if (query != null && query.length() >= 2) {
            List<String> suggestions = getSuggestions(query.toLowerCase());

            JsonArray jsonArray = new JsonArray();
            for (String suggestion : suggestions) {
                jsonArray.add(suggestion);
            }

            response.getWriter().write(jsonArray.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

    private List<String> getSuggestions(String query) {
        HashMap<String, Product> productMap = productCatalogUtility.getProductMap();
        List<String> suggestions = new ArrayList<>();

        for (Product product : productMap.values()) {
            String productName = product.getName();
            if (productName.toLowerCase().contains(query)) { 
                suggestions.add(productName); 
            }
        }

        return suggestions;
    }
}
