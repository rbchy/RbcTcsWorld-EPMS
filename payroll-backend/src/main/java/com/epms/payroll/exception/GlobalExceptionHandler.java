package com.epms.payroll.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Central mapping from the exceptions the service layer already throws to proper HTTP status
 * codes and a small, consistent JSON error body ({@code timestamp}, {@code status},
 * {@code error}, {@code message}).
 *
 * Before this class existed there was no @ControllerAdvice anywhere in the app, so every one
 * of the cases below surfaced as a raw, unhandled 500 (a jakarta.servlet.ServletException
 * wrapping the real cause) — see the payroll-backend integration test suite's
 * *ApiTest classes for the negative-path coverage that pinned down each of these before the
 * fix, and that now asserts the proper status codes below instead.
 *
 * - {@link NoSuchElementException} (the exception {@code Optional.orElseThrow()} throws when a
 *   path-variable id — employee, payroll, time entry, ... — doesn't exist) → 404 Not Found.
 * - {@link BusinessRuleException} (a request that conflicts with the current state of a real
 *   resource — double clock-in, voiding an already-PROCESSED payroll, ...) → 409 Conflict.
 * - {@link AuthenticationFailedException} (bad username/password at login) → 401 Unauthorized.
 * - {@link DataIntegrityViolationException} (a required column was left null, or a database
 *   constraint was otherwise violated — there is no bean-validation layer in front of the
 *   JPA save() calls) → 400 Bad Request.
 *
 * Anything else (a genuine, unanticipated bug) is intentionally left alone here and continues
 * to surface as Spring Boot's default 500 — silently downgrading every RuntimeException to a
 * handled response would hide real defects behind a clean-looking error body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return build(HttpStatus.NOT_FOUND, "The requested resource does not exist.");
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "The request could not be saved because a required field was missing, or a value "
                        + "violated a database constraint: " + rootMessage(ex));
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    private String rootMessage(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}
