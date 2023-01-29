package de.florianmichael.tarasande_protocol_hack.xbox;

public class XboxException extends RuntimeException {

    public XboxException(String message) {
        super(message);
    }

    public XboxException(String message, Throwable cause) {
        super(message, cause);
    }

    public XboxException(Throwable cause) {
        super(cause);
    }

    public XboxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}