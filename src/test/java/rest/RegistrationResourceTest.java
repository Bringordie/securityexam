package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Role;
import entities.User;
import facades.UserFacade;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.with;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minidev.json.JSONObject;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class RegistrationResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api/";
    private EntityManager em;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static User u1, u2;
    private static Role r1, r2;
    private static UserFacade facade;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);
        facade.serverStatus = false;

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
        

        //Create 2 dummy users
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            u1 = new User("UserFirstName userLastName", "user1", "password", "Very secret user recovery password");
            u2 = new User("AdminFirstName AdminLastName", "admin", "password", "Very secret admin recovery password");

            r1 = new Role("user");
            r2 = new Role("admin");

            u1.addRole(r1);
            u2.addRole(r2);

            em.persist(u1);
            em.persist(u2);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @Test
    public void testUsernameAlreadyExists2() {
        JSONObject obj = new JSONObject();
        obj.put("fullname", "full name");
        obj.put("username", u1.getUserName());
        obj.put("password", "password");
        obj.put("secretanswer", "secretanswer");

        with().body(obj) //include object in body
                .contentType("application/json")
                .when().request("POST", "/register/user").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }

}
