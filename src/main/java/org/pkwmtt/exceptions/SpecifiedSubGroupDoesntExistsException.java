package org.pkwmtt.exceptions;

public class SpecifiedSubGroupDoesntExistsException
  extends RuntimeException {
    public SpecifiedSubGroupDoesntExistsException () {
        super("Specified sub group doesn't exists");
    }
    
    public SpecifiedSubGroupDoesntExistsException (String subgroupName) {
        super(String.format("Specified sub group [%s] doesn't exists", subgroupName));
    }
}
