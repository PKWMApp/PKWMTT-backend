package org.pkwmtt.exceptions;

public class MailCouldNotBeSendException extends RuntimeException {
    public MailCouldNotBeSendException (String message) {
        super(message);
    }
}
