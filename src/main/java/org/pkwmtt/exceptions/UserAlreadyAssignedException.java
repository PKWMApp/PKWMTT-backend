package org.pkwmtt.exceptions;

public class UserAlreadyAssignedException extends RuntimeException {
    public UserAlreadyAssignedException (String message) {
        super(message);
    }
}
