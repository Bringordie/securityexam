package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class AlreadyExistsException extends Exception{

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException() {
        super("User already exists");
    }  
}