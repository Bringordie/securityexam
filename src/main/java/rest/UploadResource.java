//package rest;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import facades.UserFacade;
//import java.io.IOException;
//import java.sql.SQLException;
//import javax.persistence.EntityManagerFactory;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.Produces;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.SecurityContext;
////import org.springframework.util.StringUtils;
////import org.springframework.web.bind.annotation.PostMapping;
////import org.springframework.web.bind.annotation.RequestParam;
////import org.springframework.web.multipart.MultipartFile;
////import org.springframework.web.servlet.view.RedirectView;
//import utils.EMF_Creator;
//
////import javax.servlet.http.Part;
////import javax.ws.rs.core.Response;
////import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
////import org.glassfish.jersey.media.multipart.FormDataParam;
//
////import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
////import org.glassfish.jersey.media.multipart.FormDataParam;
//import org.omg.CORBA.portable.InputStream;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import javax.ws.rs.PathParam;
//
//@Path("upload")
//public class UploadResource {
//
//    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
//    private String FILE_LOCATION = "/Users/Frederik/Documents/upload_security/";
//
//    @Context
//    private UriInfo context;
//
//    @Context
//    SecurityContext securityContext;
//    
//    @POST
//    @Path("/upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response uploadFile(@PathParam("file") InputStream file) throws IOException
//    {
//        String fileName = "test";
//        saveFile(file, fileName);
//
//        return Response.ok().build();
//    }
//    
//    private void saveFile(InputStream is, String fileLocation) throws IOException
//    {
//        String location = FILE_LOCATION + fileLocation;
//        try (OutputStream os = new FileOutputStream(new File(location)))
//        {
//            byte[] buffer = new byte[256];
//            int bytes = 0;
//            while ((bytes = is.read(buffer)) != -1)
//            {
//                os.write(buffer, 0, bytes);
//            }
//        }
//    }
//
//    @GET
//    @Path("/demo")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String createUser() throws SQLException, ClassNotFoundException {
//        return GSON.toJson("Successfully made it to /demo");
//    }
//
//
//
//    // Can't work with Grizzly so "all" tests fail
////    @POST
////    @Path("/save")
////    @Consumes(MediaType.MULTIPART_FORM_DATA)
////    @Produces(MediaType.APPLICATION_JSON)
////    public Response upload(
////            @FormDataParam("file") InputStream uploadedInputStream,
////            @FormDataParam("file") FormDataContentDisposition fileDetail) {
////
////        String uploadedFileLocation = "d://uploaded/" + fileDetail.getFileName();
////
////        // save it
////        writeToFile(uploadedInputStream, uploadedFileLocation);
////
////        String output = "File uploaded to : " + uploadedFileLocation;
////
////        return Response.status(200).entity(output).build();
////
////    }
////
////    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
////        try {
////            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
////            int read = 0;
////            byte[] bytes = new byte[1024];
////            out = new FileOutputStream(new File(uploadedFileLocation));
////            while ((read = uploadedInputStream.read(bytes)) != -1) {
////                out.write(bytes, 0, read);
////            }
////            out.flush();
////            out.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        }
////    //What I get out of requestParam doesn't make fully sense. And if I put another requestParam on it "fails" and I just get 500 errors.
////    @POST
////    @Path("/save")
////    @Produces(MediaType.APPLICATION_JSON)
////    @Consumes(MediaType.MULTIPART_FORM_DATA)
////    public String saveFile(@RequestParam("first_name") String first) throws IOException {
////        String profilePicture;
////        profilePicture = UUID.randomUUID().toString();
////        String first_name = first;
////        //String last_name = last;
////        //String combine = first_name + " " + last_name; 
////        //String profilePicture = UUID.randomUUID().toString();
////         
////        //String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//// 
////        //String uploadDir = "user-photos/";
//// 
////        //FileUploadUtil.saveFile(uploadDir, profilePicture, multipartFile);
////         
////        return GSON.toJson("Successfully made it to /save. \nHello: "+first);
////    }
//}
