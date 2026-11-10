package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class LoginView {

    public void show(Stage stage, ApiClient api) {
        VBox header = buildHeader();

        TextField userField = new TextField("admin");
        userField.setPromptText(I18n.t("login.username"));
        VBox userBox = fieldBox(I18n.t("login.username"), Icons.PERSON, userField);

        PasswordField passField = new PasswordField();
        passField.setText("admin123");
        passField.setPromptText(I18n.t("login.password"));
        VBox passBox = fieldBox(I18n.t("login.password"), Icons.LOCK, passField);

        HBox fieldsRow = new HBox(18, userBox, passBox);
        HBox.setHgrow(userBox, Priority.ALWAYS);
        HBox.setHgrow(passBox, Priority.ALWAYS);
        userBox.setMaxWidth(Double.MAX_VALUE);
        passBox.setMaxWidth(Double.MAX_VALUE);

        Label msg = new Label();
        msg.getStyleClass().add("error-msg");
        msg.setWrapText(true);
        msg.setMaxWidth(420);
        msg.setAlignment(Pos.CENTER);

        Button loginBtn = buildLoginButton(userField, passField, msg, stage, api);

        VBox card = new VBox(24, header, fieldsRow, loginBtn, msg);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(44, 48, 40, 48));

        DropShadow shadow = new DropShadow();
        shadow.setRadius(35);
        shadow.setColor(Color.rgb(20, 30, 70, 0.28));
        card.setEffect(shadow);

        StackPane root = new StackPane();
        root.getStyleClass().add("login-bg");

        ImageView watermark = new ImageView(new Image(getClass().getResource("/images/watermark-light.png").toExternalForm()));
        watermark.setPreserveRatio(false);
        watermark.fitWidthProperty().bind(root.widthProperty());
        watermark.fitHeightProperty().bind(root.heightProperty());
        watermark.setMouseTransparent(true);

        Label footer = new Label(I18n.t("login.footer"));
        footer.getStyleClass().add("watermark-footer");
        StackPane.setAlignment(footer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footer, new Insets(0, 0, 18, 0));

        root.getChildren().addAll(watermark, card, footer);

        Scene scene = new Scene(root, 1000, 640);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - EPMS Login");
        stage.show();
    }

    private VBox buildHeader() {
        SVGPath lockGlyph = new SVGPath();
        lockGlyph.setContent(Icons.LOCK);
        lockGlyph.setFill(Color.WHITE);
        lockGlyph.setScaleX(1.5);
        lockGlyph.setScaleY(1.5);

        StackPane logo = new StackPane(lockGlyph);
        logo.getStyleClass().add("logo-circle");
        logo.setPrefSize(74, 74);
        logo.setMaxSize(74, 74);
        logo.setMinSize(74, 74);

        Label brand = new Label("RbcTcsWorld");
        brand.getStyleClass().add("brand-text");
        Label title = new Label("EPMS");
        title.getStyleClass().add("login-title");
        Label subtitle = new Label(I18n.t("login.subtitle"));
        subtitle.getStyleClass().add("login-subtitle");

        VBox header = new VBox(6, logo, brand, title, subtitle);
        header.setAlignment(Pos.CENTER);
        VBox.setMargin(logo, new Insets(0, 0, 4, 0));
        return header;
    }

    private VBox fieldBox(String labelText, String iconSvg, TextField field) {
        Label caption = new Label(labelText == null ? null : labelText.toUpperCase());
        caption.getStyleClass().add("field-caption");

        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.getStyleClass().add("field-icon");
        icon.setScaleX(0.75);
        icon.setScaleY(0.75);

        field.getStyleClass().add("field-input");

        HBox inputRow = new HBox(10, icon, field);
        inputRow.getStyleClass().add("field-wrapper");
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);

        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                inputRow.getStyleClass().add("focused");
            } else {
                inputRow.getStyleClass().remove("focused");
            }
        });

        VBox box = new VBox(6, caption, inputRow);
        return box;
    }

    private Button buildLoginButton(TextField userField, PasswordField passField, Label msg, Stage stage, ApiClient api) {
        SVGPath arrow = new SVGPath();
        arrow.setContent(Icons.ARROW);
        arrow.setFill(Color.WHITE);
        arrow.setScaleX(0.85);
        arrow.setScaleY(0.85);

        Button loginBtn = new Button(I18n.t("login.button").toUpperCase(), arrow);
        loginBtn.setContentDisplay(ContentDisplay.RIGHT);
        loginBtn.getStyleClass().add("login-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            msg.setText("");
            String username = userField.getText();
            String password = passField.getText();
            try {
                api.login(username, password);
                new DashboardView().show(stage, api);
            } catch (Exception ex) {
                msg.setText(I18n.t("login.error"));
            }
        });

        return loginBtn;
    }
}
