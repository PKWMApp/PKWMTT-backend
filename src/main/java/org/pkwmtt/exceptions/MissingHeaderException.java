package org.pkwmtt.exceptions;

public class MissingHeaderException extends Exception {
    public MissingHeaderException (String headerName) {
        super(String.format("Missing header: [%s]", headerName));
    }
}
