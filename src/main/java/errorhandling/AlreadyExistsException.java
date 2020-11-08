package errorhandling;

/**
 *
 * @author Frederik Braagaard
 */
public class AlreadyExistsException extends Exception{

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException() {
        super("User already exists");
    }  
}