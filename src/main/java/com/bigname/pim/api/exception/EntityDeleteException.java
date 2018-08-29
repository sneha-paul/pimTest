package com.bigname.pim.api.exception;

/**
 * Created by manu on 8/18/18.
 */
public class EntityDeleteException extends GenericEntityException {
    public EntityDeleteException() {
        super();
    }

    public EntityDeleteException(String message) {
        super(message);
    }

    public EntityDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityDeleteException(Throwable cause) {
        super(cause);
    }

    protected EntityDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
