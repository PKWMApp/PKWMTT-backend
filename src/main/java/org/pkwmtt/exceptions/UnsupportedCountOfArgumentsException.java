package org.pkwmtt.exceptions;

public class UnsupportedCountOfArgumentsException extends RuntimeException {
    public UnsupportedCountOfArgumentsException(int expectedMin, int expectedMax, Integer provided) {
        super("Invalid count of arguments provided: " + provided +
                " expected more than: " + expectedMin + " less than: " + expectedMax);
    }
}
