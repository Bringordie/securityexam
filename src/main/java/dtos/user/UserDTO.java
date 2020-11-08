package dtos.user;

import entities.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frederik Braagaard
 */
public class UserDTO {

    String fullName;
    String profilePicture;
    int userID;
    List<UserPostsDTO> posts = new ArrayList();

    public UserDTO(User user) {
        this.fullName = user.getFullName();
        this.profilePicture = user.getProfilePicture();
        this.userID = user.getId();
    }

    public UserDTO() {
    }
    
    public void addToPostList(UserPostsDTO post) {
        this.posts.add(post);
    }

    public List<UserPostsDTO> getPosts() {
        return posts;
    }
    
    

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "UserDTO{" + "fullName=" + fullName + ", profilePicture=" + profilePicture + ", userID=" + userID + '}';
    }

}
