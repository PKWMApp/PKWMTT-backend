package org.pkwmtt.exceptions;

public class MailServiceNotAvailableException
  extends RuntimeException {
    public MailServiceNotAvailableException () {
        super("Mail service is not available right now.");
    }
    
}
