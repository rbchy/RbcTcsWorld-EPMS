package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import com.epms.desktop.model.Employee;
import javafx.application.Platform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless TestFX UI-automation coverage for TimeClockView, following the same recipe as
 * LoginViewTest: launch the real screen with a mocked ApiClient, drive it with TestFX's robot.
 *
 * The status Label's own Node reference is captured once, right after the screen loads, by
 * matching its initial ("Not clocked in") text — its text is mutated in place as the screen
 * reacts to API responses, so re-reading label.getText() later reflects the live UI state
 * without needing a second, harder-to-target lookup after that text has changed.
 */
class TimeClockViewTest extends ApplicationTest {

    private final ApiClient api = Mockito.mock(ApiClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Employee idleEmployee;

    @Override
    public void start(Stage stage) throws Exception {
        idleEmployee = new Employee();
        idleEmployee.id = 42L;
        idleEmployee.employeeNumber = "TC-9001";
        idleEmployee.firstName = "Time";
        idleEmployee.lastName = "Fixture";

        Employee errorEmployee = new Employee();
        errorEmployee.id = 77L;
        errorEmployee.employeeNumber = "TC-9002";
        errorEmployee.firstName = "Broken";
        errorEmployee.lastName = "Fixture";

        Mockito.when(api.employees(Mockito.isNull())).thenReturn(List.of(idleEmployee, errorEmployee));
        Mockito.when(api.timeEntries(42L)).thenReturn(List.of());
        Mockito.when(api.timeEntries(77L)).thenThrow(new RuntimeException("Server unavailable"));

        new TimeClockView().show(stage, api);
    }

    @SuppressWarnings("unchecked")
    private ComboBox<Employee> employeeCombo() {
        return (ComboBox<Employee>) lookup((Node n) -> n instanceof ComboBox).query();
    }

    @Test
    @Tag("positive")
    @DisplayName("Selecting an idle employee then clicking CLOCK IN calls the API and flips the status to clocked-in")
    void clockInCallsApiAndUpdatesStatus() throws Exception {
        JsonNode openEntry = objectMapper.readTree(
                "{\"id\":900,\"workDate\":\"2026-09-12\",\"clockIn\":\"09:00\",\"clockOut\":null,"
                        + "\"regularHours\":0,\"overtimeHours\":0,\"status\":\"OPEN\"}");
        Mockito.when(api.clockIn(42L)).thenReturn(openEntry);
        // First reload (right after selecting the employee) finds no entries; the second
        // reload (triggered after CLOCK IN succeeds) finds the entry clockIn() just opened.
        Mockito.when(api.timeEntries(42L)).thenReturn(List.of(), List.of(openEntry));

        interact(() -> employeeCombo().getSelectionModel().select(idleEmployee));
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup((Node n) -> n instanceof Label
                && I18n.t("timeclock.status.idle").equals(((Label) n).getText())).query();
        assertEquals(I18n.t("timeclock.status.idle"), statusLabel.getText());

        clickOn(I18n.t("timeclock.clockin"));
        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(api).clockIn(42L);
        assertTrue(statusLabel.getText().startsWith(I18n.t("timeclock.status.open")),
                "after a successful CLOCK IN the status label must switch to the clocked-in message");
    }

    @Test
    @Tag("negative")
    @DisplayName("Selecting an employee whose history the API fails to load shows an error Alert")
    void selectingAnEmployeeWhoseHistoryFailsToLoadShowsAnErrorAlert() throws Exception {
        int windowsBefore = listWindows().size();

        // Selecting this employee makes the reload handler call api.timeEntries(), which throws
        // and is caught by showing an Alert.showAndWait() *synchronously*, inside the very FX-thread
        // callback that the selection change triggers. interact(Runnable) blocks this test thread on
        // a Future that only completes once that whole callback returns -- but the callback can't
        // return until the Alert is dismissed, and only this test thread can dismiss it (via the
        // clickOn("OK") below). Selecting through interact() here would deadlock both threads
        // forever (this is exactly what was hanging the build), so the selection is posted
        // fire-and-forget via Platform.runLater instead, and we poll for the resulting Alert
        // window rather than block on the selection's own completion.
        Platform.runLater(() -> employeeCombo().getSelectionModel().select(1)); // the second combo item: errorEmployee
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> listWindows().size() > windowsBefore);

        assertTrue(listWindows().size() > windowsBefore,
                "an error Alert must appear when the API fails to load this employee's time entries");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();
    }
}
