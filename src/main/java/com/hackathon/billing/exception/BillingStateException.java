package com.hackathon.billing.exception;

public class BillingStateException extends BillingException {
    public BillingStateException(String message) {
        super(message);
    }

    public BillingStateException(String message, Throwable cause) {
        super(message, cause);
    }
}