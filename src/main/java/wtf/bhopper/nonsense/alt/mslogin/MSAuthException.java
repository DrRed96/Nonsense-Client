package wtf.bhopper.nonsense.alt.mslogin;

public class MSAuthException extends Exception {
    public MSAuthException() {}

    public MSAuthException(String message) {
        super(message);
    }

    public MSAuthException(Throwable throwable) {
        super(throwable);
    }
}
