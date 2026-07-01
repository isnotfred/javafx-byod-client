package com.pup.byod.javafxbyodclient;

import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class BYODApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("BYOD Device Management System");
        
        // Set application window and taskbar icon
        try {
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/pup/byod/javafxbyodclient/images/BYOD_Logo.png")));
        } catch (Exception e) {
            System.err.println("Failed to load application icon: " + e.getMessage());
        }

        NavigationManager.getInstance().setPrimaryStage(stage);
        NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
    }
}
