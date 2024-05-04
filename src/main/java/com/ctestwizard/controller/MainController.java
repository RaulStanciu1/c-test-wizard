package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.entity.CDefine;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;
import com.ctestwizard.model.testdriver.TDriver;
import com.ctestwizard.model.testdriver.TProperty;
import com.ctestwizard.model.testdriver.TResults;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import com.ctestwizard.view.TableFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


public class MainController{
    private TProject project;
    private Stage parentStage;
    @FXML
    private TabPane MainPane;
    @FXML
    private ListView<TObject> TestObjectList;
    @FXML
    private ListView<TCase> TestCaseList;
    @FXML
    private VBox InterfaceBox;
    @FXML
    private VBox TestDataBox;
    @FXML
    private Label MemoryUsed;
    @FXML
    private Button ExecuteTestObjectBtn;
    @FXML
    private Button NewTestCaseBtn;
    @FXML
    private Button NewTestStepBtn;
    @FXML
    private TextArea Console;
    @FXML
    private Button NewUserGlobalBtn;
    @FXML
    private ListView<CFunction> StubCodeList;
    @FXML
    private Label StubFunctionSignatureLabel;
    @FXML
    private TextField StubFunctionSignature;
    @FXML
    private Label StubFunctionBodyLabel;
    @FXML
    private TextArea StubFunctionBody;
    @FXML
    private TableView<TProperty> PropertyTable;
    @FXML
    private ListView<CDefine> DefinesList;
    @FXML
    private ListView<String> LinkerList;
    @FXML
    private ListView<String> IncludeList;
    @FXML
    private Label TestCaseTitleLabel;
    @FXML
    private TextField TestCaseTitle;
    @FXML
    private Label TestCaseDescriptionLabel;
    @FXML
    private TextArea TestCaseDescription;
    public void setup(TProject project, Stage parentStage){
        this.project = project;
        this.parentStage = parentStage;
    }
    public void init(){
        TestObjectList.getItems().clear();
        TestObjectList.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(TObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTestFunction() == null) {
                    setText(null);
                } else {
                    setText(item.getTestFunction().getName());
                }
            }

        });
        TestCaseList.getItems().clear();
        TestCaseList.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(TCase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item.getId()));
                }
            }

        });
        project.getTestObjects().forEach(testObject -> TestObjectList.getItems().add(testObject));
        startMemoryMonitoring();

        MainPane.getSelectionModel().selectedItemProperty().addListener((observable,oldTab,newTab) -> {
            if(newTab.getText().equals("Data Editor") && oldTab.getText().equals("Interface Editor")){
                if(TestCaseList.getSelectionModel().getSelectedItem() != null){
                    handleTCaseClick();
                }
            }
        });

        ExecuteTestObjectBtn.setDisable(true);
        NewTestCaseBtn.setDisable(true);
        NewTestStepBtn.setDisable(true);

        setupPropertyTable();
        setupDefinesList();
        setupLinkerList();
        setupIncludeList();
    }
    @FXML
    public void handleTObjectClick(){
        TObject selectedObject = TestObjectList.getSelectionModel().getSelectedItem();

        if(selectedObject == null){
            return;
        }
        NewTestCaseBtn.setDisable(false);
        ExecuteTestObjectBtn.setDisable(false);
        NewTestStepBtn.setDisable(true);
        //Add the interface to the interface box
        InterfaceBox.getChildren().removeIf(node -> !(node instanceof ButtonBar));
        NewUserGlobalBtn.setOpacity(1);
        InterfaceBox.getChildren().add(TableFactory.createExternalFunctionsTable(selectedObject.getTestInterface(),tInterface->{
            //Refresh the stub code list
            StubCodeList.getItems().clear();
            Map<CFunction,String> stubFunctions = tInterface.getStubCode();
            stubFunctions.forEach((function, body) -> StubCodeList.getItems().add(function));
        }));
        InterfaceBox.getChildren().add(TableFactory.createGlobalsTable(selectedObject.getTestInterface()));
        InterfaceBox.getChildren().add(TableFactory.createUserGlobalsTable(selectedObject.getTestInterface()));

        //Add the test cases to the test case list
        TestCaseList.getItems().clear();
        selectedObject.getTestCases().forEach(testCase -> TestCaseList.getItems().add(testCase));

        //Add the stub functions
        StubCodeList.getItems().clear();
        Map<CFunction,String> stubFunctions = selectedObject.getTestInterface().getStubCode();
        stubFunctions.forEach((function, body) -> StubCodeList.getItems().add(function));
        //Upon clicking an element in the stub code list, display the function signature and body
        StubCodeList.getSelectionModel().selectedItemProperty().addListener((observable,oldFunction,newFunction) -> {
            if(newFunction == null){
                StubFunctionSignature.setText("");
                StubFunctionBody.setText("");
                StubFunctionSignatureLabel.setDisable(true);
                StubFunctionBodyLabel.setDisable(true);
                StubFunctionSignature.setDisable(true);
                StubFunctionBody.setDisable(true);
            }else{
                StubFunctionSignature.setText(newFunction.getFunctionSignature());
                StubFunctionBody.setText(stubFunctions.get(newFunction));
                StubFunctionSignatureLabel.setDisable(false);
                StubFunctionBodyLabel.setDisable(false);
                StubFunctionSignature.setDisable(false);
                StubFunctionBody.setDisable(false);
            }
        });
    }

    @FXML
    public void handleTCaseClick(){
        //Add the test data to the test data box
        TestDataBox.getChildren().clear();
        TCase selectedCase = TestCaseList.getSelectionModel().getSelectedItem();
        if(selectedCase == null){
            return;
        }
        TestCaseTitle.setText(selectedCase.getTitle());
        TestCaseDescription.setText(selectedCase.getDescription());
        TestCaseTitleLabel.setDisable(false);
        TestCaseDescriptionLabel.setDisable(false);
        TestCaseTitle.setDisable(false);
        TestCaseDescription.setDisable(false);
        NewTestStepBtn.setDisable(false);
        TestDataBox.getChildren().add(TableFactory.createParameterTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createInputGlobalsTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createOutputGlobalsTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createOutputTable(selectedCase));
    }

    @FXML
    public void changeTitle(){
        TCase selectedCase = TestCaseList.getSelectionModel().getSelectedItem();
        if(selectedCase == null){
            return;
        }
        selectedCase.setTitle(TestCaseTitle.getText());
    }

    @FXML
    public void changeDescription(){
        TCase selectedCase = TestCaseList.getSelectionModel().getSelectedItem();
        if(selectedCase == null){
            return;
        }
        selectedCase.setDescription(TestCaseDescription.getText());
    }

    @FXML
    public void handleCreateNewTestCase(){
        //Create a new test case in memory
        TObject selectedObject = TestObjectList.getSelectionModel().getSelectedItem();
        if(selectedObject == null){
            return;
        }
        TCase newCase = TCase.newTestCase(selectedObject);
        selectedObject.getTestCases().add(newCase);
        TestCaseList.getItems().add(newCase);
    }

    @FXML
    public void handleExecuteTestObject(){
        try{
            TObject selectedTestObject = TestObjectList.getSelectionModel().getSelectedItem();
            if(selectedTestObject == null){
                throw new Exception("No Test Object Selected");
            }
            TDriver testDriver = selectedTestObject.getParent().getTestDriver();
            Console.appendText("-----Executing "+selectedTestObject.getTestFunction().getName()+"-----\n");
            List<TResults> testResults = testDriver.executeTestObject(selectedTestObject,(p)->{
                // Redirect process output to UI
                new Thread(() -> {
                    try{
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            final String finalLine = line; // Final variable for access in lambda
                            Platform.runLater(() -> Console.appendText(finalLine + "\n"));
                        }
                    }catch(IOException e){
                        throw new RuntimeException(e.getMessage());
                    }
                }).start();

                // Redirect process error output to UI
                new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            final String finalLine = line; // Final variable for access in lambda
                            Platform.runLater(() -> Console.appendText(finalLine + "\n"));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }).start();
            });
            Console.appendText("-----Finished Test Object Execution Successfully-----\n");
            Console.appendText("-----Comparing Test Results-----\n");
            selectedTestObject.compareTestResults(testResults);
            Console.appendText("-----Finished Comparing Test Results-----\n");
            //Update the test data in the UI
            handleTCaseClick();
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleNewTestStep(){
        TCase selectedCase = TestCaseList.getSelectionModel().getSelectedItem();
        if(selectedCase == null){
            return;
        }
        selectedCase.newTStep();
        TreeTableView<CElement> parameterTable = (TreeTableView<CElement>) TestDataBox.getChildren().get(0);
        TreeTableView<CElement> inputGlobalsTable = (TreeTableView<CElement>) TestDataBox.getChildren().get(1);
        TreeTableView<CElement> outputGlobalsTable = (TreeTableView<CElement>) TestDataBox.getChildren().get(2);
        TreeTableView<CElement> outputTable = (TreeTableView<CElement>) TestDataBox.getChildren().get(3);
        TableFactory.addTestStep(parameterTable);
        TableFactory.addTestStep(inputGlobalsTable);
        TableFactory.addTestStep(outputGlobalsTable);
        TableFactory.addTestStep(outputTable);
    }

    @FXML
    public void handleNewUserGlobal() throws IOException{
        TObject selectedObject = TestObjectList.getSelectionModel().getSelectedItem();
        if(selectedObject == null){
            return;
        }
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.initOwner(parentStage);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("user-global-form-view.fxml"));
        Parent root = loader.load();
        UserGlobalFormController controller = loader.getController();
        controller.setup(selectedObject,project,formStage,this);
        controller.init();
        formStage.setScene(new Scene(root));
        formStage.setResizable(false);
        formStage.show();
    }

    @FXML
    public void setStubBody(){
        TObject selectedObject = TestObjectList.getSelectionModel().getSelectedItem();
        if(selectedObject == null){
            return;
        }
        CFunction selectedFunction = StubCodeList.getSelectionModel().getSelectedItem();
        if(selectedFunction == null){
            return;
        }
        selectedObject.getTestInterface().getStubCode().put(selectedFunction,StubFunctionBody.getText());
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

            }
        });
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

    @FXML
    public void saveProject(){
        try{
            TProject.archiveProject(project);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Project Saved");
            alert.showAndWait();
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save project");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void closeProject(){

    }

    @FXML
    public void staticAnalysis(){
        
    }

    public void updateDefines(CDefine define){
        project.getTestDriver().getDefines().add(define);
        DefinesList.getItems().add(define);
    }

    public void addEntryToUserGlobalsTable(CElement entry){
        TableView<CElement> userGlobalsTable = (TableView<CElement>) InterfaceBox.getChildren().get(3);
        userGlobalsTable.getItems().add(entry);
    }

    private void startMemoryMonitoring(){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                double usedMemoryMB = memory / (1024.0 * 1024.0);
                MemoryUsed.setText(String.format("%.2f MB", usedMemoryMB));
            }
        };
        timer.start();
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
                new TProperty("Test Header",project.getTestDriver().getTestHeaderPath())
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
}