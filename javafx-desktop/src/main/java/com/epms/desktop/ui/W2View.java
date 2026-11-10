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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/** Builds an employee's annual W-2 Wage and Tax Statement from saved payroll history. */
public class W2View {

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> employeeBox = new ComboBox<>();
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        try {
            employeeBox.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception ex) {
            error(ex);
        }

        TextField yearField = new TextField(String.valueOf(LocalDate.now().getYear()));

        VBox detail = new VBox(8);
        detail.getStyleClass().add("stat-card");
        detail.setPadding(new Insets(18));
        detail.setPrefWidth(420);

        Label emptyLabel = new Label(I18n.t("w2.empty"));
        emptyLabel.setWrapText(true);
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        TableView<JsonNode> allTable = new TableView<>();
        allTable.getStyleClass().add("payslip-table");
        allTable.getColumns().setAll(
                column(I18n.t("w2.col.employee"), n -> text(n, "employeeName")),
                column(I18n.t("w2.col.box1"), n -> "$" + text(n, "box1Wages")),
                column(I18n.t("w2.col.box2"), n -> "$" + text(n, "box2FederalTax")),
                column(I18n.t("w2.col.box3"), n -> "$" + text(n, "box3SsWages")),
                column(I18n.t("w2.col.box4"), n -> "$" + text(n, "box4SsTax")),
                column(I18n.t("w2.col.box5"), n -> "$" + text(n, "box5MedicareWages")),
                column(I18n.t("w2.col.box6"), n -> "$" + text(n, "box6MedicareTax")));
        allTable.setPrefHeight(300);

        Button generateBtn = UiKit.iconButton(I18n.t("w2.generate"), Icons.BADGE, "btn-primary");
        generateBtn.setOnAction(e -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                int year = parseYear(yearField.getText());
                JsonNode w2 = api.w2(emp.id, year);
                boolean isEmpty = w2.get("payrollCount") == null || w2.get("payrollCount").asDouble() == 0;
                emptyLabel.setVisible(isEmpty);
                emptyLabel.setManaged(isEmpty);
                renderDetail(detail, w2);
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button allYearBtn = UiKit.iconButton(I18n.t("w2.allyear"), Icons.LEDGER, "btn-secondary");
        allYearBtn.setOnAction(e -> {
            try {
                int year = parseYear(yearField.getText());
                allTable.setItems(FXCollections.observableArrayList(api.w2ByYear(year)));
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("w2.title"), back);

        var empBox = UiKit.fieldBoxControl(I18n.t("w2.select"), Icons.PERSON, employeeBox);
        empBox.setMaxWidth(320);
        var yearBox = UiKit.fieldBox(I18n.t("w2.year"), Icons.DOCUMENT, yearField);
        yearBox.setMaxWidth(160);

        HBox selectorRow = new HBox(16, empBox, yearBox, generateBtn, allYearBtn);
        selectorRow.setAlignment(Pos.CENTER_LEFT);
        VBox selectorCard = new VBox(12, selectorRow, emptyLabel);
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setPadding(new Insets(20));

        HBox detailRow = new HBox(20, detail);
        VBox detailCard = new VBox(10, detailRow);
        detailCard.getStyleClass().add("form-card");
        detailCard.setPadding(new Insets(20));

        Label allHeader = new Label(I18n.t("w2.allyear"));
        allHeader.getStyleClass().add("section-header");
        VBox allCard = new VBox(10, allHeader, allTable);
        allCard.getStyleClass().add("form-card");
        allCard.setPadding(new Insets(20));

        VBox body = new VBox(20, selectorCard, detailCard, allCard);
        body.setPadding(new Insets(24));

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(body);
        scroll.setFitToWidth(true);

        VBox content = new VBox(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1280, 840);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("w2.title"));
        stage.show();
    }

    private int parseYear(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return LocalDate.now().getYear();
        }
    }

    private void renderDetail(VBox box, JsonNode w2) {
        box.getChildren().clear();
        Label header = new Label(I18n.t("w2.detail.title") + "  —  " + text(w2, "employeeName") + "  (" + text(w2, "year") + ")");
        header.getStyleClass().add("section-header");
        box.getChildren().add(header);
        box.getChildren().add(new Label(I18n.t("w2.box1") + ":  $" + text(w2, "box1Wages")));
        box.getChildren().add(new Label(I18n.t("w2.box2") + ":  $" + text(w2, "box2FederalTax")));
        box.getChildren().add(new Label(I18n.t("w2.box3") + ":  $" + text(w2, "box3SsWages")));
        box.getChildren().add(new Label(I18n.t("w2.box4") + ":  $" + text(w2, "box4SsTax")));
        box.getChildren().add(new Label(I18n.t("w2.box5") + ":  $" + text(w2, "box5MedicareWages")));
        box.getChildren().add(new Label(I18n.t("w2.box6") + ":  $" + text(w2, "box6MedicareTax")));
        box.getChildren().add(new Label(I18n.t("w2.box16") + ":  $" + text(w2, "box16StateWages")));
        box.getChildren().add(new Label(I18n.t("w2.box17") + ":  $" + text(w2, "box17StateTax")));
        box.getChildren().add(new Label(I18n.t("w2.box18") + ":  $" + text(w2, "box18LocalWages")));
        box.getChildren().add(new Label(I18n.t("w2.box19") + ":  $" + text(w2, "box19LocalTax")));
        box.getChildren().add(new Label(I18n.t("w2.box12d") + ":  $" + text(w2, "box12dTraditional401k")));
        Label last = new Label(I18n.t("w2.box12aa") + ":  $" + text(w2, "box12aaRoth"));
        last.getStyleClass().add("big-total");
        box.getChildren().add(last);
    }

    private TableColumn<JsonNode, String> column(String label, Function<JsonNode, String> extractor) {
        TableColumn<JsonNode, String> col = new TableColumn<>(label);
        col.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(extractor.apply(x.getValue())));
        return col;
    }

    private static String text(JsonNode n, String field) {
        if (n == null) return "0";
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return "0";
        return v.asText();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
