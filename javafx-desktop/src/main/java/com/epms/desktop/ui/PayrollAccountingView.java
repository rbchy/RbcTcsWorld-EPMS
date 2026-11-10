package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Shows the automatically-posted general-ledger journal for saved payroll runs,
 * running account balances across the chart of accounts, and a YTD Social
 * Security wage-base summary for the currently filtered employee.
 */
public class PayrollAccountingView {

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> employeeBox = new ComboBox<>();
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        try {
            employeeBox.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception ex) {
            error(ex);
        }

        VBox ytdCard = new VBox(8);
        ytdCard.getStyleClass().add("stat-card");
        ytdCard.setPadding(new Insets(18));
        ytdCard.setPrefWidth(320);

        TableView<JsonNode> journalTable = new TableView<>();
        journalTable.getStyleClass().add("payslip-table");
        journalTable.getColumns().setAll(
                column(I18n.t("accounting.col.date"), n -> text(n, "entryDate")),
                column(I18n.t("accounting.col.employee"), this::employeeName),
                column(I18n.t("accounting.col.description"), n -> text(n, "description")),
                column(I18n.t("accounting.col.gross"), n -> "$" + text(payrollOf(n), "grossPay")),
                column(I18n.t("accounting.col.net"), n -> "$" + text(payrollOf(n), "netPay")));
        journalTable.setPrefHeight(280);

        VBox linesDetail = new VBox(8);
        linesDetail.getStyleClass().add("stat-card");
        linesDetail.setPadding(new Insets(18));
        linesDetail.setPrefWidth(320);

        journalTable.setOnMouseClicked(e -> {
            JsonNode selected = journalTable.getSelectionModel().getSelectedItem();
            if (selected != null) renderLines(linesDetail, selected);
        });

        TableView<JsonNode> balancesTable = new TableView<>();
        balancesTable.getStyleClass().add("payslip-table");
        balancesTable.getColumns().setAll(
                column(I18n.t("accounting.col.code"), n -> text(n, "code")),
                column(I18n.t("accounting.col.name"), n -> text(n, "name")),
                column(I18n.t("accounting.col.type"), n -> text(n, "type")),
                column(I18n.t("accounting.col.debit"), n -> "$" + text(n, "debitTotal")),
                column(I18n.t("accounting.col.credit"), n -> "$" + text(n, "creditTotal")),
                column(I18n.t("accounting.col.balance"), n -> "$" + text(n, "balance")));
        balancesTable.setPrefHeight(260);

        Runnable loadAll = () -> {
            try {
                List<JsonNode> entries = api.journalEntries(null);
                journalTable.setItems(FXCollections.observableArrayList(entries));
                linesDetail.getChildren().clear();
                ytdCard.getChildren().clear();
            } catch (Exception ex) {
                error(ex);
            }
        };

        employeeBox.setOnAction(e -> {
            Employee emp = employeeBox.getValue();
            if (emp == null) return;
            try {
                List<JsonNode> entries = api.journalEntries(emp.id);
                journalTable.setItems(FXCollections.observableArrayList(entries));
                linesDetail.getChildren().clear();
                renderYtd(ytdCard, api.ytdTax(emp.id, LocalDate.now().getYear()));
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button allBtn = UiKit.iconButton(I18n.t("accounting.all"), Icons.CLOSE, "btn-secondary");
        allBtn.setOnAction(e -> {
            employeeBox.getSelectionModel().clearSelection();
            loadAll.run();
        });

        Button refreshBtn = UiKit.iconButton(I18n.t("accounting.refresh"), Icons.CHART, "btn-primary");
        refreshBtn.setOnAction(e -> {
            try {
                balancesTable.setItems(FXCollections.observableArrayList(api.accountBalances()));
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("accounting.title"), back);

        var empBox = UiKit.fieldBoxControl(I18n.t("accounting.select"), Icons.PERSON, employeeBox);
        empBox.setMaxWidth(360);
        HBox selectorRow = new HBox(16, empBox, allBtn);
        selectorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        VBox selectorCard = new VBox(12, selectorRow, ytdCard);
        selectorCard.getStyleClass().add("form-card");
        selectorCard.setPadding(new Insets(20));

        Label journalHeader = new Label(I18n.t("accounting.journal"));
        journalHeader.getStyleClass().add("section-header");
        HBox journalRow = new HBox(20, journalTable, linesDetail);
        HBox.setHgrow(journalTable, Priority.ALWAYS);
        VBox journalCard = new VBox(10, journalHeader, journalRow);
        journalCard.getStyleClass().add("form-card");
        journalCard.setPadding(new Insets(20));

        HBox balancesHeaderRow = new HBox(14, new Label(I18n.t("accounting.balances")), refreshBtn);
        balancesHeaderRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        VBox balancesCard = new VBox(10, balancesHeaderRow, balancesTable);
        balancesCard.getStyleClass().add("form-card");
        balancesCard.setPadding(new Insets(20));

        VBox body = new VBox(20, selectorCard, journalCard, balancesCard);
        body.setPadding(new Insets(24));

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge");

        VBox content = new VBox(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1280, 840);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("accounting.title"));
        stage.show();

        loadAll.run();
        try {
            balancesTable.setItems(FXCollections.observableArrayList(api.accountBalances()));
        } catch (Exception ex) {
            error(ex);
        }
    }

    private JsonNode payrollOf(JsonNode entry) {
        JsonNode p = entry.get("payroll");
        return p == null ? entry : p;
    }

    private String employeeName(JsonNode n) {
        JsonNode emp = n.get("employee");
        if (emp == null || emp.isNull()) return "-";
        return text(emp, "firstName") + " " + text(emp, "lastName");
    }

    private void renderLines(VBox box, JsonNode entry) {
        box.getChildren().clear();
        Label header = new Label(I18n.t("accounting.detail.title"));
        header.getStyleClass().add("section-header");
        box.getChildren().add(header);
        JsonNode lines = entry.get("lines");
        if (lines != null) {
            for (JsonNode line : lines) {
                JsonNode acct = line.get("account");
                String label = (acct != null ? text(acct, "code") + " " + text(acct, "name") : "-");
                BigDecimal debit = new BigDecimal(text(line, "debit"));
                BigDecimal credit = new BigDecimal(text(line, "credit"));
                String amount = debit.signum() != 0 ? "Dr $" + debit : "Cr $" + credit;
                box.getChildren().add(new Label(label + "   —   " + amount));
            }
        }
    }

    private void renderYtd(VBox box, JsonNode s) {
        box.getChildren().clear();
        Label header = new Label(I18n.t("accounting.ytd.title"));
        header.getStyleClass().add("section-header");
        box.getChildren().add(header);
        box.getChildren().add(new Label(I18n.t("accounting.ytd.sswages") + ":  $" + text(s, "ytdSocialSecurityWages")));
        box.getChildren().add(new Label(I18n.t("accounting.ytd.ssroom") + ":  $" + text(s, "remainingSsWageRoom")));
        box.getChildren().add(new Label(I18n.t("accounting.ytd.medwages") + ":  $" + text(s, "ytdMedicareWages")));
        box.getChildren().add(new Label(I18n.t("accounting.ytd.fedwages") + ":  $" + text(s, "ytdFederalTaxableWages")));
        box.getChildren().add(new Label(I18n.t("accounting.ytd.gross") + ":  $" + text(s, "ytdGrossPay")));
        Label net = new Label(I18n.t("accounting.ytd.net") + ":  $" + text(s, "ytdNetPay"));
        net.getStyleClass().add("big-total");
        box.getChildren().add(net);
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
