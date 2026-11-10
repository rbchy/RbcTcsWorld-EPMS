package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Function;

/** Every saved payslip across every employee, in one company-wide table. */
public class PayslipReportView {

    public void show(Stage stage, ApiClient api) {
        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column("Employee", this::employeeName),
                column("Period", n -> text(n, "periodBeginning") + "  →  " + text(n, "periodEnding")),
                column("Pay Date", n -> text(n, "payDate")),
                column("Gross", n -> "$" + text(n, "grossPay")),
                column("Net", n -> "$" + text(n, "netPay")),
                column("Status", n -> text(n, "status")));
        table.setPrefHeight(460);

        try {
            List<JsonNode> rows = api.allPayrolls();
            table.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception ex) {
            error(ex);
        }

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Payslip Report", back);

        VBox tableCard = new VBox(table);
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
        stage.setTitle("RbcTcsWorld - Payslip Report");
        stage.show();
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
