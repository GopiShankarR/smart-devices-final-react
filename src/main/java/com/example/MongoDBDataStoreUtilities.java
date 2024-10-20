package com.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;

public class MongoDBDataStoreUtilities {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String DB_NAME = "enterprise";
    private static final String COLLECTION_NAME = "product_reviews";

    public static void initMongoDB() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase(DB_NAME);
    }

    public static void closeMongoDB() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public static void insertReview(Document reviewDocument) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.insertOne(reviewDocument);
    }

    public static List<Document> getReviewsByProductId(String productId) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        FindIterable<Document> documents = collection.find(Filters.eq("product_id", productId));
        List<Document> reviews = new ArrayList<>();
        for (Document doc : documents) {
            reviews.add(doc);
        }
        return reviews;
    }

    public static Document getReviewByProductAndUser(String productId, String username) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        return collection.find(Filters.and(
            Filters.eq("product_id", productId),
            Filters.eq("reviewer.username", username)
        )).first();
    }

    public static void updateReview(String productId, String username, Document updatedReview) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        collection.updateOne(
            Filters.and(Filters.eq("product_id", productId), Filters.eq("reviewer.username", username)),
            new Document("$set", updatedReview)
        );
    }
}
