package br.com.ifmg.event_manager.controllers.exceptions;

import java.time.Instant;

public class StandartError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public StandartError() {
    }
    public StandartError(Instant timestamp, String message, String path, String error, Integer status) {
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
        this.error = error;
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
