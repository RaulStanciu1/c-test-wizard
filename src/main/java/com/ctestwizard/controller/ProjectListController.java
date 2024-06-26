package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.test.entity.TProject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Controller for the project list view
 */
public class ProjectListController {
    private Stage stage;
    @FXML
    private ListView<String> ProjectList;

    /**
     * Set up the controller
     * @param stage The stage
     */
    public void setup(Stage stage){
        this.stage = stage;
    }

    /**
     * Initialize the controller
     */
    public void init() {
        String projectListPath = System.getProperty("user.home") + File.separator + ".ctestwizard" + File.separator + "ProjectList.lst";
        File projectListFile = new File(projectListPath);
        if (!projectListFile.exists()) {
            return;
        }
        try{
            List<String> projectPaths = FileUtils.readLines(projectListFile, "UTF-8");
            ProjectList.getItems().addAll(projectPaths);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem openItem = new MenuItem("Open Project");
            MenuItem deleteItem = new MenuItem("Delete Project");
            openItem.setOnAction(event -> openProject());
            deleteItem.setOnAction(event -> deleteProject());
            contextMenu.getItems().addAll(openItem,deleteItem);
            ProjectList.setContextMenu(contextMenu);
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not read project list");
            alert.setContentText("An error occurred while reading the project list");
            alert.showAndWait();
        }
    }

    /**
     * Method used to open a project
     */
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
            mainStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/icon.png"))));
            mainStage.setTitle("CTestWizard - " + project.getName());
            mainStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open project");
            alert.setContentText("An error occurred while opening the project");
            alert.showAndWait();
        }

    }

    /**
     * Method used to delete a project
     */
    public void deleteProject(){
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Project");
        confirmation.setHeaderText("Are you sure you want to delete the project?");
        confirmation.setContentText("This action cannot be undone");
        confirmation.showAndWait();
        if(confirmation.getResult().getButtonData().isCancelButton()){
            return;
        }

        String projectPath = ProjectList.getSelectionModel().getSelectedItem();
        if(projectPath == null) {
            return;
        }
        File projectFile = new File(projectPath);
        File projectFolder = projectFile.getParentFile();
        if(projectFolder.exists()){
            projectFolder.delete();
        }

        ProjectList.getItems().remove(projectPath);
        String projectListPath = System.getProperty("user.home") + File.separator + ".ctestwizard" + File.separator + "ProjectList.lst";
        File projectListFile = new File(projectListPath);
        //Truncate the file and rewrite the new projects
        try{
            FileUtils.writeLines(projectListFile,ProjectList.getItems());
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not delete project");
            alert.setContentText("An error occurred while deleting the project");
            alert.showAndWait();
        }
    }

    /**
     * Method used to create a new project
     */
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
            mainStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/icon.png"))));
            mainStage.setTitle("CTestWizard - Create Project");
            mainStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not create new project");
            alert.setContentText("An error occurred while creating a new project");
            alert.showAndWait();
        }
    }

    /**
     * Method used to add a project to the list
     * @param project The project to add
     */
    public void addProject(String project){
        ProjectList.getItems().add(project);
    }
}
