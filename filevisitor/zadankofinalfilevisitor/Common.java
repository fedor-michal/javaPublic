package com.fedordevelopment.wielowatkowosc.filevisitor.zadankofinalfilevisitor;

public class Common {

    public static void nonNull(Object value, String variableOrObjectName) {
        if (value == null) {
            throw new IllegalArgumentException(variableOrObjectName + " cannot be null.");
        }
    }

}
