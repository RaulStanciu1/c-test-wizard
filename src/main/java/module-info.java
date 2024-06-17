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
    requires org.apache.commons.io;
    requires itextpdf;
    requires java.desktop;

    opens com.ctestwizard to javafx.fxml;
    exports com.ctestwizard;
    exports com.ctestwizard.controller;
    opens com.ctestwizard.controller to javafx.fxml;

    opens com.ctestwizard.model.test.entity to javafx.fxml;
    exports com.ctestwizard.model.test.entity;

    opens com.ctestwizard.model.code.entity to javafx.fxml;
    exports com.ctestwizard.model.code.entity;

    opens com.ctestwizard.model.test.driver to javafx.fxml;
    exports com.ctestwizard.model.test.driver;

    opens com.ctestwizard.model.code.parser to javafx.fxml;
    exports com.ctestwizard.model.code.parser;
}