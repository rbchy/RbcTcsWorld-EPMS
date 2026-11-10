package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayrollView {

    private final DatePicker periodBeginning = new DatePicker(LocalDate.now().minusDays(6));
    private final DatePicker periodEnding = new DatePicker(LocalDate.now());
    private final DatePicker payDate = new DatePicker(LocalDate.now().plusDays(5));

    public void show(Stage stage, ApiClient api) {
        ComboBox<Employee> emp = new ComboBox<>();
        emp.setMaxWidth(Double.MAX_VALUE);
        try {
            emp.setItems(FXCollections.observableArrayList(api.employees(null)));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }

        TextField reg = new TextField("40"), dt = new TextField("8"), ex1h = new TextField("40"), ex1r = new TextField("3"),
                ex2h = new TextField("8"), ex2r = new TextField("6"), fed = new TextField("4.1143"), ss = new TextField("6.2"),
                med = new TextField("1.45"), pa = new TextField("3.07"), local = new TextField("1.10"), lst = new TextField("1"),
                sui = new TextField("0.0747"), match = new TextField("60");
        TextField[] fields = {reg, dt, ex1h, ex1r, ex2h, ex2r, fed, ss, med, pa, local, lst, sui, match};
        String[] labels = {"Regular Hours", "Doubletime Hours", "Extra Hours 1", "Extra Rate 1", "Extra Hours 2", "Extra Rate 2",
                "Federal Rate %", "SS Rate %", "Medicare Rate %", "PA Rate %", "Local Rate %", "LST", "SUI Rate %", "Employer Match %"};

        Label grossVal = statValue("--"), netVal = statValue("--"), baseVal = statValue("--");
        VBox grossCard = statCard("GROSS PAY", grossVal);
        VBox baseCard = statCard("FEDERAL TAXABLE", baseVal);
        VBox netCard = statCard("NET PAY", netVal);

        Button calc = UiKit.iconButton("CALCULATE", Icons.CALCULATOR, "btn-primary");
        Button save = UiKit.iconButton("SAVE PAYSLIP", Icons.SAVE, "btn-secondary");
        save.setDisable(true);

        calc.setOnAction(e -> {
            Map<String, Object> payload = buildPayload(emp, fields, labels);
            if (payload == null) return; // validation already showed the Alert
            try {
                JsonNode n = api.calculate(payload);
                grossVal.setText("$" + n.get("grossPay").asText());
                baseVal.setText("$" + n.get("federalTaxableWages").asText());
                netVal.setText("$" + n.get("netPay").asText());
                save.setDisable(false);
            } catch (Exception ex) {
                save.setDisable(true);
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        save.setOnAction(e -> {
            Map<String, Object> payload = buildPayload(emp, fields, labels);
            if (payload == null) return; // validation already showed the Alert
            try {
                api.savePayroll(payload);
                new Alert(Alert.AlertType.INFORMATION,
                        "Payslip saved. You can find it under Payslips for this employee.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Payroll Calculator", back);

        var empBox = UiKit.fieldBoxControl("EMPLOYEE", Icons.PERSON, emp);
        var periodBeginningBox = UiKit.fieldBoxControl("PAY PERIOD FROM", Icons.CLOCK, periodBeginning);
        var periodEndingBox = UiKit.fieldBoxControl("PAY PERIOD TO", Icons.CLOCK, periodEnding);
        var payDateBox = UiKit.fieldBoxControl("PAY DATE", Icons.CLOCK, payDate);

        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(20);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            dateGrid.getColumnConstraints().add(cc);
        }
        dateGrid.add(periodBeginningBox, 0, 0);
        dateGrid.add(periodEndingBox, 1, 0);
        dateGrid.add(payDateBox, 2, 0);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(18);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            grid.getColumnConstraints().add(cc);
        }
        String[][] cells = {
                {"REGULAR HOURS", Icons.CLOCK}, {"DOUBLETIME HOURS", Icons.CLOCK}, {"EXTRA HOURS 1", Icons.CLOCK},
                {"EXTRA RATE 1", Icons.MONEY}, {"EXTRA HOURS 2", Icons.CLOCK}, {"EXTRA RATE 2", Icons.MONEY},
                {"FEDERAL RATE %", Icons.MONEY}, {"SS RATE %", Icons.MONEY}, {"MEDICARE RATE %", Icons.MONEY},
                {"PA RATE %", Icons.MONEY}, {"LOCAL RATE %", Icons.MONEY}, {"LST", Icons.MONEY},
                {"SUI RATE %", Icons.MONEY}, {"EMPLOYER MATCH %", Icons.MONEY},
        };
        for (int i = 0; i < cells.length; i++) {
            grid.add(UiKit.fieldBox(cells[i][0], cells[i][1], fields[i]), i % 3, i / 3);
        }

        VBox formCard = new VBox(16, empBox, dateGrid, grid);
        formCard.getStyleClass().add("form-card");
        formCard.setPadding(new Insets(24));

        HBox resultRow = new HBox(16, grossCard, baseCard, netCard);
        HBox.setHgrow(grossCard, Priority.ALWAYS);
        HBox.setHgrow(baseCard, Priority.ALWAYS);
        HBox.setHgrow(netCard, Priority.ALWAYS);

        HBox buttons = new HBox(12, calc, save);

        VBox body = new VBox(20, formCard, resultRow, buttons);
        body.setPadding(new Insets(24));

        ScrollPane scroller = new ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("edge-to-edge");

        BorderPane content = new BorderPane();
        content.setTop(topBar);
        content.setCenter(scroller);

        Scene s = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1150, 820);
        s.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(s);
        stage.setTitle("RbcTcsWorld - Payroll Calculator");
        stage.show();
    }

    private Label statValue(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("big-total");
        return l;
    }

    private VBox statCard(String caption, Label valueLabel) {
        Label cap = new Label(caption);
        cap.getStyleClass().add("field-caption");
        VBox card = new VBox(6, valueLabel, cap);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(18));
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    /** Builds the calculate/save payload, or returns null (after showing one combined Alert) when validation fails. */
    private Map<String, Object> buildPayload(ComboBox<Employee> emp, TextField[] fields, String[] labels) {
        List<String> errors = new ArrayList<>();

        Employee x = emp.getValue();
        if (x == null) errors.add("Select an Employee.");

        LocalDate beginV = periodBeginning.getValue();
        LocalDate endV = periodEnding.getValue();
        LocalDate payV = payDate.getValue();
        if (beginV == null) errors.add("Pay Period From is required.");
        if (endV == null) errors.add("Pay Period To is required.");
        if (payV == null) errors.add("Pay Date is required.");
        if (beginV != null && endV != null && endV.isBefore(beginV)) errors.add("Pay Period To cannot be before Pay Period From.");

        TextField reg = fields[0], dt = fields[1], ex1h = fields[2], ex1r = fields[3], ex2h = fields[4],
                ex2r = fields[5], fed = fields[6], ss = fields[7], med = fields[8], pa = fields[9],
                local = fields[10], lst = fields[11], sui = fields[12], match = fields[13];

        BigDecimal regV = parseDecimal(labels[0], reg.getText(), true, errors);
        BigDecimal dtV = parseDecimal(labels[1], dt.getText(), false, errors);
        BigDecimal ex1hV = parseDecimal(labels[2], ex1h.getText(), false, errors);
        BigDecimal ex1rV = parseDecimal(labels[3], ex1r.getText(), false, errors);
        BigDecimal ex2hV = parseDecimal(labels[4], ex2h.getText(), false, errors);
        BigDecimal ex2rV = parseDecimal(labels[5], ex2r.getText(), false, errors);
        BigDecimal fedV = parseDecimal(labels[6], fed.getText(), false, errors);
        BigDecimal ssV = parseDecimal(labels[7], ss.getText(), false, errors);
        BigDecimal medV = parseDecimal(labels[8], med.getText(), false, errors);
        BigDecimal paV = parseDecimal(labels[9], pa.getText(), false, errors);
        BigDecimal localV = parseDecimal(labels[10], local.getText(), false, errors);
        BigDecimal lstV = parseDecimal(labels[11], lst.getText(), false, errors);
        BigDecimal suiV = parseDecimal(labels[12], sui.getText(), false, errors);
        BigDecimal matchV = parseDecimal(labels[13], match.getText(), false, errors);

        if (!errors.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, String.join("\n", errors)).showAndWait();
            return null;
        }

        Map<String, Object> p = new HashMap<>();
        p.put("employeeId", x.id);
        p.put("periodBeginning", beginV.toString());
        p.put("periodEnding", endV.toString());
        p.put("payDate", payV.toString());
        p.put("regularHours", regV.toString());
        p.put("doubletimeHours", dtV.toString());
        p.put("extraHours1", ex1hV.toString());
        p.put("extraHours2", ex2hV.toString());
        p.put("extraRate1", ex1rV.toString());
        p.put("extraRate2", ex2rV.toString());
        p.put("federalRate", fedV.toString());
        p.put("socialSecurityRate", ssV.toString());
        p.put("medicareRate", medV.toString());
        p.put("paRate", paV.toString());
        p.put("localRate", localV.toString());
        p.put("lst", lstV.toString());
        p.put("paSuiRate", suiV.toString());
        p.put("employerMatchRate", matchV.toString());
        return p;
    }

    private static BigDecimal parseDecimal(String label, String raw, boolean required, List<String> errors) {
        String v = raw == null ? "" : raw.trim();
        if (v.isEmpty()) {
            if (required) errors.add(label + " is required.");
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal d = new BigDecimal(v);
            if (d.signum() < 0) errors.add(label + " cannot be negative.");
            return d;
        } catch (NumberFormatException ex) {
            errors.add(label + " must be a valid number.");
            return BigDecimal.ZERO;
        }
    }
}
