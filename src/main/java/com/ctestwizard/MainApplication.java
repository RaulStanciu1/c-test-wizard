package com.ctestwizard;

import com.ctestwizard.controller.ProjectListController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("project-list.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ProjectListController controller = loader.getController();
        controller.setup(stage);
        controller.init();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}