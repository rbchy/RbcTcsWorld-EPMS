package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Total regular vs. overtime hours clocked per employee, from every time-clock entry on file. */
public class HoursReportView {

    private record HoursRow(String employee, BigDecimal regular, BigDecimal overtime, long entries) {}

    public void show(Stage stage, ApiClient api) {
        Map<String, String> nameById = new LinkedHashMap<>();
        Map<String, HoursRow> totals = new LinkedHashMap<>();
        try {
            for (Employee e : api.employees(null)) {
                nameById.put(String.valueOf(e.id), e.firstName + " " + e.lastName);
            }
            for (JsonNode t : api.allTimeEntries()) {
                JsonNode empNode = t.get("employee");
                String id = empNode != null && !empNode.isNull() ? text(empNode, "id") : "-";
                String name = nameById.getOrDefault(id, id);
                HoursRow row = totals.getOrDefault(name, new HoursRow(name, BigDecimal.ZERO, BigDecimal.ZERO, 0));
                totals.put(name, new HoursRow(name,
                        row.regular().add(dec(t, "regularHours")),
                        row.overtime().add(dec(t, "overtimeHours")),
                        row.entries() + 1));
            }
        } catch (Exception ex) {
            error(ex);
        }

        TableView<HoursRow> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        TableColumn<HoursRow, String> c1 = new TableColumn<>("Employee");
        c1.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().employee()));
        TableColumn<HoursRow, String> c2 = new TableColumn<>("Regular Hours");
        c2.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(String.valueOf(x.getValue().regular())));
        TableColumn<HoursRow, String> c3 = new TableColumn<>("Overtime Hours");
        c3.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(String.valueOf(x.getValue().overtime())));
        TableColumn<HoursRow, String> c4 = new TableColumn<>("Time Entries");
        c4.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(String.valueOf(x.getValue().entries())));
        table.getColumns().setAll(c1, c2, c3, c4);
        table.setItems(FXCollections.observableArrayList(totals.values()));
        table.setPrefHeight(420);

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Hours Report", back);

        VBox tableCard = new VBox(table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1150, 780);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Hours Report");
        stage.show();
    }

    private static BigDecimal dec(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(v.asText());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
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
