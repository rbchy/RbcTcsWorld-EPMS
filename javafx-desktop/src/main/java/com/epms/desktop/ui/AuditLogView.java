package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Function;

/** Read-only view of the audit trail — every sensitive change, who made it, and why. Nothing here can be edited or deleted. */
public class AuditLogView {

    public void show(Stage stage, ApiClient api) {
        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column("When", n -> text(n, "performedAt")),
                column("Entity", n -> text(n, "entityType") + " #" + text(n, "entityId")),
                column("Action", n -> text(n, "action")),
                column("Field", n -> text(n, "fieldName")),
                column("Old Value", n -> text(n, "oldValue")),
                column("New Value", n -> text(n, "newValue")),
                column("Performed By", n -> text(n, "performedBy")),
                column("Reason", n -> text(n, "reason")));
        table.setPrefHeight(520);

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList("All", "EMPLOYEE", "PAYROLL"));
        filter.setValue("All");

        Runnable[] reload = new Runnable[1];
        reload[0] = () -> {
            try {
                List<JsonNode> all = api.auditAll();
                String f = filter.getValue();
                List<JsonNode> shown = "All".equals(f) ? all
                        : all.stream().filter(n -> f.equals(text(n, "entityType"))).toList();
                table.setItems(FXCollections.observableArrayList(shown));
            } catch (Exception ex) {
                error(ex);
            }
        };
        filter.setOnAction(e -> reload[0].run());

        Button refreshBtn = UiKit.iconButton("REFRESH", Icons.CHART, "btn-secondary");
        refreshBtn.setOnAction(e -> reload[0].run());

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Audit Log", back);

        var filterBox = UiKit.fieldBoxControl("ENTITY TYPE", Icons.SEARCH, filter);
        filterBox.setMaxWidth(220);
        HBox controlsRow = new HBox(16, filterBox, refreshBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(12, controlsRow, table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1360, 840);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Audit Log");
        stage.show();

        reload[0].run();
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
