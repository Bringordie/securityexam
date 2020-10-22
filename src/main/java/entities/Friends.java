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
@Table(name = "friends")
public class Friends implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_friend")
    private String friendUsername;
    //private User friendList = new User();
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id;
    //private User friendList = new User();

    public Friends() {
    }

    public void addFriend(String username) {
        //this.friendList.add(user);
        this.friendUsername=username;
    }

//    public List<User> viewFriends() {
//        return friendList;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public List<User> getFriendList() {
//        return friendList;
//    }

//    public void setFriendList(List<User> friendList) {
//        this.friendList = friendList;
//    }

    @Override
    public String toString() {
        return "Friends{" + "friendList=" + friendUsername + '}';
    }

    
    
    

}
