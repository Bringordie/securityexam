package rest;

import entities.FriendRequest;
import entities.Friends;
import entities.User;
import entities.Role;
import entities.UserPosts;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
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
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//@Disabled
public class LoginEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private User u1, u2, u3, u4;
    private Role r1, r2;
    private Friends f1, f2;
    private UserPosts up1, up2;
    private FriendRequest fr1, fr2;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("UserPosts.deleteAllRows").executeUpdate();
            em.createNamedQuery("Friends.deleteAllRows").executeUpdate();
            em.createNamedQuery("FriendRequest.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            r1 = new Role("user");
            r2 = new Role("admin");
            u1 = new User("User user", "user", "test", "where I was born", UUID.randomUUID().toString());
            u1.addRole(r1);
            u2 = new User("User2 user", "user2", "test", "where I went to school", UUID.randomUUID().toString());
            u1.addRole(r1);
            u3 = new User("User3 user", "user3", "test", "where I first traveled to", UUID.randomUUID().toString());
            u1.addRole(r1);
            u4 = new User("Admin admin", "admin", "test", "where I went to school", UUID.randomUUID().toString());
            u4.addRole(r2);

            em.persist(r1);
            em.persist(r2);

            em.getTransaction().commit();
            
            em.getTransaction().begin();
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(u4);

            em.getTransaction().commit();

            up1 = new UserPosts("This is a post made by a user");
            up2 = new UserPosts("This is a post made by a admin");

            u1.addUserPost(up1);
            u4.addUserPost(up2);

            // Out commented these as it's easier to have an overview of the friend tests.
//            f1 = new Friends(u4.getUserName());
//            f2 = new Friends(u1.getUserName());
//
//            u1.addToFriendList(f1);
//            u2.addToFriendList(f2);

            fr1 = new FriendRequest(u2.getId(), u2.getFullName(), u2.getProfilePicture());
            fr2 = new FriendRequest(u1.getId(), u1.getFullName(), u1.getProfilePicture());

            u1.addFriendRequest(fr1);
            u3.addFriendRequest(fr2);

            em.getTransaction().begin();
            em.persist(u1);
            em.persist(u2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    public static String securityToken;

    //Utility method to login and set the returned securityToken
    public static void login(String username, int usernameID, String password) {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("usernameID", usernameID);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void userNotAuthenticatedTest() {
        System.out.println("Testing is server UP");
        JSONObject obj = new JSONObject();
        obj.put("username", "user123");
        obj.put("password", "password");
        obj.put("usernameID", 404);

        given().contentType("application/json")
                .body(obj).when().post("/login")
                .then().statusCode(403);
    }

    @Test
    public void successfullLoginTest() {
        System.out.println("Testing is server UP");
        JSONObject obj = new JSONObject();
        obj.put("username", u1.getUserName());
        obj.put("password", "test");
        obj.put("usernameID", u1.getId());

        given().contentType("application/json")
                .body(obj).when().post("/login")
                .then().assertThat().statusCode(200);
    }

    @Test
    public void testLoginFunctionTest() {
        login(u1.getUserName(), u1.getId(), "test");
        assertNotNull(securityToken != null);
        System.out.println("The token is NOT NULL and it is: " + securityToken);
    }

    @Test
    public void resetPasswordPass() {
        login(u1.getUserName(), u1.getId(), "test");
        
        JSONObject obj = new JSONObject();
        obj.put("token", securityToken);
        obj.put("secret", "where I was born");
        obj.put("newpassword", "new secure password");

        given() //include object in body
                .contentType("application/json")
                .body(obj)
                .when().put("/login/reset/password").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void resetPasswordFail() {
        JSONObject obj = new JSONObject();
        obj.put("token", "not_valid");
        obj.put("username", u1.getUserName());
        obj.put("post", "This is a very good post, please like and share");

        given() //include object in body
                .contentType("application/json")
                .body(obj)
                .when().put("/login/reset/password").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode());
    }

}
