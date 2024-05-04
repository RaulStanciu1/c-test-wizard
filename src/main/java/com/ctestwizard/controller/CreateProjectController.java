package com.ctestwizard.controller;

import com.ctestwizard.model.testdriver.TCompiler;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.Serializable;
import java.lang.reflect.Field;

import java.io.File;
import java.util.List;

public class CreateProjectController {
    private ProjectListController parentController;
    private Stage stage;
    @FXML
    private TextField SourceFilePath;
    @FXML
    private TextField ProjectPath;
    @FXML
    private TextField ProjectName;
    @FXML
    private TextField CompilerCommand;
    @FXML
    private TextField PreprocessFlag;
    @FXML
    private TextField CompileFlag;
    @FXML
    private TextField OutputFlag;
    @FXML
    private TextField IncludeFlag;
    @FXML
    private TextField LinkerFlag;
    @FXML
    private ListView<String> IncludeDirectories;
    @FXML
    private ListView<String> Linker;

    public void setup(Stage stage,ProjectListController parentController){
        this.stage = stage;
        this.parentController = parentController;
    }
    public void init() {
    }

    @FXML
    public void selectSourceFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Source File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("C Source Files", "*.c"));
        File sourceFile = fileChooser.showOpenDialog(stage);
        if(sourceFile != null){
            SourceFilePath.setText(sourceFile.getAbsolutePath());
        }
    }

    @FXML
    public void selectProjectDirectory(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project Directory");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Directory", "*"));
        File projectDir = fileChooser.showSaveDialog(stage);
        if(projectDir != null){
            ProjectPath.setText(projectDir.getAbsolutePath());
        }
    }


    @FXML
    public void createProject() {
        try {
            String projectName = ProjectName.getText();
            String sourceFilePath = SourceFilePath.getText();
            String projectPath = ProjectPath.getText();
            String compilerCommand = CompilerCommand.getText();
            String preprocessFlag = PreprocessFlag.getText();
            String compileFlag = CompileFlag.getText();
            String outputFlag = OutputFlag.getText();
            String includeFlag = IncludeFlag.getText();
            String linkerFlag = LinkerFlag.getText();
            List<String> includeDirectories = IncludeDirectories.getItems().stream().toList();
            List<String> linkerFiles = Linker.getItems().stream().toList();
            if(projectName.isEmpty() || sourceFilePath.isEmpty() || projectPath.isEmpty() || compilerCommand.isEmpty() || preprocessFlag.isEmpty() || compileFlag.isEmpty() || outputFlag.isEmpty() || includeFlag.isEmpty() || linkerFlag.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not create project");
                alert.setContentText("Please fill in all fields");
                alert.showAndWait();
                return;
            }
            TCompiler compiler = new TCompiler(compilerCommand,preprocessFlag,compileFlag,outputFlag,includeFlag,linkerFlag,includeDirectories,linkerFiles);
            TProject project = TProject.newTProject(projectName,sourceFilePath,projectPath,compiler);
            TProject.archiveProject(project);
            parentController.addProject(projectPath + File.separator + projectName + ".ctw");
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not create project");
            alert.setContentText("An error occurred while creating the project");
            alert.showAndWait();
        }
    }
}
