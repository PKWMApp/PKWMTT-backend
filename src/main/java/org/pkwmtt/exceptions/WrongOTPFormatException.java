package org.pkwmtt.exceptions;

public class WrongOTPFormatException
  extends IllegalArgumentException {
    public WrongOTPFormatException (String message) {
        super(message);
    }
}
