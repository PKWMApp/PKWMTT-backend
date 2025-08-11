package org.pkwmtt.exceptions;

public class SpecifiedGeneralGroupDoesntExistsException extends RuntimeException {
    public SpecifiedGeneralGroupDoesntExistsException() {
        super("Specified general group doesn't exists");
    }
}
