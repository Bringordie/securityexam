package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import errorhandling.AlreadyExistsException;
import facades.UserFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
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
import static org.apache.commons.io.IOUtils.toByteArray;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import utils.EMF_Creator;

/**
 *
 * @author Frederik Braagaard
 */
@Path("admin")
public class AdminResource {

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
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String getAllUsers() throws SQLException, ClassNotFoundException {
        try {
            return GSON.toJson(FACADE.adminGetUsers());
        } catch (NullPointerException ex) {
            throw new WebApplicationException("No users was found", 404);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new WebApplicationException("Something unexpectely went wrong", 500);
        }
    }

    /**
     *
     * @author Frederik Braagaard
     * See LoginEndpoint
     */
//    @PUT
//    @Path("/changepw")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public String changePassword(String jsonString) {
//        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
//        String username = json.get("username").getAsString();
//        String newPassword = json.get("newPassword").getAsString();
//        return GSON.toJson(FACADE.changeUserPW(username, newPassword).getUserName());
//    }

}
