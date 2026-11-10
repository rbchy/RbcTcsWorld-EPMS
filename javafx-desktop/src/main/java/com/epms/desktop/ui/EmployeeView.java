package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.model.Employee;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeView {

    // Same legal values the backend's demo-data seeder uses, so every dropdown choice here
    // is guaranteed to be a value the backend already understands.
    private static final String[] DEPARTMENTS = {"Packaging", "Production", "Quality Assurance", "Warehouse", "Maintenance", "Administration"};
    private static final String[] JOB_TITLES = {"Packaging Operator", "Line Leader", "QA Inspector", "Production Supervisor",
            "Machine Operator", "Warehouse Associate", "Maintenance Technician", "Shift Supervisor", "Material Handler", "Quality Technician"};
    private static final String[] STATES = {"PA", "NJ", "NY", "OH", "MD", "DE", "VA", "WV", "CT", "MA"};
    private static final String[] EMPLOYMENT_STATUSES = {"ACTIVE", "ON_LEAVE", "INACTIVE", "TERMINATED"};
    private static final String[] PAY_TYPES = {"HOURLY", "SALARY"};
    private static final String[] PAY_FREQUENCIES = {"WEEKLY", "BIWEEKLY", "SEMIMONTHLY", "MONTHLY"};
    private static final String[] FILING_STATUSES = {"SINGLE", "MARRIED_FILING_JOINTLY", "MARRIED_FILING_SEPARATELY", "HEAD_OF_HOUSEHOLD"};

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern ZIP_PATTERN = Pattern.compile("^\\d{5}(-\\d{4})?$");

    private final TextField num = uppercaseField("EMP-1001");
    private final TextField first = new TextField(), middle = new TextField(), last = new TextField();
    private final TextField email = new TextField(), phone = new TextField();
    private final TextField address = new TextField(), city = new TextField();
    private final ComboBox<String> state = comboOf(STATES);
    private final TextField zip = new TextField();
    private final ComboBox<String> department = comboOf(DEPARTMENTS);
    private final ComboBox<String> jobTitle = comboOf(JOB_TITLES);
    private final DatePicker hireDate = new DatePicker();
    private final ComboBox<String> employmentStatus = comboOf(EMPLOYMENT_STATUSES);
    private final ComboBox<String> payType = comboOf(PAY_TYPES);
    private final TextField rate = new TextField();
    private final ComboBox<String> payFrequency = comboOf(PAY_FREQUENCIES);
    private final ComboBox<String> filingStatus = comboOf(FILING_STATUSES);
    private final TextField k401 = new TextField(), roth = new TextField(), catchUp401k = new TextField(), catchUpRoth = new TextField();
    private final TextField health = new TextField();

    private final TextField search = new TextField();
    private final TableView<Employee> table = new TableView<>();
    private boolean currentActive = true; // preserves the loaded row's Active flag across an Update (owned by Employees > Status)

    public void show(Stage stage, ApiClient api) {
        first.setPromptText("First name");
        middle.setPromptText("Middle name (optional)");
        last.setPromptText("Last name");
        email.setPromptText("name@example.com");
        phone.setPromptText("(555) 555-1234");
        address.setPromptText("Street address");
        city.setPromptText("City");
        zip.setPromptText("17101");
        rate.setPromptText("0.00");
        k401.setPromptText("0.0");
        roth.setPromptText("0.0");
        catchUp401k.setPromptText("0.0");
        catchUpRoth.setPromptText("0.0");
        health.setPromptText("0.00");
        hireDate.setPromptText("YYYY-MM-DD");
        search.setPromptText("Search by name or employee number...");

        TableColumn<Employee, String> c1 = new TableColumn<>("Employee #");
        c1.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().employeeNumber));
        TableColumn<Employee, String> c2 = new TableColumn<>("Name");
        c2.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().firstName + " " + x.getValue().lastName));
        TableColumn<Employee, String> c3 = new TableColumn<>("Department");
        c3.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(orBlank(x.getValue().department)));
        TableColumn<Employee, String> c4 = new TableColumn<>("Job Title");
        c4.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(orBlank(x.getValue().jobTitle)));
        TableColumn<Employee, String> c5 = new TableColumn<>("Status");
        c5.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(orBlank(x.getValue().employmentStatus)));
        TableColumn<Employee, String> c6 = new TableColumn<>("State");
        c6.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(orBlank(x.getValue().state)));
        TableColumn<Employee, String> c7 = new TableColumn<>("Rate");
        c7.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(String.valueOf(x.getValue().hourlyRate)));
        table.getColumns().setAll(c1, c2, c3, c4, c5, c6, c7);
        table.getStyleClass().add("employee-table");
        table.setPrefHeight(220);
        table.setOnMouseClicked(e -> {
            Employee x = table.getSelectionModel().getSelectedItem();
            if (x != null) fill(x);
        });
        load(api, null);

        Button searchBtn = UiKit.iconButton("SEARCH", Icons.SEARCH, "btn-secondary");
        searchBtn.setOnAction(e -> load(api, search.getText()));

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Employees", back);

        var searchBox = UiKit.fieldBox("SEARCH", Icons.SEARCH, search);
        HBox searchRow = new HBox(12, searchBox, searchBtn);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(table);
        tableCard.getStyleClass().add("form-card");
        tableCard.setPadding(new Insets(16));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(18);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            grid.getColumnConstraints().add(cc);
        }

        Object[][] cells = {
                {"EMPLOYEE #", Icons.DOCUMENT, num}, {"FIRST NAME", Icons.PERSON, first}, {"MIDDLE NAME", Icons.PERSON, middle},
                {"LAST NAME", Icons.PERSON, last}, {"EMAIL", Icons.DOCUMENT, email}, {"PHONE", Icons.DOCUMENT, phone},
                {"ADDRESS", Icons.BUILDING, address}, {"CITY", Icons.BUILDING, city}, {"STATE", Icons.BUILDING, state},
                {"ZIP CODE", Icons.BUILDING, zip}, {"DEPARTMENT", Icons.BUILDING, department}, {"JOB TITLE", Icons.WORK, jobTitle},
                {"HIRE DATE", Icons.CLOCK, hireDate}, {"EMPLOYMENT STATUS", Icons.BADGE, employmentStatus}, {"PAY TYPE", Icons.MONEY, payType},
                {"HOURLY RATE", Icons.MONEY, rate}, {"PAY FREQUENCY", Icons.CLOCK, payFrequency}, {"FILING STATUS", Icons.DOCUMENT, filingStatus},
                {"401K %", Icons.MONEY, k401}, {"ROTH %", Icons.MONEY, roth}, {"401K CATCH-UP %", Icons.MONEY, catchUp401k},
                {"ROTH CATCH-UP %", Icons.MONEY, catchUpRoth}, {"HEALTH DEDUCTION", Icons.HEART, health},
        };
        for (int i = 0; i < cells.length; i++) {
            String label = (String) cells[i][0];
            String icon = (String) cells[i][1];
            javafx.scene.Node control = (javafx.scene.Node) cells[i][2];
            VBox box = control instanceof TextField
                    ? UiKit.fieldBox(label, icon, (TextField) control)
                    : UiKit.fieldBoxControl(label, icon, control);
            grid.add(box, i % 3, i / 3);
        }

        VBox formCard = new VBox(grid);
        formCard.getStyleClass().add("form-card");
        formCard.setPadding(new Insets(24));

        Button add = UiKit.iconButton("ADD / SAVE", Icons.PLUS, "btn-primary");
        Button upd = UiKit.iconButton("UPDATE", Icons.EDIT, "btn-secondary");
        Button del = UiKit.iconButton("DELETE", Icons.DELETE, "btn-danger");
        Button clear = UiKit.iconButton("CLEAR", Icons.CLOSE, "btn-secondary");
        add.setOnAction(e -> save(api, false));
        upd.setOnAction(e -> save(api, true));
        del.setOnAction(e -> {
            Employee x = table.getSelectionModel().getSelectedItem();
            if (x != null) {
                try {
                    api.delete(x.id);
                    load(api, null);
                    clear();
                } catch (Exception ex) {
                    error(ex);
                }
            } else {
                error(new RuntimeException("Select an employee first."));
            }
        });
        clear.setOnAction(e -> clear());

        HBox buttons = new HBox(12, add, upd, del, clear);

        VBox body = new VBox(20, searchRow, tableCard, formCard, buttons);
        body.setPadding(new Insets(24));

        ScrollPane scroller = new ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("edge-to-edge");

        BorderPane content = new BorderPane();
        content.setTop(topBar);
        content.setCenter(scroller);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1250, 820);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - Employees");
        stage.show();
    }

    private void load(ApiClient api, String q) {
        try {
            table.setItems(FXCollections.observableArrayList(api.employees(q)));
        } catch (Exception e) {
            error(e);
        }
    }

    private void fill(Employee x) {
        num.setText(orBlank(x.employeeNumber));
        first.setText(orBlank(x.firstName));
        middle.setText(orBlank(x.middleName));
        last.setText(orBlank(x.lastName));
        email.setText(orBlank(x.email));
        phone.setText(orBlank(x.phone));
        address.setText(orBlank(x.address));
        city.setText(orBlank(x.city));
        state.setValue(x.state);
        zip.setText(orBlank(x.zipCode));
        department.setValue(x.department);
        jobTitle.setValue(x.jobTitle);
        hireDate.setValue(x.hireDate);
        employmentStatus.setValue(x.employmentStatus == null ? "ACTIVE" : x.employmentStatus);
        payType.setValue(x.payType == null ? "HOURLY" : x.payType);
        rate.setText(x.hourlyRate == null ? "" : x.hourlyRate.toString());
        payFrequency.setValue(x.payFrequency == null ? "WEEKLY" : x.payFrequency);
        filingStatus.setValue(x.filingStatus == null ? "MARRIED_FILING_JOINTLY" : x.filingStatus);
        k401.setText(x.traditional401kPct == null ? "" : x.traditional401kPct.toString());
        roth.setText(x.rothPct == null ? "" : x.rothPct.toString());
        catchUp401k.setText(x.traditionalCatchUpPct == null ? "" : x.traditionalCatchUpPct.toString());
        catchUpRoth.setText(x.rothCatchUpPct == null ? "" : x.rothCatchUpPct.toString());
        health.setText(x.healthDeduction == null ? "" : x.healthDeduction.toString());
        currentActive = x.active;
    }

    /** Builds the Employee to submit, or returns null (after showing one combined Alert) when validation fails. */
    private Employee form() {
        List<String> errors = new ArrayList<>();

        String numV = num.getText() == null ? "" : num.getText().trim();
        String firstV = first.getText() == null ? "" : first.getText().trim();
        String lastV = last.getText() == null ? "" : last.getText().trim();
        LocalDate hireDateV = hireDate.getValue();

        if (numV.isEmpty()) errors.add("Employee # is required.");
        if (firstV.isEmpty()) errors.add("First Name is required.");
        if (lastV.isEmpty()) errors.add("Last Name is required.");
        if (hireDateV == null) errors.add("Hire Date is required.");

        String emailV = email.getText() == null ? "" : email.getText().trim();
        if (!emailV.isEmpty() && !EMAIL_PATTERN.matcher(emailV).matches()) errors.add("Email is not a valid email address.");

        String zipV = zip.getText() == null ? "" : zip.getText().trim();
        if (!zipV.isEmpty() && !ZIP_PATTERN.matcher(zipV).matches()) errors.add("Zip Code must look like 17101 or 17101-1234.");

        BigDecimal rateV = parseDecimal("Hourly Rate", rate.getText(), true, errors);
        BigDecimal healthV = parseDecimal("Health Deduction", health.getText(), false, errors);
        BigDecimal k401V = parsePercent("401K %", k401.getText(), errors);
        BigDecimal rothV = parsePercent("Roth %", roth.getText(), errors);
        BigDecimal catchUp401kV = parsePercent("401K Catch-Up %", catchUp401k.getText(), errors);
        BigDecimal catchUpRothV = parsePercent("Roth Catch-Up %", catchUpRoth.getText(), errors);

        if (!errors.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, String.join("\n", errors)).showAndWait();
            return null;
        }

        Employee e = new Employee();
        e.employeeNumber = numV;
        e.firstName = firstV;
        e.middleName = middle.getText() == null || middle.getText().isBlank() ? null : middle.getText().trim();
        e.lastName = lastV;
        e.email = emailV.isEmpty() ? null : emailV;
        e.phone = blankToNull(phone.getText());
        e.address = blankToNull(address.getText());
        e.city = blankToNull(city.getText());
        e.state = state.getValue();
        e.zipCode = zipV.isEmpty() ? null : zipV;
        e.department = department.getValue();
        e.jobTitle = jobTitle.getValue();
        e.hireDate = hireDateV;
        e.employmentStatus = employmentStatus.getValue() == null ? "ACTIVE" : employmentStatus.getValue();
        e.payType = payType.getValue() == null ? "HOURLY" : payType.getValue();
        e.hourlyRate = rateV;
        e.payFrequency = payFrequency.getValue() == null ? "WEEKLY" : payFrequency.getValue();
        e.filingStatus = filingStatus.getValue() == null ? "MARRIED_FILING_JOINTLY" : filingStatus.getValue();
        e.traditional401kPct = k401V;
        e.rothPct = rothV;
        e.traditionalCatchUpPct = catchUp401kV;
        e.rothCatchUpPct = catchUpRothV;
        e.healthDeduction = healthV;
        e.active = currentActive; // Active/Inactive is owned by Employees > Status, never edited here
        return e;
    }

    private void save(ApiClient api, boolean update) {
        Employee e = form();
        if (e == null) return; // validation already showed the Alert
        try {
            if (update) {
                Employee old = table.getSelectionModel().getSelectedItem();
                if (old == null) {
                    error(new RuntimeException("Select an employee first."));
                    return;
                }
                api.update(old.id, e);
            } else {
                currentActive = true;
                e.active = true;
                api.save(e);
            }
            load(api, null);
            clear();
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void clear() {
        num.clear();
        first.clear();
        middle.clear();
        last.clear();
        email.clear();
        phone.clear();
        address.clear();
        city.clear();
        state.setValue(null);
        zip.clear();
        department.setValue(null);
        jobTitle.setValue(null);
        hireDate.setValue(null);
        employmentStatus.getSelectionModel().selectFirst();
        payType.getSelectionModel().selectFirst();
        rate.clear();
        payFrequency.getSelectionModel().selectFirst();
        filingStatus.getSelectionModel().selectFirst();
        k401.clear();
        roth.clear();
        catchUp401k.clear();
        catchUpRoth.clear();
        health.clear();
        currentActive = true;
        table.getSelectionModel().clearSelection();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }

    private static TextField uppercaseField(String promptText) {
        TextField f = new TextField();
        f.setPromptText(promptText);
        f.setTextFormatter(new TextFormatter<String>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        return f;
    }

    private static ComboBox<String> comboOf(String[] values) {
        ComboBox<String> box = new ComboBox<>(FXCollections.observableArrayList(values));
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static String orBlank(String s) {
        return s == null ? "" : s;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
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

    private static BigDecimal parsePercent(String label, String raw, List<String> errors) {
        BigDecimal d = parseDecimal(label, raw, false, errors);
        if (d.compareTo(BigDecimal.valueOf(100)) > 0) errors.add(label + " cannot exceed 100.");
        return d;
    }
}
