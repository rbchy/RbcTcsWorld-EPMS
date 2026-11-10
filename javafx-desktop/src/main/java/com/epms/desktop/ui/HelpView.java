package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelpView {

    private static final String[][] TOPICS = {
            {"help.topic.overview", "help.overview.body"},
            {"help.topic.employees", "help.employees.body"},
            {"help.topic.payroll", "help.payroll.body"},
            {"help.topic.timeclock", "help.timeclock.body"},
            {"help.topic.payslips", "help.payslips.body"},
            {"help.topic.accounting", "help.accounting.body"},
            {"help.topic.w2", "help.w2.body"},
            {"help.topic.reports", "help.reports.body"},
            {"help.topic.settings", "help.settings.body"},
    };

    public void show(Stage stage, ApiClient api) {
        Label title = new Label(I18n.t("help.title"));
        title.getStyleClass().add("title");

        ToggleGroup langGroup = new ToggleGroup();
        RadioButton enBtn = new RadioButton("EN");
        RadioButton bnBtn = new RadioButton("বাং");
        enBtn.setToggleGroup(langGroup);
        bnBtn.setToggleGroup(langGroup);
        if ("bn".equals(I18n.lang)) bnBtn.setSelected(true); else enBtn.setSelected(true);
        enBtn.setOnAction(e -> { I18n.lang = "en"; new HelpView().show(stage, api); });
        bnBtn.setOnAction(e -> { I18n.lang = "bn"; new HelpView().show(stage, api); });

        Button back = new Button(I18n.t("common.back"));
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = new HBox(14, title, spacer(), new Label(I18n.t("help.language")), enBtn, bnBtn, back);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 20, 16, 20));
        topBar.getStyleClass().add("help-topbar");

        Label bodyLabel = new Label();
        bodyLabel.setWrapText(true);
        bodyLabel.getStyleClass().add("help-body");
        VBox bodyWrap = new VBox(bodyLabel);
        bodyWrap.setPadding(new Insets(24));
        ScrollPane scroll = new ScrollPane(bodyWrap);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("help-scroll");

        VBox sidebar = new VBox(4);
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(230);
        sidebar.getStyleClass().add("help-sidebar");

        Button[] topicButtons = new Button[TOPICS.length];
        for (int i = 0; i < TOPICS.length; i++) {
            String bodyKey = TOPICS[i][1];
            Button b = new Button(I18n.t(TOPICS[i][0]));
            b.setMaxWidth(Double.MAX_VALUE);
            b.getStyleClass().add("help-topic-button");
            int idx = i;
            b.setOnAction(e -> {
                bodyLabel.setText(I18n.t(bodyKey));
                for (Button ob : topicButtons) ob.getStyleClass().remove("selected");
                topicButtons[idx].getStyleClass().add("selected");
            });
            topicButtons[i] = b;
            sidebar.getChildren().add(b);
        }

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(scroll);

        topicButtons[0].fire();

        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("EPMS - " + I18n.t("help.title"));
        stage.show();
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}
