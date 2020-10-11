package facades;

import entities.Role;
import entities.User;
import errorhandling.AlreadyExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import utils.EMF_Creator;


public class UserFacade {
  
    private static EntityManagerFactory emf;
    private static UserFacade instance;
    
    private UserFacade(){}
    
    /**
     * 
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade (EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }
    
    /**
     * This method is used to check if a user with the given password exists in the DB.
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
    
    public User createNormalUser(String username, String password) throws AlreadyExistsException {
        EntityManager em = emf.createEntityManager();
        User userregister = new User(username, password);
        Role userRole = new Role("user");
        userregister.addRole(userRole);
        try {
            User user = em.find(User.class, username);
            if (user != null ) {
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
    
    public User adminCreateUser(String username, String password, String role) throws AlreadyExistsException {
        EntityManager em = emf.createEntityManager();
        User userregister = new User(username, password);
        Role userRole = new Role(role);
        userregister.addRole(userRole);
        try {
            User user = em.find(User.class, username);
            if (user != null ) {
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
    

     public static void main(String[] args) throws AlreadyExistsException {
         emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
        UserFacade UF = new UserFacade();
         //System.out.println(UF.createSupportUser("testsupport", "test2020"));
         //UF.changeUserPW("user", "test12");
//         EntityManager em = emf.createEntityManager();
//         User user = em.find(User.class, "user");
//         System.out.println(user.verifyPassword("test12"));
    }
}
