import com.example.Product;
import com.example.MySQLDataStoreUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AjaxUtility {
    
    private HashMap<String, Product> productMap;

    public AjaxUtility(HashMap<String, Product> productMap) {
        this.productMap = productMap;
    }

    public List<String> getProductSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();

        for (String productName : productMap.keySet()) {
            if (productName.toLowerCase().startsWith(query.toLowerCase())) {
                suggestions.add(productName);
            }
        }
        return suggestions;
    }

    public void addProduct(Product product) throws SQLException {
        productMap.put(product.getName(), product);

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String sql = "INSERT INTO products (id, name, price, description, manufacturer, imageUrl, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, product.getId());
                stmt.setString(2, product.getName());
                stmt.setDouble(3, product.getPrice());
                stmt.setString(4, product.getDescription());
                stmt.setString(5, product.getManufacturer());
                stmt.setString(6, product.getImageUrl());
                stmt.setString(7, product.getCategory());
                stmt.executeUpdate();
            }
        }
    }

    public void updateProduct(Product product) throws SQLException {
        productMap.put(product.getName(), product);

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String sql = "UPDATE products SET price = ?, description = ?, manufacturer = ?, imageUrl = ?, category = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, product.getPrice());
                stmt.setString(2, product.getDescription());
                stmt.setString(3, product.getManufacturer());
                stmt.setString(4, product.getImageUrl());
                stmt.setString(5, product.getCategory());
                stmt.setInt(6, product.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void deleteProduct(String productName) throws SQLException {
        Product product = productMap.get(productName);
        if (product != null) {
            productMap.remove(productName);

            try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
                String sql = "DELETE FROM products WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, product.getId());
                    stmt.executeUpdate();
                }
            }
        }
    }
}
