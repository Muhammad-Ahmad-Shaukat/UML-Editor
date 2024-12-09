module com.boota.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.swing;
    requires batik.all;
    requires com.fasterxml.jackson.databind;

    opens com.boota.javaproject to javafx.fxml;
    exports com.boota.javaproject;
    exports com.boota.javaproject.UseCaseDiagram;
    opens com.boota.javaproject.UseCaseDiagram to javafx.fxml;
    exports com.boota.javaproject.ClassDiagram;
    opens com.boota.javaproject.ClassDiagram to javafx.fxml;
}