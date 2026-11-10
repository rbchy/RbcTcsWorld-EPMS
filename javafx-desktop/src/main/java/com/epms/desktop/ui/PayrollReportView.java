package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

/** Company-wide payroll totals — every saved payroll run added up in one place. */
public class PayrollReportView {

    public void show(Stage stage, ApiClient api) {
        List<JsonNode> rows;
        try {
            rows = api.allPayrolls();
        } catch (Exception ex) {
            error(ex);
            rows = List.of();
        }

        BigDecimal gross = BigDecimal.ZERO, net = BigDecimal.ZERO, federal = BigDecimal.ZERO,
                ss = BigDecimal.ZERO, medicare = BigDecimal.ZERO, state = BigDecimal.ZERO, local = BigDecimal.ZERO;
        for (JsonNode n : rows) {
            gross = gross.add(dec(n, "grossPay"));
            net = net.add(dec(n, "netPay"));
            federal = federal.add(dec(n, "federalTax"));
            ss = ss.add(dec(n, "socialSecurityTax"));
            medicare = medicare.add(dec(n, "medicareTax"));
            state = state.add(dec(n, "paTax"));
            local = local.add(dec(n, "localTax"));
        }

        HBox row1 = new HBox(16,
                statCard("PAYROLL RUNS", String.valueOf(rows.size())),
                statCard("TOTAL GROSS PAY", "$" + gross),
                statCard("TOTAL NET PAY", "$" + net));
        for (var n : row1.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        HBox row2 = new HBox(16,
                statCard("FEDERAL TAX", "$" + federal),
                statCard("SOCIAL SECURITY", "$" + ss),
                statCard("MEDICARE", "$" + medicare),
                statCard("STATE + LOCAL TAX", "$" + state.add(local)));
        for (var n : row2.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Payroll Report", back);

        VBox body = new VBox(20, row1, row2);
        body.setPadding(new Insets(24));

        VBox content = new VBox(topBar, body);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1150, 640);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Payroll Report");
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

    private static BigDecimal dec(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(v.asText());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
