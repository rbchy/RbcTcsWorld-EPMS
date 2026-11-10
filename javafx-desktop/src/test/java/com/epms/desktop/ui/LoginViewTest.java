package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Headless TestFX UI-automation coverage for LoginView, demonstrating the recipe for
 * driving the JavaFX desktop client end-to-end: launch the real screen with a mocked
 * ApiClient (LoginView.show(stage, api) takes it as a constructor-free parameter, so no
 * real HTTP calls are made), then drive it with TestFX's robot the same way a person
 * would click and type. Headless execution is wired via Monocle + the system properties
 * configured on maven-surefire-plugin in this module's pom.xml.
 *
 * (This module has no Allure dependency — that reporting is wired up only in
 * payroll-backend — so positive/negative cases here are tagged with JUnit 5's own @Tag.)
 *
 * Every other screen (DashboardView, EmployeeView, PayrollView, ...) follows this exact
 * same pattern: mock ApiClient, show() the view under test, drive it with the robot.
 */
class LoginViewTest extends ApplicationTest {

    // A fresh test instance (and therefore a fresh mock) is created by JUnit 5 for every
    // @Test method, and start(Stage) below runs on that same instance before the test body —
    // so this plain Mockito.mock() call is already in place by the time LoginView is shown.
    private final ApiClient api = Mockito.mock(ApiClient.class);

    @Override
    public void start(Stage stage) {
        new LoginView().show(stage, api);
    }

    @Test
    @Tag("positive")
    @DisplayName("A successful login (pre-filled admin/admin123) navigates to the Dashboard screen")
    void successfulLoginNavigatesToDashboard() throws Exception {
        Mockito.when(api.login("admin", "admin123")).thenReturn("ADMIN");

        clickOn(".login-button");
        WaitForAsyncUtils.waitForFxEvents();

        Label welcome = lookup(".title").query();
        assertEquals(I18n.t("dashboard.welcome"), welcome.getText(),
                "clicking Log In with valid credentials must replace the login screen with the Dashboard");
    }

    @Test
    @Tag("negative")
    @DisplayName("A failed login shows the translated error message and stays on the Login screen")
    void failedLoginShowsErrorMessage() throws Exception {
        Mockito.when(api.login(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Simulated authentication failure"));

        clickOn(".login-button");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = lookup(".error-msg").query();
        assertEquals(I18n.t("login.error"), error.getText(),
                "a failed login must show the current-language error message, not navigate away");
    }
}
