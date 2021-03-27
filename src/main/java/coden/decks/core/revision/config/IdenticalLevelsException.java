package coden.decks.core.revision.config;

/**
 * Thrown if during parsing the config to identical levels are defined
 */
public class IdenticalLevelsException extends RuntimeException{
    public IdenticalLevelsException() {
    }

    public IdenticalLevelsException(String message) {
        super(message);
    }

    public IdenticalLevelsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdenticalLevelsException(Throwable cause) {
        super(cause);
    }

    public IdenticalLevelsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
