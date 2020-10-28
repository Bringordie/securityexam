package dtos.user;

import entities.User;

public class UserDTO {

    String fullName;
    String profilePicture;
    int userID;

    public UserDTO(User user) {
        this.fullName = user.getFullName();
        this.profilePicture = user.getProfilePicture();
        this.userID = user.getId();

    }

    public UserDTO() {
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
