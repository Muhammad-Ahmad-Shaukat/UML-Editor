/**
 * This is the module declaration for the `com.boota.javaproject` module.
 * It specifies the dependencies of the module, the packages it makes accessible to other modules,
 * and the packages it opens for runtime reflection to specific modules.
 *
 * Requires:
 * - `javafx.controls`, `javafx.fxml`, `javafx.web`, `javafx.swing`: JavaFX-related modules for graphical user interface creation and management.
 * - `com.dlsc.formsfx`: A library for building and managing forms in JavaFX.
 * - `net.synedra.validatorfx`: A validation library for JavaFX.
 * - `org.kordamp.ikonli.javafx`: A library providing icon packs for JavaFX.
 * - `org.kordamp.bootstrapfx.core`: A library for creating modern-looking user interfaces using Bootstrap-like styling in JavaFX.
 * - `eu.hansolo.tilesfx`: A JavaFX library for creating dashboard tiles.
 * - `java.desktop`: Provides classes for desktop application development.
 * - `java.sql`: Provides classes for accessing and processing data stored in a relational database.
 * - `java.xml`: Provides XML processing utilities.
 * - `batik.all`: Apache Batik library for processing and rendering SVG content.
 *
 * Exports:
 * - The `com.boota.javaproject` package makes its public types available to other modules.
 * - The `com.boota.javaproject.UseCaseDiagram` package exports public types related to use case diagram functionality.
 * - The `com.boota.javaproject.ClassDiagram` package exports public types related to class diagram functionality.
 *
 * Opens:
 * - The `com.boota.javaproject` package is opened to the `javafx.fxml` module to allow reflection for JavaFX FXML operations.
 * - The `com.boota.javaproject.UseCaseDiagram` and `com.boota.javaproject.ClassDiagram` packages are also opened to the `javafx.fxml` module for the same purpose.
 */
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

    opens com.boota.javaproject to javafx.fxml;
    exports com.boota.javaproject;
    exports com.boota.javaproject.UseCaseDiagram;
    opens com.boota.javaproject.UseCaseDiagram to javafx.fxml;
    exports com.boota.javaproject.ClassDiagram;
    opens com.boota.javaproject.ClassDiagram to javafx.fxml;


    requires java.desktop;
    requires java.sql;
    requires java.xml;
    requires batik.all;
}