package dtos.user;

import entities.*;
import java.util.Date;

/**
 *
 * @author Frederik Braagaard
 */
public class UserPostsDTO {

    private String message;
    private Date postDate;

    public UserPostsDTO(UserPosts post) {
        this.message = post.getMessage();
        this.postDate = post.getPostDate();
    }

    public UserPostsDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

}
