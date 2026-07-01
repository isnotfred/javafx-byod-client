module com.pup.byod.javafxbyodclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.pup.byod.javafxbyodclient to javafx.fxml;
    opens com.pup.byod.javafxbyodclient.controller to javafx.fxml;
    opens com.pup.byod.javafxbyodclient.model to com.fasterxml.jackson.databind;

    exports com.pup.byod.javafxbyodclient;
    exports com.pup.byod.javafxbyodclient.controller;
    exports com.pup.byod.javafxbyodclient.model;
    exports com.pup.byod.javafxbyodclient.service;
}