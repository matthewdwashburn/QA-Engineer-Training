package banking;

/** Checked — unknown or invalid account id. */
public class InvalidAccountException extends Exception {
    public InvalidAccountException(String message) {
        super(message);
    }
}
