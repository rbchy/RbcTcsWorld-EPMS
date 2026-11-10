package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless TestFX UI-automation coverage for EmployeeView, following the same recipe as
 * LoginViewTest: launch the real screen with a mocked ApiClient, drive it with TestFX's robot.
 *
 * EmployeeView has no unique fx:id / CSS id on its 20+ form fields (they all share the
 * "field-input" style class from UiKit.fieldBox), so instead of relying on scene-graph order
 * — which TestFX's Set-based queryAll() does not guarantee — these tests locate the handful of
 * fields they need by their (unique) prompt text via a predicate lookup, the same way a real
 * person would recognize a field by its placeholder.
 */
class EmployeeViewTest extends ApplicationTest {

    private final ApiClient api = Mockito.mock(ApiClient.class);

    @Override
    public void start(Stage stage) throws Exception {
        Mockito.when(api.employees(Mockito.isNull())).thenReturn(List.of(sample("EMP-2001", "Priya", "Rao")));
        new EmployeeView().show(stage, api);
    }

    private static Employee sample(String number, String first, String last) {
        Employee e = new Employee();
        e.id = 2001L;
        e.employeeNumber = number;
        e.firstName = first;
        e.lastName = last;
        e.hireDate = LocalDate.of(2023, 6, 1);
        return e;
    }

    private <T extends Node> T byPromptText(Class<T> type, String prompt) {
        Predicate<Node> matches = n -> type.isInstance(n) && promptTextOf(n).equals(prompt);
        return type.cast(lookup(matches).query());
    }

    private static String promptTextOf(Node n) {
        if (n instanceof TextField tf) return String.valueOf(tf.getPromptText());
        if (n instanceof DatePicker dp) return String.valueOf(dp.getPromptText());
        return "";
    }

    // ADD / SAVE sits below a long form in a ScrollPane, same as PayrollView -- it starts
    // scrolled out of the viewport (a real UI fact, not a headless-rendering artifact), so
    // scroll to the bottom first, the same way a real user would, before clicking it.
    private void scrollButtonsIntoView() {
        ScrollPane scroller = (ScrollPane) lookup(".edge-to-edge").query();
        interact(() -> scroller.setVvalue(1.0));
        WaitForAsyncUtils.waitForFxEvents();
    }

    // Several TextFields share the same placeholder text (Hourly Rate and Health Deduction
    // both prompt "0.00"), so byPromptText() isn't safe for those -- locate the field by its
    // (unique) field-caption Label instead, the same way a real user reads the form.
    private TextField byCaption(String captionText) {
        Label caption = (Label) lookup((Node n) -> n instanceof Label
                && captionText.equals(((Label) n).getText())).query();
        return (TextField) caption.getParent().lookup(".field-input");
    }

    @Test
    @Tag("positive")
    @DisplayName("The employee table is populated from the API as soon as the screen loads")
    void initialLoadPopulatesTheTableFromTheApi() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();

        @SuppressWarnings("unchecked")
        TableView<Employee> table = (TableView<Employee>) lookup(".employee-table").query();
        assertEquals(1, table.getItems().size(), "the one employee returned by the API must appear in the table");
        assertEquals("EMP-2001", table.getItems().get(0).employeeNumber);
    }

    @Test
    @Tag("positive")
    @DisplayName("Filling Employee #, First Name, Last Name, Hire Date and Hourly Rate and clicking ADD / SAVE calls the API")
    void fillingTheRequiredFieldsAndSavingCallsApiSave() throws Exception {
        TextField num = byPromptText(TextField.class, "EMP-1001");
        TextField first = byPromptText(TextField.class, "First name");
        TextField last = byPromptText(TextField.class, "Last name");
        DatePicker hireDate = lookup((Node n) -> n instanceof DatePicker).query();

        clickOn(num).write("EMP-3001");
        clickOn(first).write("Nasrin");
        clickOn(last).write("Akter");
        interact(() -> hireDate.setValue(LocalDate.of(2024, 3, 1)));

        // Hourly Rate is also required by EmployeeView.form() validation, even though it isn't
        // named in this test's @DisplayName -- leaving it blank made an earlier version of this
        // test's ADD/SAVE click silently fail validation instead of reaching api.save(). It sits
        // several rows below Employee #/First/Last (row 5 of 8 in the field grid) and is still
        // scrolled out of the ScrollPane's viewport at this point -- exactly like hireDate above,
        // set it directly via interact() rather than clickOn()+write(), which needs the field to
        // actually be visible on screen right now.
        interact(() -> byCaption("HOURLY RATE").setText("22.50"));

        Mockito.when(api.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        scrollButtonsIntoView();
        clickOn(".btn-primary"); // ADD / SAVE is the only "btn-primary" button on this screen
        WaitForAsyncUtils.waitForFxEvents();

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(api).save(captor.capture());
        assertEquals("EMP-3001", captor.getValue().employeeNumber);
        assertEquals("Nasrin", captor.getValue().firstName);
        assertEquals("Akter", captor.getValue().lastName);
    }

    @Test
    @Tag("negative")
    @DisplayName("Clicking ADD / SAVE with the required fields blank shows a validation Alert and never calls the API")
    void savingWithRequiredFieldsBlankShowsAValidationAlertAndNeverCallsApi() throws Exception {
        int windowsBefore = listWindows().size();

        scrollButtonsIntoView();
        clickOn(".btn-primary"); // ADD / SAVE with an empty form
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > windowsBefore,
                "a validation Alert window must appear when Employee #, First/Last Name or Hire Date is blank");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(api, Mockito.never()).save(Mockito.any());
    }
}
