package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import entities.User;
import errorhandling.AlreadyExistsException;
import errorhandling.AuthenticationException;
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

        String username = userPrin.getName();
        String newPost = json.get("post").getAsString();

        Boolean response = FACADE.createPost(username, newPost);
        if (!response) {
            throw new WebApplicationException("Something unexpected happened. Please try again later", 400);
        }

        return GSON.toJson("Post has successfully been created");
    }


}
