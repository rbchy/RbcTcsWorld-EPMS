package com.epms.payroll.service;

import com.epms.payroll.dto.LoginRequest;
import com.epms.payroll.dto.LoginResponse;
import com.epms.payroll.entity.User;
import com.epms.payroll.repository.UserRepository;
import com.epms.payroll.security.TokenService;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Positive and negative coverage for AuthService.login(): the one gate every user of
 * the desktop client passes through. Every failure path must return the SAME generic
 * "Invalid username or password" message — never leaking which half was wrong.
 */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Authentication")
class AuthServiceTest {

    private static final String GENERIC_ERROR = "Invalid username or password";

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private TokenService tokens;

    private AuthService service() {
        return new AuthService(userRepo, encoder, tokens);
    }

    private User activeUser() {
        User u = new User();
        u.setUsername("jdoe");
        u.setPasswordHash("hashed-secret");
        u.setRole("PAYROLL_ADMIN");
        u.setActive(true);
        return u;
    }

    @Test
    @Tag("positive")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Correct username and password returns a token, username and role")
    @Description("A valid credential pair for an active user must issue a session token and echo back username/role.")
    void validCredentialsReturnToken() {
        when(userRepo.findByUsername("jdoe")).thenReturn(Optional.of(activeUser()));
        when(encoder.matches("correct-pw", "hashed-secret")).thenReturn(true);
        when(tokens.issue("jdoe")).thenReturn("tok-abc-123");

        LoginResponse response = service().login(new LoginRequest("jdoe", "correct-pw"));

        assertEquals("tok-abc-123", response.token());
        assertEquals("jdoe", response.username());
        assertEquals("PAYROLL_ADMIN", response.role());
    }

    @Test
    @Tag("negative")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Unknown username is rejected with the generic error")
    @Description("A username that does not exist must fail with the same message as a wrong password, so an attacker cannot enumerate valid usernames.")
    void unknownUsernameRejected() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service().login(new LoginRequest("ghost", "whatever")));
        assertEquals(GENERIC_ERROR, ex.getMessage());
    }

    @Test
    @Tag("negative")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Wrong password for a real user is rejected with the generic error")
    @Description("An existing, active user with an incorrect password must be rejected without a token being issued.")
    void wrongPasswordRejected() {
        when(userRepo.findByUsername("jdoe")).thenReturn(Optional.of(activeUser()));
        when(encoder.matches("wrong-pw", "hashed-secret")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service().login(new LoginRequest("jdoe", "wrong-pw")));
        assertEquals(GENERIC_ERROR, ex.getMessage());
    }

    @Test
    @Tag("negative")
    @Story("Login")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A deactivated account is rejected even with the correct password")
    @Description("An inactive/disabled user must never be allowed to log in, regardless of whether the password is correct.")
    void inactiveUserRejectedEvenWithCorrectPassword() {
        User inactive = activeUser();
        inactive.setActive(false);
        when(userRepo.findByUsername("jdoe")).thenReturn(Optional.of(inactive));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service().login(new LoginRequest("jdoe", "correct-pw")));
        assertEquals(GENERIC_ERROR, ex.getMessage());
    }
}
