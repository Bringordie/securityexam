package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.UserFacade;
import java.sql.SQLException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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
     */
    @GET
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String getAllPosts() throws SQLException, ClassNotFoundException {
        try {
            return GSON.toJson(FACADE.adminGetPosts());
        } catch (NullPointerException ex) {
            throw new WebApplicationException("No posts was found", 404);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new WebApplicationException("Something unexpectely went wrong", 500);
        }
    }


}
