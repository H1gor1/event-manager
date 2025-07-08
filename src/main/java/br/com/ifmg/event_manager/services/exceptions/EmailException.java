package br.com.ifmg.event_manager.services.exceptions;

public class EmailException extends RuntimeException {

    public EmailException(String message) {
        super(message);
    }
}
