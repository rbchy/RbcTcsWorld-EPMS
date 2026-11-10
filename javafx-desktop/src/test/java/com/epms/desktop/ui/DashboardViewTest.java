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
 * Headless TestFX UI-automation coverage for DashboardView's navigation tiles — the screen's
 * only real responsibility, since Dashboard itself performs no I/O and has no form to validate.
 * That is also why this class, unlike the other screen tests, has no @Tag("negative") case:
 * there is no local failure mode to exercise here (a failure surfaces once a sub-screen tries
 * to load its own data, which that sub-screen's own test class already covers).
 *
 * Follows the same recipe as LoginViewTest: launch the real screen with a mocked ApiClient
 * (no real HTTP calls are made, and Dashboard.show() itself never touches the ApiClient), then
 * click a tile by its visible label the same way a person would, and assert the target screen's
 * top-bar title (UiKit.topBar uppercases it) to confirm the navigation actually happened.
 */
class DashboardViewTest extends ApplicationTest {

    private final ApiClient api = Mockito.mock(ApiClient.class);

    @Override
    public void start(Stage stage) throws Exception {
        // EmployeeView.show() calls api.employees(null) as soon as it loads, so every
        // navigation test below must have a safe default for it even though only the
        // "navigate to Employees" test actually reaches that call.
        Mockito.when(api.employees(Mockito.isNull())).thenReturn(java.util.List.of());
        new DashboardView().show(stage, api);
    }

    @Test
    @Tag("positive")
    @DisplayName("Clicking the Employees tile opens the Employees screen")
    void clickingEmployeesTileOpensEmployeeView() throws Exception {
        clickOn(I18n.t("menu.employees"));
        WaitForAsyncUtils.waitForFxEvents();

        Label title = lookup(".screen-topbar-title").query();
        assertEquals(I18n.t("menu.employees").toUpperCase(), title.getText(),
                "clicking the Employees tile must replace the Dashboard with the Employees screen");
    }

    @Test
    @Tag("positive")
    @DisplayName("Clicking the Payroll Calculator tile opens the Payroll screen")
    void clickingPayrollTileOpensPayrollView() throws Exception {
        clickOn(I18n.t("menu.payroll"));
        WaitForAsyncUtils.waitForFxEvents();

        Label title = lookup(".screen-topbar-title").query();
        assertEquals("PAYROLL CALCULATOR", title.getText(),
                "clicking the Payroll Calculator tile must replace the Dashboard with the Payroll screen");
    }

    @Test
    @Tag("positive")
    @DisplayName("Clicking the Time Clock tile opens the Time Clock screen, and Back returns to the Dashboard")
    void clickingTimeClockTileOpensTimeClockViewAndBackReturns() throws Exception {
        clickOn(I18n.t("menu.timeclock"));
        WaitForAsyncUtils.waitForFxEvents();

        Label title = lookup(".screen-topbar-title").query();
        assertEquals(I18n.t("timeclock.title").toUpperCase(), title.getText(),
                "clicking the Time Clock tile must replace the Dashboard with the Time Clock screen");

        clickOn(I18n.t("common.back"));
        WaitForAsyncUtils.waitForFxEvents();

        Label welcome = lookup(".title").query();
        assertEquals(I18n.t("dashboard.welcome"), welcome.getText(),
                "BACK from Time Clock must return to the Dashboard, not leave a stale screen showing");
    }
}
