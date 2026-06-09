package com.pup.byod.javafxbyodclient;

import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("BYOD Device Management System");
        NavigationManager.getInstance().setPrimaryStage(stage);
        NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
    }
}
