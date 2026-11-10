package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Shared by the "Tax Report" and "YTD Report" menu items — both read the same
 * year-to-date tax/wage summary computed by TaxAccountingService for every
 * employee, so one table covers both views.
 */
public class YtdReportView {

    public void show(Stage stage, ApiClient api, String mode) {
        String title = "TAX".equals(mode) ? "Tax Report" : "YTD Report";

        Map<String, String> nameById = new HashMap<>();
        try {
            for (Employee e : api.employees(null)) nameById.put(String.valueOf(e.id), e.firstName + " " + e.lastName);
        } catch (Exception ex) {
            error(ex);
        }

        TextField yearField = new TextField(String.valueOf(LocalDate.now().getYear()));

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass().add("payslip-table");
        table.getColumns().setAll(
                column("Employee", n -> nameById.getOrDefault(text(n, "employeeId"), text(n, "employeeId"))),
                column("YTD Gross", n -> "$" + text(n, "ytdGrossPay")),
                column("YTD Net", n -> "$" + text(n, "ytdNetPay")),
                column("Federal Taxable", n -> "$" + text(n, "ytdFederalTaxableWages")),
                column("Federal Tax", n -> "$" + text(n, "ytdFederalTax")),
                column("SS Wages", n -> "$" + text(n, "ytdSocialSecurityWages")),
                column("SS Tax", n -> "$" + text(n, "ytdSocialSecurityTax")),
                column("SS Wage Room Left", n -> "$" + text(n, "remainingSsWageRoom")),
                column("Medicare Wages", n -> "$" + text(n, "ytdMedicareWages")),
                column("Medicare Tax", n -> "$" + text(n, "ytdMedicareTax")));
        table.setPrefHeight(420);

        Button loadBtn = UiKit.iconButton("LOAD", Icons.CHART, "btn-primary");
        loadBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                List<JsonNode> rows = api.ytdAll(year);
                table.setItems(FXCollections.observableArrayList(rows));
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(title, back);

        var yearBox = UiKit.fieldBox("TAX YEAR", Icons.DOCUMENT, yearField);
        yearBox.setMaxWidth(160);
        HBox controlsRow = new HBox(16, yearBox, loadBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(12, controlsRow, table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(20));

        VBox body = new VBox(20, tableCard);
        body.setPadding(new Insets(24));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox content = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1360, 820);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + title);
        stage.show();

        loadBtn.fire();
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
