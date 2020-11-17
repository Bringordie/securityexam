package facades;

import dtos.user.FriendsDTO;
import dtos.user.UserDTO;
import dtos.user.UserPostsDTO;
import entities.FriendRequest;
import entities.Friends;
import entities.Role;
import entities.User;
import entities.UserPosts;
import errorhandling.AuthenticationException;
import errorhandling.NoFriendRequestsException;
import errorhandling.NoFriendsException;
import errorhandling.NotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Frederik Braagaard
 */
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private EntityManager em;

    private User u1, u2, u3, u4;
    private Role r1, r2;
    private Friends f1, f2;
    private UserPosts up1, up2;
    private FriendRequest fr1, fr2;

    public UserFacadeTest() {
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);
        facade.serverStatus = false;
    }

    @AfterAll
    public static void tearDownClass() {
    }

    /**
     *
     * @author Frederik Braagaard
     */
    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("UserPosts.deleteAllRows").executeUpdate();
            em.createNamedQuery("Friends.deleteAllRows").executeUpdate();
            em.createNamedQuery("FriendRequest.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            r1 = new Role("user");
            r2 = new Role("admin");
            em.persist(r1);
            em.persist(r2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            u1 = new User("User user", "user", "password", "where I was born", UUID.randomUUID().toString());
            u1.addRole(r1);
            u2 = new User("User2 user", "user2", "password", "where I went to school", UUID.randomUUID().toString());
            u2.addRole(r1);
            u3 = new User("User3 user", "user3", "password", "where I first traveled to", UUID.randomUUID().toString());
            u3.addRole(r1);
            u4 = new User("Admin admin", "admin", "password", "where I went to school", UUID.randomUUID().toString());
            u4.addRole(r2);

            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(u4);

            em.getTransaction().commit();

            up1 = new UserPosts("This is a post made by a user");
            up2 = new UserPosts("This is a post made by a admin");

            u1.addUserPost(up1);
            u4.addUserPost(up2);

            f1 = new Friends(u4.getId());
            f2 = new Friends(u1.getId());

            u1.addToFriendList(f1);
            u4.addToFriendList(f2);
            fr1 = new FriendRequest(u2.getId(), u2.getFullName(), u2.getProfilePicture());
            fr2 = new FriendRequest(u1.getId(), u1.getFullName(), u1.getProfilePicture());

            u1.addFriendRequest(fr1);
            u3.addFriendRequest(fr2);

            em.getTransaction().begin();
            em.persist(u1);
            em.persist(u2);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    /**
     * Test of addPost method, of class UserFacade pass.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void testAddPostPass() throws Exception {
        //UserPosts post = new UserPosts("Today was a very good day. - End of diary");
        Boolean response = facade.createPost(u2.getId(), "Today was a very good day. - End of diary");
        assertEquals(response, true);
    }

    /**
     * Test of addPost method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void testAddPostFail() throws Exception {
        //UserPosts post = new UserPosts("Today was a very good day. - End of diary");
        Boolean response = facade.createPost(404, "Today was a very good day. - End of diary");
        assertEquals(response, false);
    }

    /**
     * Test of getPosts method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void testGetPostsSuccess() throws NotFoundException {
        //UserPosts post = new UserPosts("Today was a very good day. - End of diary");
        List<UserPosts> response = facade.getPosts(u1.getId());
        assertThat(response.size(), equalTo(u1.getUserPosts().size()));
    }

    /**
     * Test of getPosts method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void testGetPostsFail() throws NotFoundException {
        try {
            List<UserPosts> response = facade.getPosts(404);
            fail("This will fail as the username doesn't exist");
        } catch (NotFoundException | NullPointerException ex) {
            final String msg = "User name could not be found";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of getUserResetPassword method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void userResetPasswordPass() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        User response = facade.userResetPassword(u1.getUserName(), "where I was born", "new password");
        assertNotNull(response);
    }

    /**
     * Test of getUserResetPassword method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void userResetPasswordFail() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        try {
            User response = facade.userResetPassword("404", "incorrect", "incorrect");
            fail("Invalid user name or secret");
        } catch (NullPointerException | AuthenticationException ex) {
            final String msg = "Invalid user name or secret";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of getUserResetPassword method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void userResetPasswordFail2() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        try {
            User response = facade.userResetPassword(u1.getUserName(), "incorrect", "new password");
            fail("Invalid user name or secret");
        } catch (NullPointerException | AuthenticationException ex) {
            final String msg = "Invalid user name or secret";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of addFriendRequest method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void userAddFriendRequestPass() throws NotFoundException, AuthenticationException {
        assertEquals(1, u3.getFriendRequests().size());

        User response = facade.addFriendRequest(u3.getId(), u2.getId());

        em = emf.createEntityManager();
        User findu3 = em.find(User.class, u3.getId());
        assertEquals(2, findu3.getFriendRequests().size());
        em.close();
        assertNotNull(response);

    }

    /**
     * Test of addFriendRequest method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void userAddFriendRequestFail() throws NotFoundException {
        try {
            User response = facade.addFriendRequest(u1.getId(), 404);
            fail("Invalid user name");
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of acceptFriendRequest method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void acceptFriendRequestPass() throws NotFoundException, AuthenticationException {
        assertEquals(0, u3.getFriendList().size());
        User response = new User();
        try {
            //Creating a friend request
            response = facade.addFriendRequest(u3.getId(), u2.getId());
            //Accepting friend request
            response = facade.acceptFriendRequest(u3.getId(), u2.getId());
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
        }
        assertNotNull(response);

        em = emf.createEntityManager();
        User findu3 = em.find(User.class, u3.getId());
        assertEquals(1, findu3.getFriendList().size());
        em.close();
    }

    /**
     * Test of acceptFriendRequest method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void acceptFriendRequestFail() throws NotFoundException, AuthenticationException {
        try {
            User response = facade.acceptFriendRequest(u2.getId(), 512);
            fail("Invalid user name");
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of removeFriend method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void removeFriendPass() throws NotFoundException, AuthenticationException {
        assertEquals(1, u1.getFriendList().size());
        User response = new User();
        try {
            //Removing friend
            facade.removeFriend(u1.getId(), u4.getId());
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
        }

        em = emf.createEntityManager();
        User findu1 = em.find(User.class, u1.getId());
        assertEquals(0, findu1.getFriendList().size());
        em.close();
    }

    /**
     * Test of removeFriend method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void removeFriendFail() throws NotFoundException, AuthenticationException {
        try {
            User response = facade.removeFriend(u1.getId(), 404);
            fail("Invalid user name");
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of removeFriendRequest method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void removeFriendRequestPass() throws NotFoundException, AuthenticationException {
        assertEquals(1, u1.getFriendRequests().size());
        User response = new User();
        try {
            //Removing friend request
            facade.removeFriendRequest(u1.getId(), u2.getId());
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "Something unexpected went wrong, user name doesn't seem to exist";
        }

        em = emf.createEntityManager();
        User findu1 = em.find(User.class, u1.getId());
        assertEquals(0, findu1.getFriendRequests().size());
        em.close();
    }

    /**
     * Test of removeFriendRequest method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void removeFriendRequestFail() throws NotFoundException, AuthenticationException {
        try {
            User response = facade.removeFriendRequest(u1.getId(), 404);
            fail("Invalid user name");
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "No friend request found.";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of friendSearch method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void findFriendPass() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        List<UserDTO> response = new ArrayList();
        try {
            response = facade.friendSearch("admin");
        } catch (NullPointerException | NotFoundException ex) {
            throw new NotFoundException("No results by this name was found");
        }

        assertNotNull(response);
        assertEquals(u4.getFullName(), response.get(0).getFullName());
    }

    /**
     * Test of friendSearch method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void findFriendFail() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        try {
            List<UserDTO> response = facade.friendSearch("Doesn't exist");
            fail("Invalid user name");
        } catch (NullPointerException | NotFoundException ex) {
            final String msg = "No results by this name was found";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of friendPosts method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void friendPostsPass() throws NotFoundException, NoFriendsException {
        List<UserDTO> response = new ArrayList();
        try {
            response = facade.friendPosts(u1.getId());
        } catch (NoFriendsException | NotFoundException ex) {
            throw new NotFoundException("No results was found");
        }

        assertNotNull(response);
        assertEquals(u4.getUserPosts().get(0).getMessage(), response.get(0).getPosts().get(0).getMessage());
    }

    /**
     * Test of friendPosts method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void friendPostsFail() throws NotFoundException, NoFriendsException {
        try {
            List<UserDTO> response = facade.friendPosts(u3.getId());
            fail("Invalid user currrently has no friends");
        } catch (NoFriendsException ex) {
            final String msg = "This user currently has no friends in their friendlist.";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of getVeryfiedAdmin method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void getVeryfiedAdminPass() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException {
        User response = facade.getVeryfiedAdmin(u4.getUserName(), "password");
        assertNotNull(response);
        assertEquals(u4.getFullName(), response.getFullName());
    }

    /**
     * Test of getVeryfiedAdmin method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void getVeryfiedAdminFail() throws NotFoundException, NoFriendsException, SQLException, ClassNotFoundException {
        try {
            User response = facade.getVeryfiedAdmin(u3.getUserName(), "password");
            fail("Should fail");
        } catch (AuthenticationException ex) {
            final String msg = "Users cannot login here";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of getVeryfiedAdmin method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void getVeryfiedAdminFailVerify() throws NotFoundException, NoFriendsException, SQLException, ClassNotFoundException {
        try {
            User response = facade.getVeryfiedAdmin(u4.getUserName(), "wrong_password");
            fail("Should fail");
        } catch (AuthenticationException ex) {
            final String msg = "Invalid user name or password";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of viewFriends method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void viewFriendsPass() throws NotFoundException, AuthenticationException, SQLException, ClassNotFoundException, NoFriendsException {
        facade.addFriendRequest(u2.getId(), u3.getId());
        facade.acceptFriendRequest(u2.getId(), u3.getId());
        List<FriendsDTO> response = facade.viewFriends(u2.getId());
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    /**
     * Test of viewFriends method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void viewFriendsFail() throws NotFoundException, NoFriendsException, SQLException, ClassNotFoundException {
        try {
            List<FriendsDTO> response = facade.viewFriends(u3.getId());
            fail("Should fail");
        } catch (NoFriendsException ex) {
            final String msg = "This user currently has no friends in their friendlist.";
            assertEquals(msg, ex.getMessage());
        }
    }

    /**
     * Test of adminGetUsers method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void adminGetUsersSuccess() throws NotFoundException, SQLException, ClassNotFoundException {
        List<UserDTO> response = facade.adminGetUsers();
        assertNotNull(response);
        assertEquals(4, response.size());
    }

    /**
     * Test of viewFriendRequests method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void viewFriendRequestsPass() throws NoFriendRequestsException, NotFoundException {
        facade.addFriendRequest(u2.getId(), u3.getId());
        List<FriendsDTO> response = facade.viewFriendRequests(u2.getId());
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    /**
     * Test of viewFriendRequests method, of class UserFacade fail.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void viewFriendRequestsFail() throws NotFoundException, NoFriendRequestsException {
        try {
            List<FriendsDTO> response = facade.viewFriendRequests(u2.getId());
            fail("Should fail");
        } catch (NoFriendRequestsException ex) {
            final String msg = "This user no friend requests.";
            assertEquals(msg, ex.getMessage());
        }
    }
    
    /**
     * Test of adminGetPosts method, of class UserFacade success.
     *
     * @author Frederik Braagaard
     */
    @Test
    public void adminGetPostsPass() throws NoFriendRequestsException, NotFoundException, SQLException, ClassNotFoundException {
        List<UserPostsDTO> response = facade.adminGetPosts();
        assertNotNull(response);
        assertEquals(2, response.size());
    }

}
