package org.pkwmtt.exceptions;

public class SpecifiedGeneralGroupDoesntExistsException extends RuntimeException {
    public SpecifiedGeneralGroupDoesntExistsException() {
        super("Specified general group doesn't exists");
    }
    
    public SpecifiedGeneralGroupDoesntExistsException(String generalGroupName) {
        super(String.format("Specified general group [%s] doesn't exists", generalGroupName));
    }
}
