package com.hackathon.billing.exception;

public class BillingValidationException extends BillingException {
    public BillingValidationException(String message) {
        super(message);
    }

    public BillingValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}