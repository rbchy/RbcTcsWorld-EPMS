package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class DashboardView {

    public void show(Stage stage, ApiClient api) {
        HBox headerBar = buildHeaderBar();
        javafx.scene.control.MenuBar menuBar = AppMenuBar.build(stage, api);

        Label welcome = new Label(I18n.t("dashboard.welcome"));
        welcome.getStyleClass().add("title");
        Label hint = new Label(I18n.t("dashboard.hint"));
        hint.setWrapText(true);
        hint.setMaxWidth(680);
        hint.setStyle("-fx-alignment: center; -fx-text-alignment: center;");

        VBox introBox = new VBox(8, welcome, hint);
        introBox.setAlignment(Pos.CENTER);
        introBox.setPadding(new Insets(28, 20, 10, 20));

        GridPane grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(22);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 30, 30, 30));

        Button[] tiles = {
                navTile(I18n.t("menu.dashboard"), Icons.HOME, e -> new DashboardView().show(stage, api)),
                navTile(I18n.t("menu.employees"), Icons.GROUP, e -> new EmployeeView().show(stage, api)),
                navTile(I18n.t("menu.payroll"), Icons.CALCULATOR, e -> new PayrollView().show(stage, api)),
                navTile(I18n.t("menu.timeclock"), Icons.CLOCK, e -> new TimeClockView().show(stage, api)),
                navTile(I18n.t("menu.payslips"), Icons.DOCUMENT, e -> new PayslipsView().show(stage, api)),
                navTile(I18n.t("menu.accounting"), Icons.LEDGER, e -> new PayrollAccountingView().show(stage, api)),
                navTile(I18n.t("menu.w2"), Icons.BADGE, e -> new W2View().show(stage, api)),
                navTile(I18n.t("menu.reports"), Icons.CHART, e -> new ReportsView().show(stage, api)),
                navTile(I18n.t("menu.help"), Icons.HELP, e -> new HelpView().show(stage, api)),
                navTile(I18n.t("menu.settings"), Icons.SETTINGS, e -> new SettingsView().show(stage, api)),
        };

        int cols = 5;
        for (int i = 0; i < tiles.length; i++) {
            grid.add(tiles[i], i % cols, i / cols);
        }

        VBox content = new VBox(headerBar, menuBar, introBox, grid);

        StackPane root = new StackPane();
        root.getStyleClass().add("dashboard-bg");

        ImageView watermark = new ImageView(new Image(getClass().getResource("/images/watermark-dark.png").toExternalForm()));
        watermark.setPreserveRatio(false);
        watermark.fitWidthProperty().bind(root.widthProperty());
        watermark.fitHeightProperty().bind(root.heightProperty());
        watermark.setMouseTransparent(true);

        root.getChildren().addAll(watermark, content);

        Scene scene = new Scene(root, 1360, 820);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("menu.dashboard"));
        stage.show();
    }

    private HBox buildHeaderBar() {
        SVGPath lockGlyph = new SVGPath();
        lockGlyph.setContent(Icons.LOCK);
        lockGlyph.setFill(Color.WHITE);

        StackPane logo = new StackPane(lockGlyph);
        logo.getStyleClass().add("logo-circle-small");
        logo.setPrefSize(44, 44);
        logo.setMaxSize(44, 44);
        logo.setMinSize(44, 44);

        Label brand = new Label("RbcTcsWorld");
        brand.getStyleClass().add("brand-text-header");
        Label subtitle = new Label("EPMS  •  " + I18n.t("dashboard.subtitle"));
        subtitle.getStyleClass().add("brand-subtitle-header");
        VBox brandBox = new VBox(2, brand, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(14, logo, brandBox, spacer);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 26, 16, 26));
        bar.getStyleClass().add("dashboard-header");
        return bar;
    }

    private Button navTile(String label, String iconSvg, EventHandler<ActionEvent> handler) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.getStyleClass().add("nav-tile-icon");

        StackPane iconWrap = new StackPane(icon);
        iconWrap.getStyleClass().add("nav-tile-icon-circle");
        iconWrap.setPrefSize(58, 58);
        iconWrap.setMaxSize(58, 58);
        iconWrap.setMinSize(58, 58);

        Button b = new Button(label, iconWrap);
        b.setContentDisplay(ContentDisplay.TOP);
        b.getStyleClass().add("nav-tile");
        b.setPrefSize(210, 148);
        b.setOnAction(handler);
        return b;
    }
}
