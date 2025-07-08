package br.com.ifmg.event_manager.services.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException() {

    }

    public DatabaseException(String message) {
        super(message);
    }
}
