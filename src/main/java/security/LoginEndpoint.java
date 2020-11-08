package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entities.Role;
import facades.UserFacade;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import java.sql.SQLException;
import java.text.ParseException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import utils.EMF_Creator;

/**
 *
 * @author Frederik Braagaard
 */
@Path("login")
public class LoginEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(String jsonString) throws AuthenticationException, SQLException, ClassNotFoundException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();
        int usernameID;

        try {
            User user = USER_FACADE.getVeryfiedUser(username, password);
            usernameID = user.getId();
            String token = createToken(username, usernameID, user.getRole());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (JOSEException | AuthenticationException ex) {
            throw new WebApplicationException("Invalid username or secret! Please try again", 401);
            //Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @PUT
    @Path("/reset/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String resetPassword(String jsonString) throws AuthenticationException, ParseException, SQLException, ClassNotFoundException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        JWTAuthenticationFilter authenticate = new JWTAuthenticationFilter();
        String username = json.get("username").getAsString();
        String secret = json.get("secret").getAsString();
        String newpassword = json.get("newpassword").getAsString();
        User user;
        try {
            user = USER_FACADE.userResetPassword(username, secret, newpassword);
        } catch (AuthenticationException | SQLException ex) {
            throw new WebApplicationException("Invalid username or secret! Please try again", 401);
            //Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return GSON.toJson("Password has been resat for user.");
    }

    /**
     *
     * @author Frederik Braagaard
     */
    private String createToken(String userName, int userNameID, Role role) throws JOSEException {

        String issuer = "semesterstartcode-dat3";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userName)
                .claim("username", userName)
                .claim("role", role.getRoleName())
                .claim("usernameID", userNameID)
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        // ERROR HERE
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @POST
    @Path("/admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginAdmin(String jsonString) throws AuthenticationException, SQLException, ClassNotFoundException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        //Uncomment and fix tests ones this has been decided.
//        String ipaddress = json.get("ipaddress").getAsString();
//        if (!ipaddress.equals("127.0.0.1")) {
//            throw new WebApplicationException("Forbidden. Request made to login outside workplace.", 403);
//        }
        int usernameID;

        try {
            User user = USER_FACADE.getVeryfiedAdmin(username, password);
            usernameID = user.getId();
            String token = createToken(username, usernameID, user.getRole());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw new WebApplicationException("Forbidden. Request made to login outside workplace.", 401);
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Invalid username or password! Please try again");
    }

}
