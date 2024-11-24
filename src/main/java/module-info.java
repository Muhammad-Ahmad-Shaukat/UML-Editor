module com.boota.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.swing;
    requires batik.all;

    opens com.boota.javaproject to javafx.fxml;
    exports com.boota.javaproject;
    exports UseCaseDiagram;
    opens UseCaseDiagram to javafx.fxml;
    exports ClassDiagram;
    opens ClassDiagram to javafx.fxml;
}