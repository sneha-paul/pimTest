package com.bigname.pim.api.exception;

/**
 * Created by manu on 8/18/18.
 */
public class GenericEntityException extends GenericPlatformException {
    public GenericEntityException() {
        super();
    }

    public GenericEntityException(String message) {
        super(message);
    }

    public GenericEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericEntityException(Throwable cause) {
        super(cause);
    }

    protected GenericEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
