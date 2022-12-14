package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

public class FileLoadingException extends RuntimeException{
    public FileLoadingException() {
        super();
    }

    public FileLoadingException(String message) {
        super(message);
    }
}
