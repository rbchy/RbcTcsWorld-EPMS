package com.epms.desktop.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Small shared UI builders so every screen (Employees, Payroll, Time Clock, ...)
 * draws fields, buttons and backgrounds the same way instead of re-implementing
 * the same look on each screen.
 */
public final class UiKit {
    private UiKit() {}

    /** Uppercases text for the ALL-CAPS label/button convention used across every screen.
     *  Safe to call on any language: scripts without letter case (Bangla, Hindi, Chinese, ...)
     *  are left unchanged by toUpperCase(), so this has no visual effect for them and simply
     *  upper-cases Latin-script text (English, Spanish, French). */
    public static String up(String text) {
        return text == null ? null : text.toUpperCase();
    }

    /** A labeled input with a leading icon, in a rounded pill — same look as the Login fields. */
    public static VBox fieldBox(String labelText, String iconSvg, TextField field) {
        Label caption = new Label(up(labelText));
        caption.getStyleClass().add("field-caption");

        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.getStyleClass().add("field-icon");
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        field.getStyleClass().add("field-input");

        HBox inputRow = new HBox(10, icon, field);
        inputRow.getStyleClass().add("field-wrapper");
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);

        field.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) inputRow.getStyleClass().add("focused");
            else inputRow.getStyleClass().remove("focused");
        });

        VBox box = new VBox(6, caption, inputRow);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /** A field box that also wraps an arbitrary control (ComboBox, DatePicker, ...) instead of a TextField. */
    public static VBox fieldBoxControl(String labelText, String iconSvg, Node control) {
        Label caption = new Label(up(labelText));
        caption.getStyleClass().add("field-caption");

        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.getStyleClass().add("field-icon");
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        control.getStyleClass().add("field-input");

        HBox inputRow = new HBox(10, icon, control);
        inputRow.getStyleClass().add("field-wrapper");
        inputRow.setAlignment(Pos.CENTER_LEFT);
        if (control instanceof Region) {
            ((Region) control).setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(control, Priority.ALWAYS);
        }

        VBox box = new VBox(6, caption, inputRow);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /** A button with an icon graphic, styled with one of the shared button classes. */
    public static Button iconButton(String text, String iconSvg, String styleClass) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.setFill("btn-secondary".equals(styleClass) ? Color.web("#33405c") : Color.WHITE);
        icon.setScaleX(0.75);
        icon.setScaleY(0.75);
        Button b = new Button(up(text), icon);
        b.setContentDisplay(ContentDisplay.LEFT);
        b.getStyleClass().add(styleClass);
        return b;
    }

    /** Wraps content in a full-bleed background with a faint watermark behind it. */
    public static StackPane withWatermark(Region content, String watermarkResourcePath, String bgStyleClass) {
        StackPane root = new StackPane();
        root.getStyleClass().add(bgStyleClass);

        ImageView watermark = new ImageView(new Image(UiKit.class.getResource(watermarkResourcePath).toExternalForm()));
        watermark.setPreserveRatio(false);
        watermark.fitWidthProperty().bind(root.widthProperty());
        watermark.fitHeightProperty().bind(root.heightProperty());
        watermark.setMouseTransparent(true);

        root.getChildren().addAll(watermark, content);
        return root;
    }

    /** A gradient top bar with a white title and optional trailing controls (e.g. a BACK button). */
    public static HBox topBar(String title, Node... trailing) {
        Label titleLabel = new Label(up(title));
        titleLabel.getStyleClass().add("screen-topbar-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(14, titleLabel, spacer);
        bar.getChildren().addAll(trailing);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 24, 16, 24));
        bar.getStyleClass().add("screen-topbar");
        return bar;
    }
}
