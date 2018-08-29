package com.bigname.pim.api.exception;

/**
 * Created by manu on 8/18/18.
 */
public class EntityDeactivateException extends GenericEntityException {
    public EntityDeactivateException() {
        super();
    }

    public EntityDeactivateException(String message) {
        super(message);
    }

    public EntityDeactivateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityDeactivateException(Throwable cause) {
        super(cause);
    }

    protected EntityDeactivateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
