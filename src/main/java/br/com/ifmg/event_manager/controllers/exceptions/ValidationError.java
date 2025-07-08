package br.com.ifmg.event_manager.controllers.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandartError{

    private List<FieldMessage> errors = new ArrayList<FieldMessage>();

    public ValidationError() {
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldMessage> errors) {
        this.errors = errors;
    }

    public void addFieldError(String field, String message) {
        this.errors.add(new FieldMessage(field, message));
    }
}
