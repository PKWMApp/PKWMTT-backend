package org.pkwmtt.exceptions;

public class InvalidGroupIdentifierException extends RuntimeException {
    public InvalidGroupIdentifierException(String groupIdentifier) {
        super("Invalid group identifier: " + groupIdentifier);
    }
}
