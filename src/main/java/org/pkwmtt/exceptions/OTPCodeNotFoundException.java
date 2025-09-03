package org.pkwmtt.exceptions;

public class OTPCodeNotFoundException
  extends IllegalArgumentException {
    public OTPCodeNotFoundException () {
        super("Provided isn't assigned to any group.");
    }
}
