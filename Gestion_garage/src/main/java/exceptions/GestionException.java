package exceptions;

public class GestionException extends Exception {
    
    public GestionException() {
        super();
    }
    
    public GestionException(String message) {
        super(message);
    }
    
    public GestionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GestionException(Throwable cause) {
        super(cause);
    }
}