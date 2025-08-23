package org.pkwmtt.exceptions;

public class NoSuchElementWithProvidedIdException extends RuntimeException{
    public NoSuchElementWithProvidedIdException(int id) {
        super("No such element with id: " + id);
    }
}
