package com.example;

import org.bson.Document;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet(urlPatterns = "/submitReview", name = "SubmitReviewServlet")
public class SubmitReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        MongoDBDataStoreUtilities.initMongoDB();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {

        JsonObject jsonObject = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        System.out.println("jsonObject" + jsonObject);
        

        String productModelName = jsonObject.get("productModelName").getAsString();
        String productCategory = jsonObject.get("productCategory").getAsString();
        double productPrice = jsonObject.get("productPrice").getAsDouble();
        String storeId = jsonObject.get("storeId").getAsString();
        String storeZip = jsonObject.get("storeZip").getAsString();
        String storeCity = jsonObject.get("storeCity").getAsString();
        String storeState = jsonObject.get("storeState").getAsString();
        boolean productOnSale = jsonObject.has("productOnSale") && !jsonObject.get("productOnSale").isJsonNull() ? jsonObject.get("productOnSale").getAsBoolean() : false;
        String manufacturerName = jsonObject.get("manufacturerName").getAsString();
        boolean manufacturerRebate = jsonObject.has("manufacturerRebate") && !jsonObject.get("manufacturerRebate").isJsonNull() ? jsonObject.get("manufacturerRebate").getAsBoolean() : false;
        String userId = jsonObject.get("userId").getAsString();
        int userAge = jsonObject.get("userAge").getAsInt();
        String userGender = jsonObject.get("userGender").getAsString();
        String userOccupation = jsonObject.get("userOccupation").getAsString();
        int reviewRating = jsonObject.get("reviewRating").getAsInt();
        String reviewDate = jsonObject.get("reviewDate").getAsString();
        String reviewText = jsonObject.get("reviewText").getAsString();

        Document reviewDocument = new Document("productModelName", productModelName)
                .append("productCategory", productCategory)
                .append("productPrice", productPrice)
                .append("storeId", storeId)
                .append("storeZip", storeZip)
                .append("storeCity", storeCity)
                .append("storeState", storeState)
                .append("productOnSale", productOnSale)
                .append("manufacturerName", manufacturerName)
                .append("manufacturerRebate", manufacturerRebate)
                .append("user", new Document("userId", userId)
                        .append("userAge", userAge)
                        .append("userGender", userGender)
                        .append("userOccupation", userOccupation))
                .append("reviewRating", reviewRating)
                .append("reviewDate", reviewDate)
                .append("reviewText", reviewText);

        System.out.println("reviewDocument" + reviewDocument);
        MongoDBDataStoreUtilities.insertReview(reviewDocument);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        response.getWriter().write(jsonResponse.toString());
        } catch(Exception e) {
            e.printStackTrace();
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
