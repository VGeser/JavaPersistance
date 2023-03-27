package ru.nsu.fit.persistance;

public class PersistenceException extends RuntimeException {
    protected PersistenceException(String msg) {
        super(msg);
    }

    protected PersistenceException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
