package freelancer.backendtfg.shared.exceptions;

public class ActiveSessionExistsException extends RuntimeException {
    
    public ActiveSessionExistsException(String message) {
        super(message);
    }
    
    public ActiveSessionExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 