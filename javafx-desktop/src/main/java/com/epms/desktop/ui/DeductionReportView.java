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
import java.util.List;
import java.util.Map;

/** Total 401(k), Roth and health-insurance deductions withheld per employee, across every saved payroll. */
public class DeductionReportView {

    private record DeductionRow(String employee, BigDecimal health, BigDecimal k401, BigDecimal roth, BigDecimal catchUps) {}

    public void show(Stage stage, ApiClient api) {
        Map<String, String> nameById = new LinkedHashMap<>();
        Map<String, DeductionRow> totals = new LinkedHashMap<>();
        try {
            for (Employee e : api.employees(null)) {
                nameById.put(String.valueOf(e.id), e.firstName + " " + e.lastName);
            }
            for (JsonNode p : api.allPayrolls()) {
                JsonNode empNode = p.get("employee");
                String id = empNode != null && !empNode.isNull() ? text(empNode, "id") : "-";
                String name = nameById.getOrDefault(id, id);
                DeductionRow row = totals.getOrDefault(name, new DeductionRow(name, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                BigDecimal catchUps = dec(p, "traditionalCatchUp").add(dec(p, "rothCatchUp"));
                totals.put(name, new DeductionRow(name,
                        row.health().add(dec(p, "health")),
                        row.k401().add(dec(p, "traditional401k")),
                        row.roth().add(dec(p, "roth")),
                        row.catchUps().add(catchUps)));
            }
        } catch (Exception ex) {
            error(ex);
        }

        TableView<DeductionRow> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        TableColumn<DeductionRow, String> c1 = new TableColumn<>("Employee");
        c1.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().employee()));
        TableColumn<DeductionRow, String> c2 = new TableColumn<>("Health Insurance");
        c2.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty("$" + x.getValue().health()));
        TableColumn<DeductionRow, String> c3 = new TableColumn<>("401(k)");
        c3.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty("$" + x.getValue().k401()));
        TableColumn<DeductionRow, String> c4 = new TableColumn<>("Roth");
        c4.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty("$" + x.getValue().roth()));
        TableColumn<DeductionRow, String> c5 = new TableColumn<>("Catch-Up (401k+Roth)");
        c5.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty("$" + x.getValue().catchUps()));
        table.getColumns().setAll(c1, c2, c3, c4, c5);
        table.setItems(FXCollections.observableArrayList(totals.values()));
        table.setPrefHeight(420);

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Deduction Report", back);

        VBox tableCard = new VBox(table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1280, 780);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Deduction Report");
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
