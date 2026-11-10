package com.epms.payroll.security;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Positive and negative coverage for the in-memory session token store. */
@Epic("EPMS Payroll Backend")
@Feature("Authentication")
class TokenServiceTest {

    @Test
    @Tag("positive")
    @Story("Token lifecycle")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A token issued for a username resolves back to that same username")
    void issuedTokenResolvesToUsername() {
        TokenService tokens = new TokenService();
        String token = tokens.issue("jdoe");

        assertEquals("jdoe", tokens.username(token));
    }

    @Test
    @Tag("positive")
    @Story("Token lifecycle")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Two separate issue() calls produce two distinct tokens")
    void eachIssuedTokenIsUnique() {
        TokenService tokens = new TokenService();
        String first = tokens.issue("jdoe");
        String second = tokens.issue("jdoe");

        assertNotEquals(first, second, "re-logging in must not reuse a previous session token");
    }

    @Test
    @Tag("negative")
    @Story("Token lifecycle")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("An unknown/never-issued token resolves to no username")
    void unknownTokenResolvesToNull() {
        TokenService tokens = new TokenService();

        assertNull(tokens.username("this-token-was-never-issued"));
    }

    @Test
    @Tag("negative")
    @Story("Token lifecycle")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A revoked token no longer resolves to a username")
    void revokedTokenNoLongerResolves() {
        TokenService tokens = new TokenService();
        String token = tokens.issue("jdoe");
        tokens.revoke(token);

        assertNull(tokens.username(token), "logging out must actually invalidate the token, not just forget it client-side");
    }
}
