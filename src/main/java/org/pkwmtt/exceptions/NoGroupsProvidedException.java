package org.pkwmtt.exceptions;

public class NoGroupsProvidedException extends RuntimeException {
    public NoGroupsProvidedException() {
        super("groups set is empty");
    }
}
