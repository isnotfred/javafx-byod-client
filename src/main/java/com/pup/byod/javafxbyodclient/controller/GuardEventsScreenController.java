package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.service.RequestService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GuardEventsScreenController {
    @FXML private TableView<Request> eventsTable;
    @FXML private TableColumn<Request, String> colEventName;
    @FXML private TableColumn<Request, String> colVenue;
    @FXML private TableColumn<Request, Void> colView;
    @FXML private Region overlay;

    private final RequestService requestService = new RequestService();
    private final ObservableList<Request> eventsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));

        // Setup View Button Cell Factory
        colView.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.getStyleClass().addAll("action-btn", "action-btn-primary");
                btn.setStyle("-fx-font-size: 11px; -fx-padding: 4px 10px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Request req = getTableView().getItems().get(getIndex());
                    openEventDetailsModal(req);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        eventsTable.setItems(eventsList);

        // Load active event requests
        loadEvents();
    }

    @FXML
    public void loadEvents() {
        new Thread(() -> {
            try {
                LocalDate today = LocalDate.now();
                List<Request> all = requestService.getAllRequests();
                List<Request> activeEvents = new ArrayList<>();

                for (Request r : all) {
                    if ("event".equalsIgnoreCase(r.getRequestType()) && "approved".equalsIgnoreCase(r.getStatus())) {
                        LocalDate start = LocalDate.parse(r.getStartDate());
                        LocalDate end = LocalDate.parse(r.getEndDate());
                        if (!today.isBefore(start) && !today.isAfter(end)) {
                            activeEvents.add(r);
                        }
                    }
                }

                Platform.runLater(() -> {
                    eventsList.setAll(activeEvents);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Load Error", "Failed to retrieve events list", e.getMessage());
                });
            }
        }).start();
    }

    private void openEventDetailsModal(Request req) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/GuardEventDetailsModal.fxml"));
            Parent root = loader.load();

            GuardEventDetailsModalController modalController = loader.getController();
            modalController.initData(req);

            // Show overlay
            overlay.setVisible(true);
            overlay.setManaged(true);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(eventsTable.getScene().getWindow());
            stage.setTitle("Event Specifications Details");
            stage.setScene(new Scene(root));

            // Center stage on owner
            stage.setOnShown(event -> {
                Window owner = stage.getOwner();
                if (owner != null) {
                    double x = owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0;
                    double y = owner.getY() + (owner.getHeight() - stage.getHeight()) / 2.0;
                    stage.setX(x);
                    stage.setY(y);
                }
            });

            // Hide overlay when stage is closed
            stage.setOnHidden(event -> {
                overlay.setVisible(false);
                overlay.setManaged(false);
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            overlay.setVisible(false);
            overlay.setManaged(false);
            AlertHelper.showError("Modal Error", "Could not open event details modal", e.getMessage());
        }
    }
}
