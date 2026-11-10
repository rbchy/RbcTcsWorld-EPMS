package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for /api/auth/login. The seeded "admin"/"admin123" account
 * (created by DataInitializer on every context startup, including this H2 test context) is
 * used as the known-good credential pair.
 *
 * The two negative cases here used to assert on a raw, uncaught 500 (see the ApiTestSupport
 * Javadoc for why that showed up as an exception out of mockMvc.perform(...) rather than a
 * response). Now that GlobalExceptionHandler maps AuthenticationFailedException to 401, both
 * assert the proper response directly.
 */
@Epic("EPMS Payroll Backend")
@Feature("Authentication")
class AuthApiTest extends ApiTestSupport {

    @Test
    @Tag("positive")
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("The seeded admin account logs in and receives a token + role")
    void adminLoginSucceeds() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "admin", "password", "admin123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.token", not(emptyOrNullString())));
    }

    @Test
    @Tag("negative")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A wrong password returns 401 Unauthorized with a generic message")
    void wrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "admin", "password", "definitely-wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid username or password")));
    }

    @Test
    @Tag("negative")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("An unknown username returns the same 401 + generic message as a wrong password — the " +
            "response must never reveal whether the username itself exists")
    void unknownUsernameReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "no-such-user", "password", "whatever"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid username or password")));
    }
}
