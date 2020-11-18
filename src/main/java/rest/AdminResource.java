package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JOSEException;
import errorhandling.AuthenticationException;
import facades.UserFacade;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import mongodb.MongoConnection;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import utils.EMF_Creator;

/**
 *
 * @author Frederik Braagaard
 */
@Path("admin")
public class AdminResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final MongoConnection MONGODB = new MongoConnection();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;


    /**
     *
     * @author Frederik Braagaard
     */
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String getAllUsers(String jsonString, @HeaderParam("x-access-token") String accessToken, @HeaderParam("ip_address") String ip_address) throws SQLException, ClassNotFoundException, ParseException, JOSEException, AuthenticationException, IOException {
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }
        String username = userPrin.getName();
        String userIP;
        if (ip_address == null || ip_address == "") {
            userIP = "UNKNOWN";
        } else {
            userIP = ip_address;
        }
        try {
            MONGODB.loggetInsertDocument(MONGODB.loggerDocument("Successfull", userIP, "getAllUsers()", username));
            
            return GSON.toJson(FACADE.adminGetUsers());
        } catch (NullPointerException ex) {
            MONGODB.loggetInsertDocument(MONGODB.loggerDocument("Fail", userIP, "getAllUsers()", username));
            throw new WebApplicationException("No users was found", 404);
        } catch (SQLException | ClassNotFoundException ex) {
            MONGODB.loggetInsertDocument(MONGODB.loggerDocument("Fail", userIP, "getAllUsers()", username));
            throw new WebApplicationException("Something unexpectely went wrong", 500);
        }
    }
    
    /**
     *
     * @author Frederik Braagaard
     */
    @GET
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String getAllPosts(String jsonString, @HeaderParam("x-access-token") String accessToken) throws SQLException, ClassNotFoundException, ParseException, IOException {
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }
        String username = userPrin.getName();
        try {
            return GSON.toJson(FACADE.adminGetPosts());
        } catch (NullPointerException ex) {
            throw new WebApplicationException("No posts was found", 404);
        } catch (SQLException | ClassNotFoundException ex) {
            MONGODB.loggetInsertDocument(MONGODB.loggerDocument("Fail", "TODO", "getAllPosts()", username));
            throw new WebApplicationException("Something unexpectely went wrong", 500);
        }
    }


}
