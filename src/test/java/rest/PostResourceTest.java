package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.FriendRequest;
import entities.Friends;
import entities.Role;
import entities.User;
import entities.UserPosts;
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

public class PostResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api/";
    private EntityManager em;
    private static UserFacade facade;

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

            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(u4);
            em.getTransaction().commit();

            up1 = new UserPosts("This is a post made by a user");
            up2 = new UserPosts("This is a post made by a admin");

            u1.addUserPost(up1);
            u4.addUserPost(up2);

            f1 = new Friends(u4.getId());
            f2 = new Friends(u1.getId());

            u1.addToFriendList(f1);
            u2.addToFriendList(f2);

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
    public void successfullCreatePostTest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("post", "This is a very good post, please like and share");

        given() //include object in body
                .contentType("application/json")
                .header("x-access-token", token)
                .body(obj)
                .when().post("/post/create").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void failCreatePostTestNoValidToken() {
        //Creating a JSON Object
        JSONObject obj = new JSONObject();
        obj.put("post", "This is a very good post, please like and share");

        given() //include object in body
                .contentType("application/json")
                .header("x-access-token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZSI6InVzZXIiLCJleHAiOjE2MDM2MjU1MzAsImlhdCI6MTYwMzYyMzczMCwiaXNzdWVyIjoic2VtZXN0ZXJzdGFydGNvZGUtZGF0MyIsInVzZXJuYW1lIjoidXNlciJ9.mLrZ_pPX8GPIpBGnGEnG2eSUCh6Pcrz7Eq0uyEDOr2")
                .body(obj)
                .when().post("/post/create").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    public void successGetPostTest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        UserPosts[] result
                = with()
                        .contentType("application/json")
                        .header("x-access-token", token)
                        .when().request("GET", "/post/own").then() //post REQUEST
                        .assertThat()
                        .statusCode(HttpStatus.OK_200.getStatusCode())
                        .extract()
                        .as(UserPosts[].class); //extract result JSON as object

        assertNotNull(result);
        assertEquals(1, result.length);
    }

    @Test
    public void failGetPostTest() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u2.getUserName(), "test");
        String token = getToken.securityToken;

        given() //include object in body
                .contentType("application/json")
                .header("x-access-token", token)
                .when().get("post/own").then() //get REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }

    @Test
    public void getFriendsPostsPass() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u1.getUserName(), "test");
        String token = getToken.securityToken;

        given() //include object in body
                .contentType("application/json")
                .header("x-access-token", token)
                .when().get("/post/friends").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void getFriendsPostsFailNoFriends() {
        LoginEndpointTest getToken = new LoginEndpointTest();
        getToken.login(u3.getUserName(), "test");
        String token = getToken.securityToken;

        given() //include object in body
                .contentType("application/json")
                .header("x-access-token", token)
                .when().get("/post/friends").then() //post REQUEST
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }

}
