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

public class PayslipsView {

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> employeeBox = new ComboBox<>();
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        try {
            employeeBox.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception ex) {
            error(ex);
        }

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column(I18n.t("payslips.col.period"), n -> text(n, "periodBeginning") + "  →  " + text(n, "periodEnding")),
                column(I18n.t("payslips.col.paydate"), n -> text(n, "payDate")),
                column(I18n.t("payslips.col.gross"), n -> "$" + text(n, "grossPay")),
                column(I18n.t("payslips.col.net"), n -> "$" + text(n, "netPay")));
        table.setPrefHeight(320);

        Label emptyLabel = new Label(I18n.t("payslips.empty"));
        emptyLabel.setWrapText(true);
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        VBox detail = new VBox(8);
        detail.getStyleClass().add("stat-card");
        detail.setPadding(new Insets(18));
        detail.setPrefWidth(320);

        table.setOnMouseClicked(e -> {
            JsonNode selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) renderDetail(detail, selected);
        });

        employeeBox.setOnAction(e -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                List<JsonNode> history = api.payrollHistory(emp.id);
                table.setItems(FXCollections.observableArrayList(history));
                boolean isEmpty = history.isEmpty();
                emptyLabel.setVisible(isEmpty);
                emptyLabel.setManaged(isEmpty);
                detail.getChildren().clear();
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("payslips.title"), back);

        var empBox = UiKit.fieldBoxControl(I18n.t("payslips.select"), Icons.PERSON, employeeBox);
        empBox.setMaxWidth(420);
        VBox selectorCard = new VBox(8, empBox, emptyLabel);
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setPadding(new Insets(20));

        HBox content2 = new HBox(20, table, detail);
        HBox.setHgrow(table, Priority.ALWAYS);

        VBox tableCard = new VBox(content2);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, selectorCard, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("payslips.title"));
        stage.show();
    }

    private void renderDetail(VBox detail, JsonNode p) {
        detail.getChildren().clear();
        Label header = new Label(I18n.t("payslips.detail.title"));
        header.getStyleClass().add("section-header");
        detail.getChildren().add(header);
        addRow(detail, "payslips.detail.gross", text(p, "grossPay"));
        addRow(detail, "payslips.detail.federal", text(p, "federalTax"));
        addRow(detail, "payslips.detail.ss", text(p, "socialSecurityTax"));
        addRow(detail, "payslips.detail.medicare", text(p, "medicareTax"));
        addRow(detail, "payslips.detail.pa", text(p, "paTax"));
        addRow(detail, "payslips.detail.local", text(p, "localTax"));
        addRow(detail, "payslips.detail.lst", text(p, "lst"));
        addRow(detail, "payslips.detail.sui", text(p, "paSui"));
        addRow(detail, "payslips.detail.health", text(p, "health"));
        addRow(detail, "payslips.detail.k401", text(p, "traditional401k"));
        addRow(detail, "payslips.detail.roth", text(p, "roth"));
        addRow(detail, "payslips.detail.match", text(p, "employerMatch"));
        Label net = new Label(I18n.t("payslips.detail.net") + ":  $" + text(p, "netPay"));
        net.getStyleClass().add("big-total");
        detail.getChildren().add(net);
    }

    private void addRow(VBox box, String labelKey, String value) {
        box.getChildren().add(new Label(I18n.t(labelKey) + ":  $" + value));
    }

    private TableColumn<JsonNode, String> column(String label, Function<JsonNode, String> extractor) {
        TableColumn<JsonNode, String> col = new TableColumn<>(label);
        col.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(extractor.apply(x.getValue())));
        return col;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return "0";
        return v.asText();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
