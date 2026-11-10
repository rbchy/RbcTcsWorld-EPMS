package com.epms.desktop.ui;

import com.epms.desktop.api.ApiClient;
import com.epms.desktop.i18n.I18n;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsView {

    // Parallel to I18n.LANGS — index i here is the same language as I18n.LANGS[i].
    private static final String[] CODES = I18n.LANGS;
    private static final String[] LABEL_KEYS = {
            "settings.language.en", "settings.language.bn", "settings.language.es",
            "settings.language.hi", "settings.language.fr", "settings.language.zh"
    };

    public void show(Stage stage, ApiClient api) {
        Label langSection = new Label(I18n.t("settings.language.section"));
        langSection.getStyleClass().add("section-header");
        Label langHint = new Label(I18n.t("settings.language.hint"));
        langHint.setWrapText(true);

        String[] displayNames = new String[CODES.length];
        for (int i = 0; i < CODES.length; i++) displayNames[i] = I18n.t(LABEL_KEYS[i]);

        ComboBox<String> langCombo = new ComboBox<>(FXCollections.observableArrayList(displayNames));
        langCombo.setMaxWidth(Double.MAX_VALUE);
        int currentIndex = 0;
        for (int i = 0; i < CODES.length; i++) if (CODES[i].equals(I18n.lang)) currentIndex = i;
        langCombo.getSelectionModel().select(currentIndex);

        Button apply = UiKit.iconButton(I18n.t("settings.apply"), Icons.SAVE, "btn-primary");
        apply.setOnAction(e -> {
            int idx = langCombo.getSelectionModel().getSelectedIndex();
            if (idx < 0) idx = 0;
            I18n.lang = CODES[idx];
            new DashboardView().show(stage, api);
        });

        VBox languageBox = new VBox(10, langSection, langHint, langCombo, apply);
        VBox languageCard = new VBox(languageBox);
        languageCard.getStyleClass().add("form-card");
        languageCard.setPadding(new Insets(24));

        Label connSection = new Label(I18n.t("settings.connection.section"));
        connSection.getStyleClass().add("section-header");
        Label connHint = new Label(I18n.t("settings.connection.hint"));
        connHint.setWrapText(true);
        Label connUrl = new Label(api.getBase());
        connUrl.getStyleClass().add("field-caption");
        VBox connCard = new VBox(10, connSection, connHint, connUrl);
        connCard.getStyleClass().add("form-card");
        connCard.setPadding(new Insets(24));

        Label aboutSection = new Label(I18n.t("settings.about.section"));
        aboutSection.getStyleClass().add("section-header");
        Label aboutText = new Label(I18n.t("settings.about.text"));
        aboutText.setWrapText(true);
        VBox aboutCard = new VBox(10, aboutSection, aboutText);
        aboutCard.getStyleClass().add("form-card");
        aboutCard.setPadding(new Insets(24));

        Button back = UiKit.iconButton(I18n.t("common.back"), Icons.BACK_ARROW, "btn-topbar");
        back.setOnAction(e -> new DashboardView().show(stage, api));

        HBox topBar = UiKit.topBar(I18n.t("settings.title"), back);

        VBox body = new VBox(20, languageCard, connCard, aboutCard);
        body.setPadding(new Insets(24));
        body.setMaxWidth(680);

        VBox content = new VBox(topBar, body);

        Scene scene = new Scene(UiKit.withWatermark(content, "/images/watermark-dark.png", "dashboard-bg"), 1000, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RbcTcsWorld - " + I18n.t("settings.title"));
        stage.show();
    }
}
