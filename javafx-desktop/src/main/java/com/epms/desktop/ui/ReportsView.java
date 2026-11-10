package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import com.epms.desktop.model.Employee;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportsView {

    public void show(Stage stage, ApiClient api) {
        List<Employee> employees;
        try {
            employees = api.employees(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            employees = List.of();
        }

        int total = employees.size();
        long active = employees.stream().filter(e -> e.active).count();
        long inactive = total - active;
        BigDecimal avgRate = BigDecimal.ZERO;
        if (total > 0) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Employee e : employees) sum = sum.add(e.hourlyRate == null ? BigDecimal.ZERO : e.hourlyRate);
            avgRate = sum.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        }

        HBox statRow = new HBox(16,
                statCard(I18n.t("reports.headcount"), String.valueOf(total)),
                statCard(I18n.t("reports.active"), String.valueOf(active)),
                statCard(I18n.t("reports.inactive"), String.valueOf(inactive)),
                statCard(I18n.t("reports.avgrate"), "$" + avgRate));
        for (var node : statRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        Map<String, Integer> byDept = new LinkedHashMap<>();
        for (Employee e : employees) {
            String dept = (e.department == null || e.department.isBlank()) ? "-" : e.department;
            byDept.merge(dept, 1, Integer::sum);
        }

        TableView<Map.Entry<String, Integer>> deptTable = new TableView<>();
        deptTable.getStyleClass().add("payslip-table");
        TableColumn<Map.Entry<String, Integer>, String> colDept = new TableColumn<>(I18n.t("reports.col.dept"));
        colDept.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().getKey()));
        TableColumn<Map.Entry<String, Integer>, String> colCount = new TableColumn<>(I18n.t("reports.col.count"));
        colCount.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(String.valueOf(x.getValue().getValue())));
        deptTable.getColumns().setAll(colDept, colCount);
        deptTable.setItems(FXCollections.observableArrayList(byDept.entrySet()));
        deptTable.setPrefHeight(280);

        Label deptLabel = new Label(I18n.t("reports.bydept"));
        deptLabel.getStyleClass().add("section-header");

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("reports.title"), back);

        VBox deptCard = new VBox(10, deptLabel, deptTable);
        deptCard.getStyleClass().add("form-card");
        deptCard.setPadding(new Insets(20));

        VBox body = new VBox(20, statRow, deptCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(deptCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1150, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("reports.title"));
        stage.show();
    }

    private VBox statCard(String label, String value) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("big-total");
        Label captionLabel = new Label(label);
        captionLabel.getStyleClass().add("field-caption");
        VBox card = new VBox(6, valueLabel, captionLabel);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(18));
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }
}
