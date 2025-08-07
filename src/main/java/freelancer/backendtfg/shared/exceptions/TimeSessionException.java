package freelancer.backendtfg.shared.exceptions;

public class TimeSessionException extends RuntimeException {
    
    public TimeSessionException(String message) {
        super(message);
    }
    
    public TimeSessionException(String message, Throwable cause) {
        super(message, cause);
    }
} 