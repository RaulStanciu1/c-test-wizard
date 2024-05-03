package com.ctestwizard;

import com.ctestwizard.controller.MainController;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TProject project = TProject.newTProject("test_project", "src/main/resources/test_project/test.c", "src/main/resources/test_project");
        TObject testObject = project.getTestObjects().get(0);
        testObject.getTestCases().add(TCase.newTestCase(testObject));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setup(project,stage);
        controller.init();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}