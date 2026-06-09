module com.pup.byod.javafxbyodclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.pup.byod.javafxbyodclient to javafx.fxml;
    exports com.pup.byod.javafxbyodclient;
}