package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.code.entity.CDefine;
import com.ctestwizard.model.test.driver.TProperty;
import com.ctestwizard.model.test.entity.TProject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TestExecutionSettingsController {
    @FXML
    private TableView<TProperty> PropertyTable;
    @FXML
    private ListView<CDefine> DefinesList;
    @FXML
    private ListView<String> LinkerList;
    @FXML
    private ListView<String> IncludeList;
    @FXML
    private CheckBox CodeCoverageEnabled;
    @FXML
    private CheckBox CreateReport;
    private TProject project;
    private Stage parentStage;

    public void setup(TProject project, Stage parentStage){
        this.project = project;
        this.parentStage = parentStage;
    }

    public void init(){
        setupPropertyTable();
        setupDefinesList();
        setupLinkerList();
        setupIncludeList();
        CodeCoverageEnabled.setSelected(project.getTestDriver().isCoverageEnabled());
    }

    private void setupPropertyTable(){
        PropertyTable.getColumns().clear();
        PropertyTable.getItems().clear();
        TableColumn<TProperty,String> propertyColumn = new TableColumn<>("Property");
        propertyColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProperty()));
        TableColumn<TProperty,String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(event -> {
            TProperty property = event.getRowValue();
            property.setValue(event.getNewValue());
        });
        PropertyTable.getColumns().add(propertyColumn);
        PropertyTable.getColumns().add(valueColumn);
        ObservableList<TProperty> tableData = FXCollections.observableArrayList(
                new TProperty("Project Name",project.getName()),
                new TProperty("Compiler",project.getTestDriver().getCompiler()),
                new TProperty("Preprocess Flag",project.getTestDriver().getPreprocessFlag()),
                new TProperty("Compile Flag",project.getTestDriver().getCompileFlag()),
                new TProperty("Output Flag",project.getTestDriver().getOutputFlag()),
                new TProperty("Source File Path",project.getTestDriver().getSourceFilePath()),
                new TProperty("Project Path",project.getTestDriver().getProjectPath()),
                new TProperty("Test Header",project.getTestDriver().getTestHeaderPath()),
                new TProperty("Result Significance",String.valueOf(project.getTestDriver().getResultSignificance())),
                new TProperty("Coverage Significance",String.valueOf(project.getTestDriver().getCoverageSignificance()))
        );
        PropertyTable.setEditable(true);

        PropertyTable.setItems(tableData);

    }

    private void setupDefinesList(){
        DefinesList.getItems().clear();
        project.getTestDriver().getDefines().forEach(define -> DefinesList.getItems().add(define));
    }

    private void setupLinkerList(){
        LinkerList.getItems().clear();
        project.getTestDriver().getLinker().forEach(linker -> LinkerList.getItems().add(linker));
    }

    private void setupIncludeList(){
        IncludeList.getItems().clear();
        project.getTestDriver().getIncludeDirectories().forEach(include -> IncludeList.getItems().add(include));
    }

    @FXML
    public void commitPropertyChanges(){
        PropertyTable.getItems().forEach(property -> {
            switch(property.getProperty()){
                case "Project Name":
                    project.setName(property.getValue());
                    break;
                case "Compiler":
                    project.getTestDriver().setCompiler(property.getValue());
                    break;
                case "Preprocess Flag":
                    project.getTestDriver().setPreprocessFlag(property.getValue());
                    break;
                case "Compile Flag":
                    project.getTestDriver().setCompileFlag(property.getValue());
                    break;
                case "Output Flag":
                    project.getTestDriver().setOutputFlag(property.getValue());
                    break;
                case "Source File Path":
                    project.getTestDriver().setSourceFilePath(property.getValue());
                    break;
                case "Project Path":
                    project.getTestDriver().setProjectPath(property.getValue());
                    break;
                case "Test Header":
                    project.getTestDriver().setTestHeaderPath(property.getValue());
                    break;
                case "Result Significance":
                    project.getTestDriver().setResultSignificance(Double.parseDouble(property.getValue()));
                    break;
                case "Coverage Significance":
                    project.getTestDriver().setCoverageSignificance(Double.parseDouble(property.getValue()));
                    break;
            }
        });
        project.getTestDriver().setCoverageEnabled(CodeCoverageEnabled.isSelected());
        project.getTestDriver().setReportEnabled(CreateReport.isSelected());
    }

    @FXML
    public void createDefine(){
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("create-define-view.fxml"));
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.initOwner(parentStage);
        try {
            Parent root = loader.load();
            CreateDefineController controller = loader.getController();
            controller.setup(this,formStage);
            formStage.setScene(new Scene(root));
            formStage.setResizable(false);
            formStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/icon.png"))));
            formStage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void removeSelectedDefine(){
        CDefine selectedDefine = DefinesList.getSelectionModel().getSelectedItem();
        if(selectedDefine == null){
            return;
        }
        project.getTestDriver().getDefines().remove(selectedDefine);
        DefinesList.getItems().remove(selectedDefine);
    }

    @FXML
    public void addLinker(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Linker");
        dialog.setHeaderText("Enter the Linker File");
        dialog.setContentText("Linker:");
        dialog.showAndWait().ifPresent(linker -> {
            project.getTestDriver().getLinker().add(linker);
            LinkerList.getItems().add(linker);
        });
    }

    @FXML
    public void removeSelectedLinker(){
        String selectedLinker = LinkerList.getSelectionModel().getSelectedItem();
        if(selectedLinker == null){
            return;
        }
        project.getTestDriver().getLinker().remove(selectedLinker);
        LinkerList.getItems().remove(selectedLinker);
    }

    @FXML
    public void addIncludeDirectory(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Include Directory");
        dialog.setHeaderText("Enter the Include Directory");
        dialog.setContentText("Include Directory:");
        dialog.showAndWait().ifPresent(directory -> {
            project.getTestDriver().getIncludeDirectories().add(directory);
            IncludeList.getItems().add(directory);
        });
    }

    @FXML
    public void removeSelectedDirectory(){
        String selectedDirectory = IncludeList.getSelectionModel().getSelectedItem();
        if(selectedDirectory == null){
            return;
        }
        project.getTestDriver().getIncludeDirectories().remove(selectedDirectory);
        IncludeList.getItems().remove(selectedDirectory);
    }

    public void updateDefines(CDefine define){
        project.getTestDriver().getDefines().add(define);
        DefinesList.getItems().add(define);
    }

}