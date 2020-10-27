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
    private int friendUsernameID;

    public Friends() {
    }

    public Friends(int friendUsernameID) {
        this.friendUsernameID = friendUsernameID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFriendUsernameID() {
        return friendUsernameID;
    }

    public void addFriend(int friendUsernameID) {
        this.friendUsernameID = friendUsernameID;
    }

    @Override
    public String toString() {
        return "Friends{" + "id=" + id + ", friendUsernameID=" + friendUsernameID + '}';
    }

    

}
