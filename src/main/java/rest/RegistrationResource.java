package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import errorhandling.AlreadyExistsException;
import facades.UserFacade;
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
import utils.EMF_Creator;



@Path("register")
public class RegistrationResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;
    
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createUser(String jsonString) {
        String profilePicture = UUID.randomUUID().toString();
        // ## TO DO ADD/RENAME THE PICTURE ALSO
        
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String fullName = json.get("fullname").getAsString();
        String userName = json.get("username").getAsString();
        String userPass = json.get("password").getAsString();
        String secretAnswer = json.get("secretanswer").getAsString();
        
        try {
            return GSON.toJson(FACADE.createNormalUser(fullName, userName, userPass, secretAnswer, profilePicture));
        } catch (AlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(), 400);
        }
    }

    @POST
    @Path("/admin/{fullName}/{userName}/{userPass}/{secretAnswer}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String createAdmin(@PathParam("fullName") String fullName, @PathParam("userName") String userName, @PathParam("userPass") String userPass, @PathParam("secretAnswer") String secretAnswer) {
        String profilePicture = UUID.randomUUID().toString();
        // ## TO DO ADD/RENAME THE PICTURE ALSO
        try {
            return GSON.toJson(FACADE.adminCreateUser(fullName, userName, userPass, secretAnswer, profilePicture));
        } catch (AlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(), 400);
        }
    }
    

    @PUT
    @Path("/changepw")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String changePassword(String jsonString) {
    JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
    String username = json.get("username").getAsString();
    String newPassword = json.get("newPassword").getAsString();
    return GSON.toJson(FACADE.changeUserPW(username, newPassword).getUserName());
    }
        
}
