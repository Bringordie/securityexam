package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import errorhandling.AlreadyExistsException;
import facades.UserFacade;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import static org.apache.commons.io.IOUtils.toByteArray;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import utils.EMF_Creator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Frederik Braagaard
 */
@Path("register")
public class RegistrationResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final String PASSWORD_PATTERN
            = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

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
        Boolean isValid = isValid(userPass);
        if (!isValid) {
            throw new WebApplicationException("Password does not accord with the password policies.", 422);
        }

        String profilePicture = UUID.randomUUID().toString();

        //Checking mimetype
        String mimeType = body.getMediaType().toString();
        if (!mimeType.equals("image/png")) {
            throw new WebApplicationException("Only .png pictures are allowed to be uploaded.", 415);
        }

//        InputStream sizeChecker = uploadedInputStream;
        byte[] size = toByteArray(uploadedInputStream);
        int maxSize = 1048576 * 5; //5 MB
        if (size.length > maxSize) {
            throw new WebApplicationException("Uploaded file is too big.", 413);
        }

        try {
            User user = FACADE.createNormalUser(fullName, userName, userPass, secretAnswer, profilePicture);
            String uploadedFileLocation = "d://uploaded/" + profilePicture + ".png";
            writeToFile(size, uploadedFileLocation);
            return GSON.toJson(user);
        } catch (AlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(), 400);
        }
    }

//    /**
//     *
//     * @author Frederik Braagaard
//     */
//    @POST
//    @Path("/test")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public String test(String jsonString) throws SQLException, ClassNotFoundException, IOException {
//        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
//        String username = json.get("username").getAsString();
//        String password = json.get("password").getAsString();
//        String secret = json.get("secret").getAsString();
//
//        try {
//            User user = FACADE.createNormalUser("fullName", username, password, secret, "profilePicture");
//            return GSON.toJson(user);
//        } catch (AlreadyExistsException ex) {
//            throw new WebApplicationException(ex.getMessage(), 400);
//        }
//    }

    private void writeToFile(byte[] uploadedInputStream, String uploadedFileLocation) throws FileNotFoundException, IOException {
        //int size = toByteArray(uploadedInputStream).length;
        try (FileOutputStream outputStream = new FileOutputStream(uploadedFileLocation)) {

            int read;
            byte[] bytes = new byte[1024];
            FileUtils.writeByteArrayToFile(new File(uploadedFileLocation), uploadedInputStream);

        }
    }

//    /**
//     *
//     * @author Frederik Braagaard
//     */
//    @POST
//    @Path("/admin/{fullName}/{userName}/{userPass}/{secretAnswer}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed("admin")
//    public String createAdmin(@PathParam("fullName") String fullName, @PathParam("userName") String userName, @PathParam("userPass") String userPass, @PathParam("secretAnswer") String secretAnswer) {
//        String profilePicture = UUID.randomUUID().toString();
//        // ## TO DO ADD/RENAME THE PICTURE ALSO
//        try {
//            return GSON.toJson(FACADE.adminCreateUser(fullName, userName, userPass, secretAnswer, profilePicture));
//        } catch (AlreadyExistsException ex) {
//            throw new WebApplicationException(ex.getMessage(), 400);
//        }
//    }
    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
