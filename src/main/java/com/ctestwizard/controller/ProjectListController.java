package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.testentity.TProject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectListController {
    private Stage stage;
    @FXML
    private ListView<String> ProjectList;

    public void setup(Stage stage){
        this.stage = stage;
    }

    public void init() {
        String projectListPath = System.getProperty("user.home") + File.separator + ".ctestwizard" + File.separator + "ProjectList.lst";
        File projectListFile = new File(projectListPath);
        if (!projectListFile.exists()) {
            return;
        }
        try{
            List<String> projectPaths = FileUtils.readLines(projectListFile, "UTF-8");
            ProjectList.getItems().addAll(projectPaths);
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not read project list");
            alert.setContentText("An error occurred while reading the project list");
            alert.showAndWait();
        }
    }

    @FXML
    public void openProject() {
        try{
            Stage mainStage = new Stage();
            String projectPath = ProjectList.getSelectionModel().getSelectedItem();
            if(projectPath == null){
                return;
            }
            TProject project = TProject.loadProject(projectPath);
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setup(project,mainStage);
            controller.init();
            mainStage.setScene(new Scene(root));
            stage.close();
            mainStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open project");
            alert.setContentText("An error occurred while opening the project");
            alert.showAndWait();
        }

    }

    @FXML
    public void createNewProject(){
        try{
            Stage mainStage = new Stage();
            mainStage.initModality(Modality.APPLICATION_MODAL);
            mainStage.initOwner(stage);
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("create-project-view.fxml"));
            Parent root = loader.load();
            CreateProjectController controller = loader.getController();
            controller.setup(mainStage,this);
            controller.init();
            mainStage.setScene(new Scene(root));
            mainStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not create new project");
            alert.setContentText("An error occurred while creating a new project");
            alert.showAndWait();
        }
    }

    public void addProject(String project){
        ProjectList.getItems().add(project);
    }
}
