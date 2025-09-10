package org.pkwmtt.exceptions;

import java.util.Set;

public class InvalidGroupIdentifierException extends RuntimeException {
    public InvalidGroupIdentifierException(String groupIdentifier) {
        super("Invalid group identifier: " + groupIdentifier);
    }

    public InvalidGroupIdentifierException(Set<String> groupIdentifiers) {
        super("Invalid group identifiers: " + groupIdentifiers.toString());
    }

    public InvalidGroupIdentifierException(Set<String> all, Set<String> provided) {
        super("Invalid group identifiers: " + extractInvalidGroups(all, provided));
    }

    private static String extractInvalidGroups(Set<String> all, Set<String> provided) {
        provided.removeAll(all);
        return provided.toString();
    }
}
