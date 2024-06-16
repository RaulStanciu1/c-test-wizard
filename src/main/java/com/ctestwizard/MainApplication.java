package com.ctestwizard;

import com.ctestwizard.controller.ProjectListController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

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
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/icon.png"))));
        stage.setTitle("CTestWizard - Project List");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}