package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Function;

/** One employee's full history in one place: every saved payroll plus every time-clock entry. */
public class EmployeeHistoryView {

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> employeeBox = new ComboBox<>();
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        try {
            employeeBox.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception ex) {
            error(ex);
        }

        TableView<JsonNode> payrollTable = new TableView<>();
        payrollTable.getStyleClass().add("payslip-table");
        payrollTable.getColumns().setAll(
                column("Period", n -> text(n, "periodBeginning") + "  →  " + text(n, "periodEnding")),
                column("Pay Date", n -> text(n, "payDate")),
                column("Gross", n -> "$" + text(n, "grossPay")),
                column("Net", n -> "$" + text(n, "netPay")),
                column("Status", n -> text(n, "status")));
        payrollTable.setPrefHeight(260);

        TableView<JsonNode> timeTable = new TableView<>();
        timeTable.getStyleClass().add("payslip-table");
        timeTable.getColumns().setAll(
                column("Date", n -> text(n, "workDate")),
                column("Clock In", n -> text(n, "clockIn")),
                column("Clock Out", n -> text(n, "clockOut")),
                column("Regular Hrs", n -> text(n, "regularHours")),
                column("Overtime Hrs", n -> text(n, "overtimeHours")),
                column("Status", n -> text(n, "status")));
        timeTable.setPrefHeight(260);

        employeeBox.setOnAction(e -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                payrollTable.setItems(FXCollections.observableArrayList(api.payrollHistory(emp.id)));
                timeTable.setItems(FXCollections.observableArrayList(api.timeEntries(emp.id)));
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Employee History", back);

        var empBox = UiKit.fieldBoxControl("SELECT EMPLOYEE", Icons.PERSON, employeeBox);
        empBox.setMaxWidth(420);
        VBox selectorCard = new VBox(empBox);
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setPadding(new Insets(20));

        Label payrollLabel = new Label("Payroll History");
        payrollLabel.getStyleClass().add("section-header");
        VBox payrollCard = new VBox(10, payrollLabel, payrollTable);
        payrollCard.getStyleClass().add("form-card");
        payrollCard.setPadding(new Insets(20));

        Label timeLabel = new Label("Time Clock History");
        timeLabel.getStyleClass().add("section-header");
        VBox timeCard = new VBox(10, timeLabel, timeTable);
        timeCard.getStyleClass().add("form-card");
        timeCard.setPadding(new Insets(20));

        VBox body = new VBox(20, selectorCard, payrollCard, timeCard);
        body.setPadding(new Insets(24));

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(body);
        scroll.setFitToWidth(true);

        VBox content = new VBox(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1280, 840);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Employee History");
        stage.show();
    }

    private TableColumn<JsonNode, String> column(String label, Function<JsonNode, String> extractor) {
        TableColumn<JsonNode, String> col = new TableColumn<>(label);
        col.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(extractor.apply(x.getValue())));
        return col;
    }

    private static String text(JsonNode n, String field) {
        if (n == null) return "-";
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return "-";
        return v.asText();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
