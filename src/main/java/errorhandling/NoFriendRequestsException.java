package errorhandling;

/**
 *
 * @author Frederik Braagaard
 */
public class NoFriendRequestsException extends Exception{

    public NoFriendRequestsException(String message) {
        super(message);
    }

    public NoFriendRequestsException() {
        super("Requested item could not be found");
    }  
}