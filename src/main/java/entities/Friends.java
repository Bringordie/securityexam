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
@NamedQuery(name = "Friends.deleteAllRows", query = "DELETE from Friends")
@Table(name = "friends")
public class Friends implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_friend")
    private String friendUsername;

    public Friends() {
    }

    public Friends(String friendUsername) {
        this.friendUsername = friendUsername;
    }
    
    

    public void addFriend(String username) {
        this.friendUsername = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Friends{" + "friendList=" + friendUsername + '}';
    }

}
