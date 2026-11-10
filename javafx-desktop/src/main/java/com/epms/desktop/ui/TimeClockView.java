package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.util.List;
import java.util.function.Function;

public class TimeClockView {

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> employeeBox = new ComboBox<>();
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        try {
            employeeBox.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception ex) {
            error(ex);
        }

        Label statusLabel = new Label(I18n.t("timeclock.status.idle"));
        statusLabel.getStyleClass().add("field-caption");
        Button clockInBtn = UiKit.iconButton(I18n.t("timeclock.clockin"), Icons.CLOCK, "btn-primary");
        Button clockOutBtn = UiKit.iconButton(I18n.t("timeclock.clockout"), Icons.CLOCK, "btn-danger");
        clockInBtn.setDisable(true);
        clockOutBtn.setDisable(true);

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column(I18n.t("timeclock.col.date"), n -> text(n, "workDate")),
                column(I18n.t("timeclock.col.in"), n -> text(n, "clockIn")),
                column(I18n.t("timeclock.col.out"), n -> text(n, "clockOut")),
                column(I18n.t("timeclock.col.regular"), n -> text(n, "regularHours")),
                column(I18n.t("timeclock.col.overtime"), n -> text(n, "overtimeHours")),
                column(I18n.t("timeclock.col.status"), n -> text(n, "status")));
        table.setPrefHeight(300);

        long[] openEntryId = {-1};
        Runnable[] reloadHolder = new Runnable[1];
        reloadHolder[0] = () -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                List<JsonNode> entries = api.timeEntries(emp.id);
                table.setItems(FXCollections.observableArrayList(entries));
                JsonNode open = entries.stream().filter(n -> "OPEN".equals(text(n, "status"))).findFirst().orElse(null);
                if (open != null) {
                    openEntryId[0] = open.get("id").asLong();
                    statusLabel.setText(I18n.t("timeclock.status.open") + "  (" + text(open, "clockIn") + ")");
                    clockInBtn.setDisable(true);
                    clockOutBtn.setDisable(false);
                } else {
                    openEntryId[0] = -1;
                    statusLabel.setText(I18n.t("timeclock.status.idle"));
                    clockInBtn.setDisable(false);
                    clockOutBtn.setDisable(true);
                }
            } catch (Exception ex) {
                error(ex);
            }
        };

        employeeBox.setOnAction(e -> reloadHolder[0].run());

        clockInBtn.setOnAction(e -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                api.clockIn(emp.id);
                reloadHolder[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        clockOutBtn.setOnAction(e -> {
            if (openEntryId[0] < 0) return;
            try {
                api.clockOut(openEntryId[0]);
                reloadHolder[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("timeclock.title"), back);

        var empBox = UiKit.fieldBoxControl(I18n.t("timeclock.select"), Icons.PERSON, employeeBox);
        HBox selectorRow = new HBox(16, empBox, clockInBtn, clockOutBtn, statusLabel);
        selectorRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(empBox, Priority.ALWAYS);

        VBox selectorCard = new VBox(selectorRow);
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setPadding(new Insets(20));

        Label historyLabel = new Label(I18n.t("timeclock.history"));
        historyLabel.getStyleClass().add("section-header");

        VBox tableCard = new VBox(10, historyLabel, table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, selectorCard, tableCard);
        body.setPadding(new Insets(24));

        VBox content = new VBox(topBar, body);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1150, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("timeclock.title"));
        stage.show();
    }

    private TableColumn<JsonNode, String> column(String label, Function<JsonNode, String> extractor) {
        TableColumn<JsonNode, String> col = new TableColumn<>(label);
        col.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(extractor.apply(x.getValue())));
        return col;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return "-";
        return v.asText();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
