package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import dtos.user.UserDTO;
import entities.User;
import entities.UserPosts;
import errorhandling.AlreadyExistsException;
import errorhandling.AuthenticationException;
import errorhandling.NoFriendsException;
import errorhandling.NotFoundException;
import facades.UserFacade;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

@Path("post")
public class PostResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @POST
    @Path("/own")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getPosts(String jsonString) throws ParseException, JOSEException, AuthenticationException, NotFoundException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String token = json.get("token").getAsString();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(token);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int username = userPrin.getNameID();
        List<UserPosts> response;
        response = FACADE.getPosts(username);
        if (response.isEmpty()) {
            throw new WebApplicationException("This user has no posts", 404);
        }

        return GSON.toJson(response);
    }
    
    @POST
    @Path("/friends")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getFriendsPosts(String jsonString) throws ParseException, JOSEException, AuthenticationException, NotFoundException, NoFriendsException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String token = json.get("token").getAsString();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(token);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        List<UserDTO> response;
        try {
        response = FACADE.friendPosts(usernameID);
        } catch (NoFriendsException ex) {
            throw new WebApplicationException("This user currently has no friends in their friendlist.", 404);
        }

        return GSON.toJson(response);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createPost(String jsonString) throws ParseException, JOSEException, AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String token = json.get("token").getAsString();
        UserPrincipal userPrin;
        try {
            userPrin = authenticate.getUserPrincipalFromTokenIfValid(token);
        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException(ex.getMessage(), 401);
        }

        int usernameID = userPrin.getNameID();
        String newPost = json.get("post").getAsString();

        Boolean response = FACADE.createPost(usernameID, newPost);
        if (!response) {
            throw new WebApplicationException("Something unexpected happened. Please try again later", 400);
        }

        return GSON.toJson("Post has successfully been created");
    }

}
