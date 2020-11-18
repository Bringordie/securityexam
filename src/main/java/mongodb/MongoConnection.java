/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Frederik
 */
public class MongoConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase dbLogger;
    private static MongoCollection<Document> logCollection;

//    public static void main(String[] args) throws IOException {
//        MongoConnection setup = new MongoConnection();
//        
//        //Document logCollection = new Document("type", "exam").append("status", "successfull");
//        setup.loggetInsertDocument(setup.loggerDocument("test", "test2", "test3", "test4"));
//        //loggetInsertDocument(logCollection);
////        MongoConnection test = new MongoConnection();
////        test.loggetSetup();
//    }

    public void loggetSetup() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "mongo.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String connectionString = prop.getProperty("mongouri");
            mongoClient = MongoClients.create(connectionString);
            
            String databaseString = prop.getProperty("mongoLoggerDB");
            dbLogger = mongoClient.getDatabase(databaseString);
            
            String collectionString = prop.getProperty("mongoLoggerCollection");
            logCollection = dbLogger.getCollection(collectionString);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public void loggetInsertDocument(Document loggerAddToCollection) throws IOException {
        MongoConnection setup = new MongoConnection();
        setup.loggetSetup();
        logCollection.insertOne(loggerAddToCollection);
    }

    public Document loggerDocument(String status, String ip_address, String method, String user) {
        Document logCollection = new Document("status", status).append("IP", ip_address).append("method", method).append("userID", user);
        return logCollection;
    }

}
