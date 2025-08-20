package org.pkwmtt.exceptions;

import java.util.Set;

public class InvalidGroupIdentifierException extends RuntimeException {
    public InvalidGroupIdentifierException(String groupIdentifier) {
        super("Invalid group identifier: " + groupIdentifier);
    }

    public InvalidGroupIdentifierException(Set<String> groupIdentifiers) {
        super("Invalid group identifiers: " + groupIdentifiers.toString());
    }
}
