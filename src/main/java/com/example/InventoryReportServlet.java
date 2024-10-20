package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/inventory-report", name = "InventoryReportServlet")
public class InventoryReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch inventory data using MySQLDataStoreUtilities
            List<ProductInventory> productInventories = MySQLDataStoreUtilities.getAllProductsInventory();
            List<ProductInventory> productsOnSale = MySQLDataStoreUtilities.getProductsOnSale();
            List<ProductInventory> productsWithRebates = MySQLDataStoreUtilities.getProductsWithManufacturerRebates();

            // Prepare JSON response
            Gson gson = new Gson();
            InventoryReport report = new InventoryReport(productInventories, productsOnSale, productsWithRebates);
            String jsonResponse = gson.toJson(report);

            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
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

    // Inventory report object to hold different lists
    class InventoryReport {
        List<ProductInventory> productInventories;
        List<ProductInventory> productsOnSale;
        List<ProductInventory> productsWithRebates;

        InventoryReport(List<ProductInventory> productInventories, List<ProductInventory> productsOnSale, List<ProductInventory> productsWithRebates) {
            this.productInventories = productInventories;
            this.productsOnSale = productsOnSale;
            this.productsWithRebates = productsWithRebates;
        }
    }
}
