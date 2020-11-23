package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class LoginMaxTriesException extends Exception{

    public LoginMaxTriesException(String message) {
        super(message);
    }

    public LoginMaxTriesException() {
        super("The maximum of 5 tries within 10 minutes has been reached.");
    }  
}
