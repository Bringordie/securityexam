package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import entities.User;
import errorhandling.AlreadyExistsException;
import errorhandling.AuthenticationException;
import errorhandling.NotFoundException;
import facades.UserFacade;
import java.text.ParseException;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import utils.EMF_Creator;

@Path("friend")
public class FriendResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String friendRequest(String jsonString) throws NotFoundException, ParseException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String token = json.get("token").getAsString();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(token);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }
        
        String username = userPrin.getName();
        String requestMadeByUsername = json.get("request_username").getAsString();
        User user;
        try {
            user = FACADE.addFriendRequest(username, requestMadeByUsername);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend could not be found", 404);
        }
        
        return GSON.toJson("Friend request has been sent");
    }
    
    @POST
    @Path("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String acceptFriendRequest(String jsonString) throws NotFoundException, ParseException, AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String token = json.get("token").getAsString();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(token);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }
        
        String username = userPrin.getName();
        String request_username = json.get("request_username").getAsString();
        User user;
        try {
            user = FACADE.acceptFriendRequest(username, request_username);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend could not be found", 404);
        } catch (AuthenticationException ex) {
            throw new WebApplicationException("Something unexpected went wrong. This request has been logged for further investigation", 400);
        }
        
        return GSON.toJson("Friend request has been accepted");
    }

}
