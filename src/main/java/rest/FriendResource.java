package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import dtos.user.FriendsDTO;
import dtos.user.UserDTO;
import entities.User;
import errorhandling.AuthenticationException;
import errorhandling.NoFriendRequestsException;
import errorhandling.NoFriendsException;
import errorhandling.NotFoundException;
import facades.UserFacade;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import utils.EMF_Creator;

/**
 *
 * @author Frederik Braagaard
 */
@Path("friend")
public class FriendResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String friendRequest(String jsonString, @HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        int requestMadeByUsernameID = json.get("request_username").getAsInt();
        User user;
        try {
            user = FACADE.addFriendRequest(usernameID, requestMadeByUsernameID);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend could not be found", 404);
        }

        return GSON.toJson("Friend request has been sent");
    }
    
    /**
     *
     * @author Frederik Braagaard
     */
    @GET
    @Path("/friends")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getFriends(@HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException, NoFriendsException {
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        List<FriendsDTO> friends;
        try {
            friends = FACADE.viewFriends(usernameID);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("Something unexpected went wrong", 500);
        } catch (NoFriendsException ex) {
            throw new WebApplicationException("The requester currently has no friends", 404);
        }

        return GSON.toJson(friends);
    }
    
    /**
     *
     * @author Frederik Braagaard
     */
    @GET
    @Path("/requests")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getFriendsRequests(@HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException, NoFriendRequestsException {
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        List<FriendsDTO> friends;
        try {
            friends = FACADE.viewFriendRequests(usernameID);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("Something unexpected went wrong", 500);
        } catch (NoFriendRequestsException ex) {
            throw new WebApplicationException("You do not have any friend requests", 404);
        }

        return GSON.toJson(friends);
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String acceptFriendRequest(String jsonString, @HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException, AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        int request_usernameID = json.get("request_userid").getAsInt();
        User user;
        try {
            user = FACADE.acceptFriendRequest(usernameID, request_usernameID);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend could not be found", 404);
        } catch (AuthenticationException ex) {
            throw new WebApplicationException("Something unexpected went wrong. This request has been logged for further investigation", 400);
        }

        return GSON.toJson("Friend request has been accepted");
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String removeFriend(String jsonString, @HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        int request_usernameID = json.get("request_userid").getAsInt();
        User user;
        try {
            user = FACADE.removeFriend(usernameID, request_usernameID);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend could not be found", 404);
        }

        return GSON.toJson("Friend has been removed");
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/remove/friendrequest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String removeFriendRequest(String jsonString, @HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int username = userPrin.getNameID();
        int request_username = json.get("request_userid").getAsInt();
        User user;
        try {
            user = FACADE.removeFriendRequest(username, request_username);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("The requested friend request could not be found", 404);
        }

        return GSON.toJson("Friend Request has been removed");
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String friendSearch(String jsonString, @HeaderParam("x-access-token") String accessToken) throws NotFoundException, ParseException, SQLException, ClassNotFoundException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(accessToken);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        String searchName = json.get("search_name").getAsString();
        List<UserDTO> dtoList;
        try {
            dtoList = FACADE.friendSearch(searchName);
        } catch (NotFoundException ex) {
            throw new WebApplicationException("No users could be found by this search", 404);
        }

        return GSON.toJson(dtoList);
    }

}
