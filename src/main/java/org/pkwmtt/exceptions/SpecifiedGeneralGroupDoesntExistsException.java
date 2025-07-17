package org.pkwmtt.exceptions;

import net.bytebuddy.asm.Advice;

public class SpecifiedGeneralGroupDoesntExistsException extends RuntimeException {
    public SpecifiedGeneralGroupDoesntExistsException() {
        super("Specified general group doesn't exists");
    }
}
