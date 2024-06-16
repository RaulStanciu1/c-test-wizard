package com.ctestwizard.controller;

import com.ctestwizard.model.test.driver.TCompiler;
import com.ctestwizard.model.test.entity.TProject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
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
    @FXML
    private TextField AdditionalFlags;

    public void setup(Stage stage,ProjectListController parentController){
        this.stage = stage;
        this.parentController = parentController;
    }
    public void init() {
        IncludeDirectories.getItems().clear();
        Linker.getItems().clear();
        ContextMenu contextMenuInclude = new ContextMenu();
        MenuItem removeIncludeDirectory = new MenuItem("Remove Include Directory");
        MenuItem removeLinker = new MenuItem("Remove Linker");
        removeIncludeDirectory.setOnAction(event -> removeSelectedDirectory());
        removeLinker.setOnAction(event -> removeSelectedLinker());
        contextMenuInclude.getItems().addAll(removeIncludeDirectory);
        IncludeDirectories.setContextMenu(contextMenuInclude);

        ContextMenu contextMenuLinker = new ContextMenu();
        contextMenuLinker.getItems().addAll(removeLinker);
        Linker.setContextMenu(contextMenuLinker);

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
            List<String> includeDirectoriesMut = new ArrayList<>(includeDirectories.size());
            includeDirectoriesMut.addAll(includeDirectories);

            List<String> linkerFiles = Linker.getItems().stream().toList();
            List<String> linkerFilesMut = new ArrayList<>(linkerFiles.size());
            linkerFilesMut.addAll(linkerFiles);
            if(projectName.isEmpty() || sourceFilePath.isEmpty() || projectPath.isEmpty() || compilerCommand.isEmpty() || preprocessFlag.isEmpty() || compileFlag.isEmpty() || outputFlag.isEmpty() || includeFlag.isEmpty() || linkerFlag.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not create project");
                alert.setContentText("Please fill in all fields");
                alert.showAndWait();
                return;
            }
            TCompiler compiler = new TCompiler(compilerCommand,preprocessFlag,compileFlag,outputFlag,includeFlag,linkerFlag,includeDirectoriesMut,linkerFilesMut,AdditionalFlags.getText());
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

    @FXML
    public void addIncludeDirectory(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Include Directory");
        dialog.setHeaderText("Enter the Include Directory");
        dialog.setContentText("Include Directory:");
        dialog.showAndWait().ifPresent(directory -> {
            IncludeDirectories.getItems().add(directory);
        });
    }

    @FXML
    public void removeSelectedDirectory(){
        String selectedDirectory = IncludeDirectories.getSelectionModel().getSelectedItem();
        if(selectedDirectory == null){
            return;
        }
        IncludeDirectories.getItems().remove(selectedDirectory);
    }

    @FXML
    public void addLinker(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Linker");
        dialog.setHeaderText("Enter the Linker File");
        dialog.setContentText("Linker:");
        dialog.showAndWait().ifPresent(linker -> {
            Linker.getItems().add(linker);
        });
    }

    @FXML
    public void removeSelectedLinker(){
        String selectedLinker = Linker.getSelectionModel().getSelectedItem();
        if(selectedLinker == null){
            return;
        }
        Linker.getItems().remove(selectedLinker);
    }
}
