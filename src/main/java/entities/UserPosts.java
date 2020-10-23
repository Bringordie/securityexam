package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Frederik
 */
@Entity
@Table(name = "posts")
public class UserPosts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_post")
    private String message;
    @Column(name = "post_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date postDate;

    public UserPosts(String message) {
        this.message = message;
        postDate = new Date();
    }

    public UserPosts() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserPosts{" + "id=" + id + ", message=" + message + ", postDate=" + postDate + '}';
    }
    
    

}
