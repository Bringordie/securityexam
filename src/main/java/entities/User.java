package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User")
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_name", length = 25)
    private String userName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_pass")
    private String userPass;
    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToOne
    private Role role;
    @ManyToMany (cascade = {CascadeType.PERSIST})
    private List<Friends> friendList = new ArrayList();
    @ManyToMany (cascade = {CascadeType.PERSIST})
    private List<UserPosts> userPosts = new ArrayList();


    public User() {
    }

    public void addUserPost(UserPosts userPost) {
        this.userPosts.add(userPost);
    }

    public List<UserPosts> getUserPosts() {
        return userPosts;
    }

    public void addToFriendList(Friends user) {
        this.friendList.add(user);
    }

    public void removeFromFriendList(Friends user) {
        this.friendList.remove(user);
    }

    public List<Friends> getFriendList() {
        return friendList;
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.userPass);
    }

    public User(String userName, String userPass) {
        this.userName = userName;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }


    public void setRoleList(Role role) {
        this.role = role;
    }
    
    public Role getRole() {
        return role;
    }

    public void addRole(Role userRole) {
        this.role = userRole;
    }



}
