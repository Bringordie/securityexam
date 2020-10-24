package facades;

import entities.FriendRequest;
import entities.Friends;
import entities.Role;
import entities.User;
import entities.UserPosts;
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

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

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
            u1 = new User("User user", "user", "test", "where I was born", UUID.randomUUID().toString());
            u1.addRole(r1);
            u2 = new User("User2 user", "user2", "test", "where I went to school", UUID.randomUUID().toString());
            u1.addRole(r1);
            u3 = new User("User3 user", "user3", "test", "where I first traveled to", UUID.randomUUID().toString());
            u1.addRole(r1);
            u4 = new User("Admin admin", "admin", "test", "where I went to school", UUID.randomUUID().toString());
            u4.addRole(r2);

            em.persist(r1);
            em.persist(r2);
            
            em.getTransaction().commit();

            up1 = new UserPosts("This is a post made by a user");
            up2 = new UserPosts("This is a post made by a admin");

            u1.addUserPost(up1);
            u4.addUserPost(up2);
            
            f1 = new Friends(u4.getUserName());
            f2 = new Friends(u1.getUserName());

            u1.addToFriendList(f1);
            u2.addToFriendList(f2);
            
            em.getTransaction().begin();
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(u4);

            em.getTransaction().commit();

            fr1 = new FriendRequest(u2.getUserName(), u2.getFullName(), u2.getProfilePicture());
            fr2 = new FriendRequest(u1.getUserName(), u1.getFullName(), u1.getProfilePicture());

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
     */
    @Test
    public void testAddPostPass() throws Exception {
        //UserPosts post = new UserPosts("Today was a very good day. - End of diary");
        Boolean response = facade.createPost(u2.getUserName(), "Today was a very good day. - End of diary");
        assertEquals(response, true);
    }
    
    /**
     * Test of addPost method, of class UserFacade fail.
     */
    @Test
    public void testAddPostFail() throws Exception {
        //UserPosts post = new UserPosts("Today was a very good day. - End of diary");
        Boolean response = facade.createPost("notfound", "Today was a very good day. - End of diary");
        assertEquals(response, false);
    }

}
