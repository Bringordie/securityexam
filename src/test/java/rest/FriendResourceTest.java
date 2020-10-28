package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.user.UserDTO;
import entities.FriendRequest;
import entities.Friends;
import entities.Role;
import entities.User;
import entities.UserPosts;
import errorhandling.NotFoundException;
import facades.UserFacade;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static rest.LoginEndpointTest.login;
import static rest.LoginEndpointTest.securityToken;
import static rest.LoginEndpointTest.startServer;
import utils.EMF_Creator;

public class FriendResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api/";
    private EntityManager em;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static UserFacade facade;

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
        facade = UserFacade.getUserFacade(emf);
        facade.serverStatus = false;
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

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
            u2.addRole(r1);
            u3 = new User("User3 user", "user3", "test", "where I first traveled to", UUID.randomUUID().toString());
            u3.addRole(r1);
            u4 = new User("Admin admin", "admin", "test", "where I went to school", UUID.randomUUID().toString());
            u4.addRole(r2);


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

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @Test
    public void successMakeFriendRequest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("request_username", u2.getId());

        String response = with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/add").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .as(String.class); //extract result JSON as object

        assertNotNull(response);
        assertEquals("Friend request has been sent", response);
    }

    @Test
    public void failMakeFriendRequest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("request_username", 404);
        obj.put("usernameID",404);

        with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/add").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());

    }

    @Test
    public void successAcceptFriendRequest() throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u4.getUserName(), "test");
        String token = getToken.securityToken;

        //assertEquals(0, u4.getFriendList().size());
        User user = facade.addFriendRequest(u4.getId(), u2.getId());

        assertEquals(1, user.getFriendRequests().size());

        //Creating a JSON Object
        JSONObject json = new JSONObject();
        json.put("request_usernameID", u2.getId());
        json.put("token", token);

        String response = with()
                .contentType("application/json")
                .body(json)
                .when().request("POST", "/friend/accept").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .as(String.class); //extract result JSON as object

        assertNotNull(response);
        assertEquals("Friend request has been accepted", response);
        User friendRequestLength = em.find(User.class, u4.getId());
        assertEquals(0, friendRequestLength.getFriendRequests().size());
        assertEquals(1, friendRequestLength.getFriendList().size());
        em.close();
    }

    @Test
    public void failAcceptFriendRequest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("request_usernameID", 404);

        with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/accept").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());

    }
    
    @Test
    public void hardFailAcceptFriendRequest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("request_usernameID", u4.getId());

        with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/accept").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());

    }
    
    @Test
    public void successRemoveFriendRequest() throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u4.getUserName(), "test");
        String token = getToken.securityToken;

        User user = facade.addFriendRequest(u4.getId(), u2.getId());

        assertEquals(1, user.getFriendRequests().size());

        //Creating a JSON Object
        JSONObject json = new JSONObject();
        json.put("request_username", u2.getId());
        json.put("token", token);

        String response = with()
                .contentType("application/json")
                .body(json)
                .when().request("POST", "/friend/remove/friendrequest").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .as(String.class); //extract result JSON as object

        assertNotNull(response);
        assertEquals("Friend Request has been removed", response);
        User friendRequestLength = em.find(User.class, u4.getId());
        assertEquals(0, friendRequestLength.getFriendRequests().size());
        em.close();
    }

    @Test
    public void failRemoveFriendRequest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("request_username", 404);

        with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/remove/friendrequest").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());

    }
    
    @Test
    public void successFriendSearch() throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject json = new JSONObject();
        json.put("search_name", "admin");
        json.put("token", token);

        UserDTO[] response = with()
                .contentType("application/json")
                .body(json)
                .when().request("POST", "/friend/search").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .as(UserDTO[].class); //extract result JSON as object

        assertNotNull(response);
        assertEquals(u4.getFullName(), response[0].getFullName());
        assertEquals(1, response.length);
    }

    @Test
    public void failFriendSearch() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("search_name", 404);

        with()
                .contentType("application/json")
                .body(obj)
                .when().request("POST", "/friend/search").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());

    }

}
