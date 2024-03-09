module com.ctestwizard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.antlr.antlr4.runtime;

    opens com.ctestwizard to javafx.fxml;
    exports com.ctestwizard;
    exports com.ctestwizard.controller;
    opens com.ctestwizard.controller to javafx.fxml;
}