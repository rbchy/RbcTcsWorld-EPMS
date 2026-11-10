package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless TestFX UI-automation coverage for PayrollView, following the same recipe as
 * LoginViewTest: launch the real screen with a mocked ApiClient, drive it with TestFX's robot.
 *
 * Every numeric field on this screen (hours, rates, ...) already comes pre-filled with a
 * working default value — the only thing genuinely required before CALCULATE will submit is
 * an Employee selected in the combo box, so these tests exercise exactly that instead of also
 * re-typing 14 rate/hours fields that already have valid values.
 */
class PayrollViewTest extends ApplicationTest {

    private final ApiClient api = Mockito.mock(ApiClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Stage stage) throws Exception {
        Employee employee = new Employee();
        employee.id = 501L;
        employee.employeeNumber = "PR-9001";
        employee.firstName = "Payroll";
        employee.lastName = "Fixture";
        Mockito.when(api.employees(Mockito.isNull())).thenReturn(List.of(employee));
        new PayrollView().show(stage, api);
    }

    @SuppressWarnings("unchecked")
    private ComboBox<Employee> employeeCombo() {
        // PayrollView has exactly one ComboBox on screen — the employee selector.
        return (ComboBox<Employee>) lookup((Node n) -> n instanceof ComboBox).query();
    }

    // The form + stat cards above CALCULATE/SAVE are taller than the screen, so both
    // buttons start scrolled out of the ScrollPane's viewport (a real UI fact, not a
    // headless-rendering artifact) — TestFX's robot correctly refuses to click a node
    // that isn't actually visible. Scroll the pane to the bottom first, the same way a
    // real user would, before targeting either button.
    private void scrollButtonsIntoView() {
        ScrollPane scroller = (ScrollPane) lookup(".edge-to-edge").query();
        interact(() -> scroller.setVvalue(1.0));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Tag("positive")
    @DisplayName("Calculating with an employee selected enables SAVE PAYSLIP once the API returns a result")
    void calculateWithASelectedEmployeeEnablesSave() throws Exception {
        interact(() -> employeeCombo().getSelectionModel().selectFirst());

        JsonNode result = objectMapper.readTree("{\"grossPay\":800.00,\"federalTaxableWages\":800.00,\"netPay\":658.80}");
        Mockito.when(api.calculate(Mockito.anyMap())).thenReturn(result);

        Button saveBtn = lookup(".btn-secondary").query(); // SAVE PAYSLIP is the only "btn-secondary" button here
        assertTrue(saveBtn.isDisabled(), "SAVE PAYSLIP must start disabled, before any successful calculation");

        scrollButtonsIntoView();
        clickOn(".btn-primary"); // CALCULATE
        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(api).calculate(Mockito.anyMap());
        assertFalse(saveBtn.isDisabled(), "a successful CALCULATE must enable SAVE PAYSLIP");
    }

    @Test
    @Tag("negative")
    @DisplayName("Calculating with no employee selected shows a validation Alert and never calls the API")
    void calculatingWithNoEmployeeSelectedShowsAValidationAlertAndNeverCallsApi() throws Exception {
        int windowsBefore = listWindows().size();

        scrollButtonsIntoView();
        clickOn(".btn-primary"); // CALCULATE with no employee chosen
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > windowsBefore, "a validation Alert window must appear when no Employee is selected");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(api, Mockito.never()).calculate(Mockito.anyMap());
    }
}
