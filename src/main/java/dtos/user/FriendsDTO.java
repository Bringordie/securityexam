package dtos.user;

import entities.Friends;
import entities.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frederik Braagaard
 */
public class FriendsDTO {

    int friendID;
    String fullName;
    String picture;

    public FriendsDTO(User user) {
        this.friendID = user.getId();
        this.fullName = user.getFullName();
        this.picture = user.getProfilePicture();
    }

    public FriendsDTO() {
    }

    public int getFriendID() {
        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "FriendsDTO{" + "friendID=" + friendID + ", fullName=" + fullName + ", picture=" + picture + '}';
    }

}
