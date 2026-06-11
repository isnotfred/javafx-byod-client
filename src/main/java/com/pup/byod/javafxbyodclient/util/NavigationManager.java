package com.pup.byod.javafxbyodclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationManager {
    private static NavigationManager instance;
    private Stage primaryStage;
    private Pane contentArea;

    private NavigationManager() {
    }

    public void setContentArea(Pane contentArea) {
        this.contentArea = contentArea;
    }

    public Pane getContentArea() {
        return contentArea;
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Swaps the root of the primary stage to a new full-screen view (e.g. Login or Dashboard layout).
     */
    public void switchRootScene(String fxmlFileName) {
        try {
            URL url = getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/" + fxmlFileName);
            if (url == null) {
                throw new IllegalArgumentException("Cannot find FXML file: " + fxmlFileName);
            }
            Parent root = FXMLLoader.load(url);
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            
            // Add style sheet if it exists
            URL cssUrl = getClass().getResource("/com/pup/byod/javafxbyodclient/css/styles.css");
            if (cssUrl != null) {
                String cssPath = cssUrl.toExternalForm();
                if (!scene.getStylesheets().contains(cssPath)) {
                    scene.getStylesheets().add(cssPath);
                }
            }

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

            if ("LoginScreen.fxml".equals(fxmlFileName)) {
                primaryStage.setMaximized(false);
                primaryStage.setWidth(750);
                primaryStage.setHeight(600);
                primaryStage.centerOnScreen();
            } else {
                primaryStage.setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Failed to switch screen", e.getMessage());
        }
    }

    /**
     * Loads a sub-view (e.g. DeviceManagementScreen) into a container pane (e.g. the main content panel in a BorderPane).
     */
    public void loadViewIntoContainer(Pane container, String fxmlFileName) {
        try {
            container.getChildren().clear();
            URL url = getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/" + fxmlFileName);
            if (url == null) {
                throw new IllegalArgumentException("Cannot find FXML file: " + fxmlFileName);
            }
            Parent view = FXMLLoader.load(url);
            
            // Bind view width/height to container if needed
            view.prefWidth(-1);
            view.prefHeight(-1);
            
            container.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Failed to load sub-screen", e.getMessage());
        }
    }

    /**
     * Opens an FXML view as a modal dialog.
     */
    public void openModal(String fxmlFileName, String title) {
        try {
            URL url = getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/" + fxmlFileName);
            if (url == null) {
                throw new IllegalArgumentException("Cannot find FXML file: " + fxmlFileName);
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle(title);
            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/com/pup/byod/javafxbyodclient/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Modal Error", "Failed to open modal", e.getMessage());
        }
    }

    /**
     * Closes the window associated with the action event (e.g. from a cancel button).
     */
    public void closeModal(javafx.event.ActionEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
