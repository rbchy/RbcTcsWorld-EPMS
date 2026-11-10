package com.epms.desktop;
import com.epms.desktop.api.ApiClient; import com.epms.desktop.ui.LoginView; import javafx.application.Application; import javafx.stage.Stage;
public class MainApp extends Application {public void start(Stage stage){new LoginView().show(stage,new ApiClient());}public static void main(String[] args){launch(args);}}
