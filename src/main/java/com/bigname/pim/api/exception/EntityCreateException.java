package com.bigname.pim.api.exception;

/**
 * Created by manu on 8/18/18.
 */
public class EntityCreateException extends GenericEntityException {

    public EntityCreateException() {
        super();
    }

    public EntityCreateException(String message) {
        super(message);
    }

    public EntityCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCreateException(Throwable cause) {
        super(cause);
    }

    protected EntityCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
