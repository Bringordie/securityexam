package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class NoFriendsException extends Exception{

    public NoFriendsException(String message) {
        super(message);
    }

    public NoFriendsException() {
        super("Requested item could not be found");
    }  
}