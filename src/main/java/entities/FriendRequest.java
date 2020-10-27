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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Frederik
 */
@Entity
@NamedQuery(name = "FriendRequest.deleteAllRows", query = "DELETE from FriendRequest")
@Table(name = "request")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "requested_friend")
    private int requestUsernameID;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "picture_url")
    private String pictureUrl;

    public FriendRequest() {
    }

    public FriendRequest(int requestUsernameID, String fullName, String pictureUrl) {
        this.requestUsernameID = requestUsernameID;
        this.fullName = fullName;
        this.pictureUrl = pictureUrl;
    }

    public int getRequestUsername() {
        return requestUsernameID;
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
        return "FriendRequest{" + "id=" + id + ", requestUsernameID=" + requestUsernameID + ", fullName=" + fullName + ", pictureUrl=" + pictureUrl + '}';
    }

}
