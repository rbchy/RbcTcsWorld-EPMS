package com.epms.payroll.exception;

/**
 * Thrown by {@link com.epms.payroll.service.AuthService#login} when the username does not
 * exist, the account is inactive, or the password does not match. The message is deliberately
 * identical in every case ("Invalid username or password") so a caller cannot use the response
 * to enumerate which usernames exist in the system.
 *
 * {@link com.epms.payroll.exception.GlobalExceptionHandler} maps this to HTTP 401 Unauthorized.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
