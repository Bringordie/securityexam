package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author Frederik
 */
@Entity
@Table(name = "request")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "requested_friend")
    private String requestUsername;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "picture_url")
    private String pictureUrl;

    public FriendRequest() {
    }

    public FriendRequest(String requestUsername, String fullName, String pictureUrl) {
        this.requestUsername = requestUsername;
        this.fullName = fullName;
        this.pictureUrl = pictureUrl;
    }

    public String getRequestUsername() {
        return requestUsername;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FriendRequest{" + "id=" + id + ", requestUsername=" + requestUsername + ", fullName=" + fullName + ", pictureUrl=" + pictureUrl + '}';
    }

}
