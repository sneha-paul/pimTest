package com.bigname.core.exception;

/**
 * Created by Manu on 8/8/2018.
 */
public class GenericPlatformException extends RuntimeException {

    public GenericPlatformException() {
    }

    public GenericPlatformException(String message) {
        super(message);
    }

    public GenericPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericPlatformException(Throwable cause) {
        super(cause);
    }

    public GenericPlatformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
