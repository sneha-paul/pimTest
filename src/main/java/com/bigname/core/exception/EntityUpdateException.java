package com.bigname.core.exception;

/**
 * Created by manu on 8/18/18.
 */
public class EntityUpdateException extends GenericEntityException {
    public EntityUpdateException() {
        super();
    }

    public EntityUpdateException(String message) {
        super(message);
    }

    public EntityUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityUpdateException(Throwable cause) {
        super(cause);
    }

    protected EntityUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
