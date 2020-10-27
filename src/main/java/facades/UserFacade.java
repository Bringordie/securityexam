package facades;

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
import java.util.ArrayList;
import java.util.List;
import utils.EMF_Creator;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
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
    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User userResetPassword(String username, String secret, String newPassword) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
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

    public User createNormalUser(String fullName, String userName, String userPass, String secretAnswer, String profilePicture) throws AlreadyExistsException {
        EntityManager em = emf.createEntityManager();
        User userregister = new User(fullName, userName, userPass, secretAnswer, profilePicture);
        Role userRole = new Role("user");
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

    public boolean createPost(String username, String userPost) {
        EntityManager em = emf.createEntityManager();
        User user;
        UserPosts post = new UserPosts(userPost);
        try {
            em.getTransaction().begin();
            user = em.find(User.class, username);
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

    public List<UserPosts> getPosts(String username) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user;
        List<UserPosts> post = new ArrayList();
        try {
            em.getTransaction().begin();
            user = em.find(User.class, username);
            post = user.getUserPosts();
        } catch (NullPointerException ex) {
            throw new NotFoundException("User name could not be found");
        } finally {
            em.close();
        }
        return post;
    }

    public User addFriendRequest(String requestReceiverUsername, String requestMadeByUsername) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user, requester;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, requestReceiverUsername);
            requester = em.find(User.class, requestMadeByUsername);
            if (user == null || requester == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            FriendRequest friendReq = new FriendRequest(requester.getUserName(), requester.getFullName(), requester.getProfilePicture());
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

    public User acceptFriendRequest(String username, String request_username) throws NotFoundException, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        User requester;
        try {
            user = em.find(User.class, username);
            requester = em.find(User.class, request_username);
            if (user == null || requester == null) {
                throw new NotFoundException("Something unexpected went wrong, user name doesn't seem to exist");
            }
            //Validating that there is actaully a request and that a layer wasn't circumvented security.
            Boolean validation = user.validateSpecificFriendRequest(request_username);
            if (validation == false) {
                throw new AuthenticationException("Something unexpected went wrong, this could have been a try to circumvent the security.");
            }
            //Making the friend connection
            Friends friendRequester = new Friends(request_username);
            Friends friendReceiver = new Friends(username);
            //Adding to each others friend list.
            user.addToFriendList(friendRequester);
            requester.addToFriendList(friendReceiver);
            //Removing friend reuqest
            user.deleteSpecificFriendRequest(requester.getUserName());
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


}
