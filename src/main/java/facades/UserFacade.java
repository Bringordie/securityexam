package facades;

import dtos.user.UserDTO;
import entities.FriendRequest;
import entities.Friends;
import entities.Role;
import entities.User;
import entities.UserPosts;
import errorhandling.AlreadyExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import errorhandling.NotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    public static Boolean serverStatus = true;
    private static Connection connection;

    private UserFacade() {
    }

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        DbSelector connectionStatus;
        if (serverStatus == true) {
            connectionStatus = EMF_Creator.DbSelector.DEV;
        } else {
            connectionStatus = EMF_Creator.DbSelector.TEST;
        }
        connection = EMF_Creator.getConnection(connectionStatus, EMF_Creator.Strategy.CREATE);
        return connection;
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    /**
     * This method is used to check if a user with the given password exists in
     * the DB.
     *
     * @param username
     * @param password
     * @return User The verified user.
     * @throws AuthenticationException
     */
    public User getVeryfiedUser(String username, String password) throws AuthenticationException, SQLException, ClassNotFoundException {
        EntityManager em = emf.createEntityManager();
        User user = new User();
        String query = "SELECT * FROM users WHERE user_name = ?";
        try {
            PreparedStatement ps = createConnection().prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setId(rs.getInt("user_id"));
            }
            rs.close();
            ps.close();
            user = em.find(User.class, user.getId());
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User userResetPassword(int usernameID, String secret, String newPassword) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, usernameID);
            Boolean verifySecretPassword = user.verifySecretAnswer(secret);
            if (user == null || !verifySecretPassword) {
                throw new AuthenticationException("Invalid user name or secret");
            }
            user.setUserPass(newPassword);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (AuthenticationException | NullPointerException ex) {
            throw new AuthenticationException("Invalid user name or secret");
        } finally {
            em.close();
        }
        return user;
    }

    public User createNormalUser(String fullName, String userName, String userPass, String secretAnswer, String profilePicture) throws AlreadyExistsException, SQLException, ClassNotFoundException {
        EntityManager em = emf.createEntityManager();
        User userregister = new User(fullName, userName, userPass, secretAnswer, profilePicture);
        User checker = new User();
        Role userRole = new Role("user");
        userregister.addRole(userRole);
        String query = "SELECT user_name FROM users WHERE user_name = ?";
        try {
            PreparedStatement ps = createConnection().prepareStatement(query);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO dto = new UserDTO();
                checker.setUserName(rs.getString("user_name"));
            }
            rs.close();
            ps.close();
            //User user = em.find(User.class, userName);
            if (checker != null) {
                throw new AlreadyExistsException("User name already exists");
            }
            em.getTransaction().begin();
            em.persist(userregister);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return userregister;
    }

    //WILL NOT WORK YET
    public User adminCreateUser(String fullName, String userName, String userPass, String secretAnswer, String profilePicture) throws AlreadyExistsException {
        EntityManager em = emf.createEntityManager();
        User userregister = new User(fullName, userName, userPass, secretAnswer, profilePicture);
        Role userRole = new Role("admin");
        userregister.addRole(userRole);
        try {
            User user = em.find(User.class, userName);
            if (user != null) {
                throw new AlreadyExistsException("User name already exists");
            }
            em.getTransaction().begin();
            em.persist(userregister);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return userregister;
    }

    /**
     * This method is used to change a users password.
     *
     * @param username
     * @param newPassword
     * @return User This returns the User that had his pw changed.
     */
    public User changeUserPW(String username, String newPassword) {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, username);
            user.setUserPass(newPassword);
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }

    public boolean createPost(int usernameID, String userPost) {
        EntityManager em = emf.createEntityManager();
        User user;
        UserPosts post = new UserPosts(userPost);
        try {
            em.getTransaction().begin();
            user = em.find(User.class, usernameID);
            if (user == null) {
                return false;
            }
            user.addUserPost(post);
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return true;
    }

    public List<UserPosts> getPosts(int usernameID) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user;
        List<UserPosts> post = new ArrayList();
        try {
            em.getTransaction().begin();
            user = em.find(User.class, usernameID);
            post = user.getUserPosts();
        } catch (NullPointerException ex) {
            throw new NotFoundException("User name could not be found");
        } finally {
            em.close();
        }
        return post;
    }

    public User addFriendRequest(int requestReceiverUsernameID, int requestMadeByUsernameID) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user, requester;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, requestReceiverUsernameID);
            requester = em.find(User.class, requestMadeByUsernameID);
            if (user == null || requester == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            FriendRequest friendReq = new FriendRequest(requester.getId(), requester.getFullName(), requester.getProfilePicture());
            user.addFriendRequest(friendReq);
            em.persist(user);
            em.getTransaction().commit();
        } catch (NullPointerException ex) {
            throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
        } finally {
            em.close();
        }
        return user;
    }

    public User acceptFriendRequest(int usernameID, int request_usernameID) throws NotFoundException, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        User requester;
        try {
            user = em.find(User.class, usernameID);
            requester = em.find(User.class, request_usernameID);
            if (user == null || requester == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            //Validating that there is actually a request and that a layer wasn't circumvented security.
            Boolean validation = user.validateSpecificFriendRequest(requester.getId());
            if (validation == false) {
                throw new AuthenticationException("Something unexpected went wrong, this could have been a try to circumvent the security.");
            }
            //Making the friend connection
            Friends friendRequester = new Friends(requester.getId());
            Friends friendReceiver = new Friends(user.getId());
            //Adding to each others friend list.
            user.addToFriendList(friendRequester);
            requester.addToFriendList(friendReceiver);
            //Removing friend reuqest
            user.deleteSpecificFriendRequest(requester.getId());
            //Persisting
            em.getTransaction().begin();
            em.persist(user);
            em.persist(requester);
            em.getTransaction().commit();
        } catch (NullPointerException ex) {
            throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
        } finally {
            em.close();
        }
        return user;
    }

    public User removeFriend(int userRequesterID, int userFriendID) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user, requester;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, userRequesterID);
            requester = em.find(User.class, userFriendID);
            if (user == null || requester == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            Boolean userBol = user.removeFriend(requester.getId());
            Boolean requestBol = requester.removeFriend(user.getId());
            if (!userBol == false && !requestBol == false) {
                em.persist(user);
                em.persist(requester);
                em.getTransaction().commit();
            }
        } catch (NullPointerException ex) {
            throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
        } finally {
            em.close();
        }
        return user;
    }

    public User removeFriendRequest(int userRequesterID, int userMadeRequestID) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, userRequesterID);
            if (user == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            Boolean userBol = user.deleteSpecificFriendRequest(userMadeRequestID);
            if (!userBol == false) {
                em.persist(user);
                em.getTransaction().commit();
            } else {
                throw new NotFoundException("No friend request found.");
            }
        } catch (NullPointerException ex) {
            throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
        } finally {
            em.close();
        }
        return user;
    }

    public List<UserDTO> friendSearch(String name) throws NotFoundException, SQLException, ClassNotFoundException {
        List<UserDTO> userDTOList = new ArrayList();
        String query = "SELECT full_name, profile_picture FROM users WHERE full_name LIKE ?";
        try {
            PreparedStatement ps = createConnection().prepareStatement(query);
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setFullName(rs.getString("full_name"));
                dto.setProfilePicture(rs.getString("profile_picture"));
                userDTOList.add(dto);
            }
            rs.close();
            ps.close();
            if (userDTOList.isEmpty()){
                throw new NotFoundException("No results by this name was found");
            }
        } catch (NullPointerException | NotFoundException ex) {
            throw new NotFoundException("No results by this name was found");
        } 
        return userDTOList;
    }
    

}
