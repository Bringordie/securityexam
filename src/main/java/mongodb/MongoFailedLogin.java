/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mongodb;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import errorhandling.LoginMaxTriesException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import mongoClass.Mongo;
import static mongodb.MongoConnection.loggingStatus;
import org.bson.Document;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

/**
 *
 * @author Frederik
 */
public class MongoFailedLogin {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDB;
    private static MongoCollection<Mongo> loginCollection;
    public static Boolean loggingStatus = true;
    static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
    private static Gson gson = new Gson();

    //Only needed the first time if the DB has been dropped.
    public MongoClientSettings loggerSetup() throws IOException {
        InputStream inputStream = null;
        MongoClientSettings clientSettings = null;
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
            ConnectionString connectionMongo = new ConnectionString(prop.getProperty("mongouri"));

            String databaseString = prop.getProperty("mongoDB");
            mongoDB = mongoClient.getDatabase(databaseString);

            String collectionString = prop.getProperty("mongoLoginAttemptsCollection");
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry);
            clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionMongo)
                    .codecRegistry(codecRegistry)
                    .build();
            loginCollection = mongoDB.getCollection(collectionString, Mongo.class);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return clientSettings;
    }

    public void newFailedLogin(String ip_address, String username) {
        InputStream inputStream = null;
        Properties prop = new Properties();
        ConnectionString connectionString = null;
        String connectionStringProp;
        try {
            //Properties prop = new Properties();
            String propFileName = "mongo.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            connectionStringProp = prop.getProperty("mongouri");

            connectionString = new ConnectionString(connectionStringProp);
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .codecRegistry(codecRegistry)
                    .build();
            try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
                String mongoDatabase = prop.getProperty("mongoDB");
                MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
                
                String databaseString = prop.getProperty("mongoLoginAttemptsCollection");
                MongoCollection<Mongo> failedLogin = db.getCollection(databaseString, Mongo.class);
                
                Mongo insertDocument = new Mongo();
                insertDocument.setExpireAt(new Date());
                insertDocument.setIp_address(ip_address);
                insertDocument.setTries(1);
                insertDocument.setUsername(username);
                failedLogin.insertOne(insertDocument);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public Mongo findFailedLogin(String ip_address) {
        InputStream inputStream = null;
        Properties prop = new Properties();
        ConnectionString connectionString = null;
        String connectionStringProp;
        Mongo mongo = new Mongo();
        try {
            //Properties prop = new Properties();
            String propFileName = "mongo.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            connectionStringProp = prop.getProperty("mongouri");

            connectionString = new ConnectionString(connectionStringProp);
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .codecRegistry(codecRegistry)
                    .build();
            try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
                String mongoDatabase = prop.getProperty("mongoDB");
                MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
                
                String databaseString = prop.getProperty("mongoLoginAttemptsCollection");
                MongoCollection<Mongo> login = db.getCollection(databaseString, Mongo.class);
                
                mongo = login.find(eq("ip_address", ip_address)).first();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return mongo;
    }

    public Mongo updateLoginAttempts(Mongo mongo) {
        InputStream inputStream = null;
        Properties prop = new Properties();
        ConnectionString connectionString = null;
        String connectionStringProp;
        Mongo user = mongo;
        int tries = mongo.getTries();
        user.setTries(++tries);
        try {
            //Properties prop = new Properties();
            String propFileName = "mongo.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            connectionStringProp = prop.getProperty("mongouri");

            connectionString = new ConnectionString(connectionStringProp);
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .codecRegistry(codecRegistry)
                    .build();
            try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
                String mongoDatabase = prop.getProperty("mongoDB");
                MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
                
                String databaseString = prop.getProperty("mongoLoginAttemptsCollection");
                MongoCollection<Mongo> login = db.getCollection(databaseString, Mongo.class);
                
                Document filterByLoggerId = new Document("_id", user.getId());
                FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
                Mongo updatedLogin = login.findOneAndReplace(filterByLoggerId, user, returnDocAfterReplace);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return mongo;
    }

    public void loginLogger(String ip_address, String username) throws LoginMaxTriesException {
        if (loggingStatus) {
            try {
                Mongo user = findFailedLogin(ip_address);
                if (user.getTries() == 5) {
                    throw new LoginMaxTriesException("User has hit the limit of 5 tries in 10 minutes.");
                }
                updateLoginAttempts(user);
            } catch (NullPointerException ex) {
                newFailedLogin(ip_address, username);
            }
        }
    }

//    public static void main(String[] args) throws InterruptedException, IOException, LoginMaxTriesException {
//        MongoFailedLogin test2 = new MongoFailedLogin();
//        //test2.newFailedLogin("ip_address", "username");
////        Mongo testMongo = test2.findFailedLogin("ip_address");
////        System.out.println(testMongo.getExpireAt());
////        System.out.println(testMongo.getIp_address());
////        System.out.println(testMongo.getUsername());
////        System.out.println(testMongo.getId());
////        test2.updateLoginAttempts(testMongo);
//          test2.loginLogger("ip_address", "username");
//    }
}
