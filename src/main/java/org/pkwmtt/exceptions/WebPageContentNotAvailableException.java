package org.pkwmtt.exceptions;

public class WebPageContentNotAvailableException extends RuntimeException {
    public WebPageContentNotAvailableException(String message) {
        super(message);
    }

    public WebPageContentNotAvailableException() {
        super("Content of university webpage is not available right now.");
    }
}
