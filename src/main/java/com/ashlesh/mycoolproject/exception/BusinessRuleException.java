package com.ashlesh.mycoolproject.exception;

// Thrown when the request is valid but breaks a business rule (HTTP 422)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}