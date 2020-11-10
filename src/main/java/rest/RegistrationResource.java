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
@Path("register")
public class RegistrationResource {

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
    @Path("/user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String createUser(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body, @FormDataParam("fullname") String fullName, @FormDataParam("username") String userName, @FormDataParam("password") String userPass, @FormDataParam("secret") String secretAnswer) throws SQLException, ClassNotFoundException, IOException {
        String profilePicture = UUID.randomUUID().toString();

        //Checking mimetype
        String mimeType = body.getMediaType().toString();
        if (!mimeType.equals("image/png")) {
            throw new WebApplicationException("Only .png pictures are allowed to be uploaded.", 415);
        }

        int size = toByteArray(uploadedInputStream).length;
        int maxSize = 1048576 * 5; //5 MB
        if (size > maxSize) {
            throw new WebApplicationException("Uploaded file is too big.", 413);
        }

        try {
            User user = FACADE.createNormalUser(fullName, userName, userPass, secretAnswer, profilePicture);
            String uploadedFileLocation = "d://uploaded/" + profilePicture + ".png";
            writeToFile(uploadedInputStream, uploadedFileLocation);
            return GSON.toJson(user);
        } catch (AlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(), 400);
        }
    }

    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @author Frederik Braagaard
     */
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
