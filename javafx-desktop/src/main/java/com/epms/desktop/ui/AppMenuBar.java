package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 * The nested dropdown navigation bar shown across the top of the Dashboard —
 * Payroll / Employees / Reports, each opening a menu of the screens and
 * reports underneath it.
 */
public final class AppMenuBar {
    private AppMenuBar() {}

    public static MenuBar build(Stage stage, ApiClient api) {
        MenuBar bar = new MenuBar();
        bar.getStyleClass().add("app-menubar");

        Menu payroll = new Menu(I18n.t("menubar.payroll") + "  ▼");
        payroll.getItems().addAll(
                item(I18n.t("menubar.payroll.new"), () -> new PayrollView().show(stage, api)),
                item(I18n.t("menubar.payroll.calculate"), () -> new PayrollView().show(stage, api)),
                new SeparatorMenuItem(),
                item(I18n.t("menubar.payroll.review"), () -> new PayrollWorkflowView().show(stage, api, "REVIEW")),
                item(I18n.t("menubar.payroll.approve"), () -> new PayrollWorkflowView().show(stage, api, "APPROVE")),
                item(I18n.t("menubar.payroll.process"), () -> new PayrollWorkflowView().show(stage, api, "PROCESS")),
                new SeparatorMenuItem(),
                item(I18n.t("menubar.payroll.history"), () -> new PayslipsView().show(stage, api)));

        Menu employees = new Menu(I18n.t("menubar.employees") + "  ▼");
        employees.getItems().addAll(
                item(I18n.t("menubar.employees.add"), () -> new EmployeeView().show(stage, api)),
                item(I18n.t("menubar.employees.list"), () -> new EmployeeView().show(stage, api)),
                item(I18n.t("menubar.employees.search"), () -> new EmployeeView().show(stage, api)),
                item(I18n.t("menubar.employees.edit"), () -> new EmployeeView().show(stage, api)),
                new SeparatorMenuItem(),
                item(I18n.t("menubar.employees.status"), () -> new EmployeeStatusView().show(stage, api)),
                item(I18n.t("menubar.employees.history"), () -> new EmployeeHistoryView().show(stage, api)));

        Menu reports = new Menu(I18n.t("menubar.reports") + "  ▼");
        reports.getItems().addAll(
                item(I18n.t("menubar.reports.payroll"), () -> new PayrollReportView().show(stage, api)),
                item(I18n.t("menubar.reports.employee"), () -> new ReportsView().show(stage, api)),
                item(I18n.t("menubar.reports.tax"), () -> new YtdReportView().show(stage, api, "TAX")),
                item(I18n.t("menubar.reports.deduction"), () -> new DeductionReportView().show(stage, api)),
                item(I18n.t("menubar.reports.hours"), () -> new HoursReportView().show(stage, api)),
                item(I18n.t("menubar.reports.ytd"), () -> new YtdReportView().show(stage, api, "YTD")),
                item(I18n.t("menubar.reports.payslip"), () -> new PayslipReportView().show(stage, api)),
                new SeparatorMenuItem(),
                item(I18n.t("menubar.reports.audit"), () -> new AuditLogView().show(stage, api)));

        bar.getMenus().addAll(payroll, employees, reports);
        return bar;
    }

    private static MenuItem item(String label, Runnable action) {
        MenuItem mi = new MenuItem(label);
        mi.setOnAction(e -> action.run());
        return mi;
    }
}
