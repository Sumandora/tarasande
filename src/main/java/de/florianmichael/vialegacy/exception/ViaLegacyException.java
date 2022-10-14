package de.florianmichael.vialegacy.exception;

public class ViaLegacyException extends RuntimeException {

    public ViaLegacyException(final String id) {
        super("Failed loading Component: " + id);
    }
}
