package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * One screen shared by the Review / Approve / Process payroll menu items —
 * each is a step in the same CALCULATED -> REVIEWED -> APPROVED -> PROCESSED
 * workflow, filtered to the stage the payroll needs to be at before it can
 * move forward.
 */
public class PayrollWorkflowView {

    public void show(Stage stage, ApiClient api, String mode) {
        String title, fromStatus, toStatus, actionLabel, iconSvg;
        switch (mode) {
            case "APPROVE" -> { title = "Approve Payroll"; fromStatus = "REVIEWED"; toStatus = "APPROVED"; actionLabel = "APPROVE"; iconSvg = Icons.SAVE; }
            case "PROCESS" -> { title = "Process Payroll"; fromStatus = "APPROVED"; toStatus = "PROCESSED"; actionLabel = "MARK PROCESSED"; iconSvg = Icons.MONEY; }
            default -> { title = "Review Payroll"; fromStatus = "CALCULATED"; toStatus = "REVIEWED"; actionLabel = "MARK REVIEWED"; iconSvg = Icons.EDIT; }
        }

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column("ID", n -> text(n, "id")),
                column("Employee", this::employeeName),
                column("Period", n -> text(n, "periodBeginning") + "  →  " + text(n, "periodEnding")),
                column("Pay Date", n -> text(n, "payDate")),
                column("Gross", n -> "$" + text(n, "grossPay")),
                column("Net", n -> "$" + text(n, "netPay")),
                column("Status", n -> text(n, "status")));
        table.setPrefHeight(360);

        CheckBox showAll = new CheckBox("Show all statuses");

        Label emptyLabel = new Label("Nothing is currently waiting at the \"" + fromStatus + "\" stage.");
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        Button actionBtn = UiKit.iconButton(actionLabel, iconSvg, "btn-primary");
        actionBtn.setDisable(true);

        Button voidBtn = UiKit.iconButton("VOID", Icons.CLOSE, "btn-danger");
        voidBtn.setDisable(true);

        Button reverseBtn = UiKit.iconButton("REVERSE", Icons.EDIT, "btn-secondary");
        reverseBtn.setDisable(true);

        Runnable[] reload = new Runnable[1];
        reload[0] = () -> {
            try {
                List<JsonNode> all = api.allPayrolls();
                List<JsonNode> filtered = showAll.isSelected() ? all
                        : all.stream().filter(n -> fromStatus.equals(text(n, "status"))).toList();
                table.setItems(FXCollections.observableArrayList(filtered));
                boolean isEmpty = !showAll.isSelected() && filtered.isEmpty();
                emptyLabel.setVisible(isEmpty);
                emptyLabel.setManaged(isEmpty);
                actionBtn.setDisable(true);
                voidBtn.setDisable(true);
                reverseBtn.setDisable(true);
            } catch (Exception ex) {
                error(ex);
            }
        };

        table.setOnMouseClicked(e -> {
            JsonNode sel = table.getSelectionModel().getSelectedItem();
            actionBtn.setDisable(sel == null || !fromStatus.equals(text(sel, "status")));
            if (sel == null) {
                voidBtn.setDisable(true);
                reverseBtn.setDisable(true);
                return;
            }
            String status = text(sel, "status");
            voidBtn.setDisable(status.equals("PROCESSED") || status.equals("VOID") || status.equals("REVERSED"));
            reverseBtn.setDisable(!status.equals("PROCESSED"));
        });

        actionBtn.setOnAction(e -> {
            JsonNode sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            try {
                api.updatePayrollStatus(sel.get("id").asLong(), toStatus);
                reload[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        voidBtn.setOnAction(e -> {
            JsonNode sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Void Payroll #" + text(sel, "id"));
            dialog.setHeaderText("This marks the payroll VOID. It is never deleted — the record and its audit trail stay in the system.");
            dialog.setContentText("Reason:");
            Optional<String> reason = dialog.showAndWait();
            if (reason.isEmpty()) return;
            try {
                api.voidPayroll(sel.get("id").asLong(), reason.get());
                reload[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        reverseBtn.setOnAction(e -> {
            JsonNode sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reverse Payroll #" + text(sel, "id"));
            dialog.setHeaderText("This posts a mirror-image journal entry to zero out this payroll's ledger impact. The original record and its journal entry are kept, unchanged, for audit purposes.");
            dialog.setContentText("Reason:");
            Optional<String> reason = dialog.showAndWait();
            if (reason.isEmpty()) return;
            try {
                api.reversePayroll(sel.get("id").asLong(), reason.get());
                reload[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        showAll.setOnAction(e -> reload[0].run());

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(title, back);

        Label stageLabel = new Label("Stage: " + fromStatus + "  →  " + toStatus);
        stageLabel.getStyleClass().add("field-caption");

        HBox controlsRow = new HBox(16, stageLabel, showAll, actionBtn, voidBtn, reverseBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(12, controlsRow, table, emptyLabel);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + title);
        stage.show();

        reload[0].run();
    }

    private String employeeName(JsonNode n) {
        JsonNode emp = n.get("employee");
        if (emp == null || emp.isNull()) return "-";
        return text(emp, "firstName") + " " + text(emp, "lastName");
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
