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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/** Quick view of every employee's active/inactive status with a one-click toggle. */
public class EmployeeStatusView {

    public void show(Stage stage, ApiClient api) {
        TableView<Employee> table = new TableView<>();
        table.getStyleClass().add("employee-table");
        TableColumn<Employee, String> c1 = new TableColumn<>("Employee #");
        c1.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().employeeNumber));
        TableColumn<Employee, String> c2 = new TableColumn<>("Name");
        c2.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().firstName + " " + x.getValue().lastName));
        TableColumn<Employee, String> c3 = new TableColumn<>("Department");
        c3.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().department == null ? "-" : x.getValue().department));
        TableColumn<Employee, String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().active ? "ACTIVE" : "INACTIVE"));
        table.getColumns().setAll(c1, c2, c3, c4);
        table.setPrefHeight(420);

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList("All", "Active", "Inactive"));
        filter.setValue("All");

        Button toggleBtn = UiKit.iconButton("TOGGLE ACTIVE / INACTIVE", Icons.EDIT, "btn-primary");
        toggleBtn.setDisable(true);

        List<Employee>[] cacheHolder = new List[1];

        Runnable[] apply = new Runnable[1];
        apply[0] = () -> {
            if (cacheHolder[0] == null) return;
            String f = filter.getValue();
            List<Employee> shown = cacheHolder[0].stream()
                    .filter(x -> f.equals("All") || (f.equals("Active") && x.active) || (f.equals("Inactive") && !x.active))
                    .toList();
            table.setItems(FXCollections.observableArrayList(shown));
            toggleBtn.setDisable(true);
        };

        Runnable[] reload = new Runnable[1];
        reload[0] = () -> {
            try {
                cacheHolder[0] = api.employees(null);
                apply[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        };

        filter.setOnAction(e -> apply[0].run());
        table.setOnMouseClicked(e -> toggleBtn.setDisable(table.getSelectionModel().getSelectedItem() == null));

        toggleBtn.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            try {
                api.setEmployeeActive(sel.id, !sel.active);
                reload[0].run();
            } catch (Exception ex) {
                error(ex);
            }
        });

        Button back = UiKit.iconButton("BACK", Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar("Employee Status", back);

        var filterBox = UiKit.fieldBoxControl("FILTER", Icons.SEARCH, filter);
        filterBox.setMaxWidth(260);
        HBox controlsRow = new HBox(16, filterBox, toggleBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(12, controlsRow, table);
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
        stage.setTitle("RbcTcsWorld - Employee Status");
        stage.show();

        reload[0].run();
    }

    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
