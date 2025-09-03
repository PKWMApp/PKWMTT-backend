package org.pkwmtt.exceptions;

public class ExamTypeNotExistsException extends RuntimeException {
    public ExamTypeNotExistsException(String examType) {
        super("Invalid exam type " + examType);
    }
}
