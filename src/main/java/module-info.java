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

    opens com.ctestwizard to javafx.fxml;
    exports com.ctestwizard;
    exports com.ctestwizard.controller;
    opens com.ctestwizard.controller to javafx.fxml;

    opens com.ctestwizard.model.testentity to javafx.fxml;
    exports com.ctestwizard.model.testentity;

    opens com.ctestwizard.model.entity to javafx.fxml;
    exports com.ctestwizard.model.entity;

    opens com.ctestwizard.model.testdriver to javafx.fxml;
    exports com.ctestwizard.model.testdriver;

    opens com.ctestwizard.model.cparser to javafx.fxml;
    exports com.ctestwizard.model.cparser;
}