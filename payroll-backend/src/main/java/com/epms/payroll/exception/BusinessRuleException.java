package com.epms.payroll.exception;

/**
 * Thrown by the service layer when a request is well-formed and refers to a real resource,
 * but is rejected because it would violate a business/state-machine rule — clocking in twice
 * before clocking out, clocking out an already-closed entry, voiding a payroll that is already
 * PROCESSED/REVERSED, or reversing one that was never PROCESSED.
 *
 * {@link com.epms.payroll.exception.GlobalExceptionHandler} maps this to HTTP 409 Conflict.
 * Using a dedicated type here (rather than a bare RuntimeException) is what lets the global
 * handler distinguish "the request conflicts with current state" from an actual programming
 * bug, which should keep surfacing as a 500 instead of being silently swallowed into a 409.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
