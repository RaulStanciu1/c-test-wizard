package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;

import com.ctestwizard.model.code.entity.CElement;
import com.ctestwizard.model.code.entity.CFunction;
import com.ctestwizard.model.exception.InterfaceChangedException;
import com.ctestwizard.model.test.driver.*;
import com.ctestwizard.model.test.entity.TCase;
import com.ctestwizard.model.test.entity.TObject;
import com.ctestwizard.model.test.entity.TProject;
import com.ctestwizard.view.entity.TCaseTable;
import com.ctestwizard.view.entity.TObjectTable;
import com.ctestwizard.view.table.TableFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The controller for the main view of the application(the project view)
 */
public class MainController{
    private TProject project;
    private Stage parentStage;
    @FXML
    private TabPane MainPane;
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
    private Label TestCaseTitleLabel;
    @FXML
    private TextField TestCaseTitle;
    @FXML
    private Label TestCaseDescriptionLabel;
    @FXML
    private TextArea TestCaseDescription;
    @FXML
    private TableView<TObjectTable> TestObjectTable;
    @FXML
    private TableColumn<TObjectTable, String> TestObjectColumn;
    @FXML
    private TableColumn<TObjectTable, Image> TestObjectRsColumn;
    @FXML
    private TableColumn<TObjectTable, Image> TestObjectCovColumn;
    @FXML
    private TableView<TCaseTable> TestCaseTable;
    @FXML
    private TableColumn<TCaseTable, String> TestCaseIdColumn;
    @FXML
    private TableColumn<TCaseTable, String> TestCaseTitleColumn;
    @FXML
    private TableColumn<TCaseTable, Image> TestCaseRsColumn;
    @FXML
    private TextArea PrologueCode;
    @FXML
    private TextArea EpilogueCode;

    /**
     * Method used to set up the controller data
     * @param project The project to be set up
     * @param parentStage The parent stage of the project
     */
    public void setup(TProject project, Stage parentStage){
        this.project = project;
        this.parentStage = parentStage;
    }

    /**
     * Initialize all the ui elements with the data from the project
     */
    public void init(){
        TestObjectTable.getItems().clear();
        TestObjectColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTestObject().getTestFunction().getName()));
        TestObjectRsColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getResultImage()));
        TestObjectRsColumn.setCellFactory(param -> new TableCell<>(){
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView(item);
                    imageView.setFitHeight(16);
                    imageView.setFitWidth(16);
                    setGraphic(imageView);
                }
            }
        });
        TestObjectCovColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCoverageImage()));
        TestObjectCovColumn.setCellFactory(param -> new TableCell<>(){
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView(item);
                    imageView.setFitHeight(16);
                    imageView.setFitWidth(16);
                    setGraphic(imageView);
                }
            }
        });
        project.getTestObjects().forEach(testObject -> TestObjectTable.getItems().add(new TObjectTable(testObject)));

        TestCaseTable.getItems().clear();
        TestCaseIdColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getTestCase().getId())));
        TestCaseTitleColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTestCase().getTitle()));

        TestCaseRsColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getResultImage()));
        TestCaseRsColumn.setCellFactory(param -> new TableCell<>(){
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView(item);
                    imageView.setFitHeight(16);
                    imageView.setFitWidth(16);
                    setGraphic(imageView);
                }
            }
        });
        startMemoryMonitoring();

        MainPane.getSelectionModel().selectedItemProperty().addListener((observable,oldTab,newTab) -> {
            if(newTab.getText().equals("Data Editor") && oldTab.getText().equals("Interface Editor")){
                if(TestCaseTable.getSelectionModel().getSelectedItem() != null){
                    handleTCaseClick();
                }
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeTestCase = getMenuItem();
        contextMenu.getItems().add(removeTestCase);
        TestCaseTable.setContextMenu(contextMenu);

        ExecuteTestObjectBtn.setDisable(true);
        NewTestCaseBtn.setDisable(true);
        NewTestStepBtn.setDisable(true);
    }

    /**
     * Private method used to create the menu item for deleting a test case
     * @return The menu item for deleting a test case
     */
    private MenuItem getMenuItem() {
        MenuItem removeTestCase = new MenuItem("Delete Test Case");
        removeTestCase.setOnAction(event -> {
            TCaseTable selectedTable = TestCaseTable.getSelectionModel().getSelectedItem();
            if(selectedTable == null){
                return;
            }
            TCase selectedCase = selectedTable.getTestCase();
            if(selectedCase == null){
                return;
            }
            TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
            if(selectedObject == null){
                return;
            }
            selectedObject.getTestCases().remove(selectedCase);
            TestCaseTable.getItems().remove(selectedTable);
        });
        return removeTestCase;
    }

    /**
     * Method to open the test execution settings form
     * @throws IOException If the form could not be loaded
     */
    @FXML
    public void handleTestExecutionSettingsClick() throws IOException {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.initOwner(parentStage);
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("test-execution-settings-view.fxml"));
        Parent root = loader.load();
        TestExecutionSettingsController controller = loader.getController();
        controller.setup(project,formStage);
        controller.init();
        formStage.setScene(new Scene(root));
        formStage.setResizable(false);
        formStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/icon.png"))));
        formStage.show();
    }

    /**
     * Method to handle the event when the user clicks on a test object (show the test object details)
     */
    @FXML
    public void handleTObjectClick(){
        MainPane.getTabs().get(0).setDisable(false);
        MainPane.getTabs().get(1).setDisable(false);
        MainPane.getTabs().get(2).setDisable(false);
        MainPane.getTabs().get(3).setDisable(true);
        PrologueCode.setDisable(false);
        EpilogueCode.setDisable(false);
        MainPane.getSelectionModel().select(0);
        TObjectTable selectedTable = TestObjectTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TObject selectedObject = selectedTable.getTestObject();
        if(selectedObject == null){
            return;
        }
        PrologueCode.setText(selectedObject.getPrologueCode());
        EpilogueCode.setText(selectedObject.getEpilogueCode());
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
        TestCaseTable.getItems().clear();
        selectedObject.getTestCases().forEach(testCase -> TestCaseTable.getItems().add(new TCaseTable(testCase)));

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

    /**
     * Method that handles the event when the user clicks on a test case from a test object
     */
    @FXML
    public void handleTCaseClick(){
        MainPane.getTabs().get(0).setDisable(true);
        MainPane.getTabs().get(1).setDisable(true);
        MainPane.getTabs().get(2).setDisable(true);
        MainPane.getTabs().get(3).setDisable(false);
        PrologueCode.setDisable(true);
        EpilogueCode.setDisable(true);
        MainPane.getSelectionModel().select(3);
        TCaseTable selectedTable = TestCaseTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        //Add the test data to the test data box
        TestDataBox.getChildren().clear();
        TCase selectedCase = selectedTable.getTestCase();
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

    /**
     * Method to change the title of a test case when the user changes it
     */
    @FXML
    public void changeTitle(){
        TCaseTable selectedTable = TestCaseTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TCase selectedCase = selectedTable.getTestCase();
        if(selectedCase == null){
            return;
        }
        selectedCase.setTitle(TestCaseTitle.getText());
        TestCaseTable.refresh();
    }

    /**
     * Method to change the description of a test case when the user changes it
     */
    @FXML
    public void changeDescription(){
        TCaseTable selectedTable = TestCaseTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TCase selectedCase = selectedTable.getTestCase();
        if(selectedCase == null){
            return;
        }
        selectedCase.setDescription(TestCaseDescription.getText());
    }

    /**
     * Method to create a new test case for a test object
     */
    @FXML
    public void handleCreateNewTestCase(){
        TObjectTable selectedTable = TestObjectTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        //Create a new test case in memory
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
        if(selectedObject == null){
            return;
        }
        TCase newCase = TCase.newTestCase(selectedObject);
        selectedObject.getTestCases().add(newCase);
        TestCaseTable.getItems().add(new TCaseTable(newCase));
    }

    /**
     * Method that takes the selected test object and runs the tests
     */
    @FXML
    public void handleExecuteTestObject() {
        try{
            Console.setText("");
            TObjectTable selectedTable = TestObjectTable.getSelectionModel().getSelectedItem();
            if(selectedTable == null){
                throw new Exception("No Test Object Selected");
            }
            TObject selectedTestObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
            if(selectedTestObject == null){
                throw new Exception("No Test Object Selected");
            }

            TDriver testDriver = selectedTestObject.getParent().getTestDriver();
            Console.appendText("-----Executing "+selectedTestObject.getTestFunction().getName()+"-----\n");
            TSummary testSummary = testDriver.executeTestObject(selectedTestObject,(p)->{
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
            selectedTestObject.compareTestResults(testSummary.getTestResults());
            Console.appendText("-----Finished Comparing Test Results-----\n");
            if(testSummary.getResultsPassed()){
                TestObjectTable.getSelectionModel().getSelectedItem().setRsStatus(1);
            }else{
                TestObjectTable.getSelectionModel().getSelectedItem().setRsStatus(0);
            }
            if(testSummary.getCoveragePassed() == 1){
                TestObjectTable.getSelectionModel().getSelectedItem().setCovStatus(1);
            }else if(testSummary.getCoveragePassed() == 0){
                TestObjectTable.getSelectionModel().getSelectedItem().setCovStatus(0);
            }else{
                TestObjectTable.getSelectionModel().getSelectedItem().setCovStatus(-1);
            }
            TestObjectTable.refresh();
            List<TResults> testResults = testSummary.getTestResults();
            for(int i = 0; i < testResults.size(); i++){
                TCaseTable testCaseTable = TestCaseTable.getItems().get(i);
                TResults testResult = testResults.get(i);
                testCaseTable.setResultStatus(testResult.getResultsPassed() ? 1 : 0);
            }
            TestCaseTable.refresh();
            //Update the test data in the UI
            handleTCaseClick();
            //Print the summary to the console
            Console.appendText("-----Test Summary-----\n");
            Console.appendText("Test Execution Status :"+(testSummary.getResultsPassed() ? "PASSED" : "FAILED")+"\n");
            if(testSummary.getCoveragePassed() != -1){
                Console.appendText("Coverage Status :"+(testSummary.getCoveragePassed() == 1 ? "PASSED" : "FAILED")+"\n");
            }
            Console.appendText("Total Test Cases :"+testSummary.getTotalTestCases()+"\n");
            Console.appendText("Total Test Steps :"+testSummary.getTotalTestSteps()+"\n");
            Console.appendText("Passed Test Steps :"+testSummary.getPassedTestSteps()+"\n");
            Console.appendText("-----End of Test Summary-----\n");
            if(project.getTestDriver().isReportEnabled()){
                ReportGenerator.generateReport(project,selectedTestObject,testSummary);
            }
        }catch(Exception e){
            if(e instanceof InterfaceChangedException){
                //Force open the interface editor tab
                handleTObjectClick();
                TestObjectTable.getItems().clear();
                project.getTestObjects().forEach(testObject -> TestObjectTable.getItems().add(new TObjectTable(testObject)));
                TestObjectTable.refresh();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method that creates a new test step for a test case
     */
    @FXML
    public void handleNewTestStep(){
        TCaseTable selectedTable = TestCaseTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TCase selectedCase = selectedTable.getTestCase();
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

    /**
     * Method that open the new user global form
     * @throws IOException If the form could not be loaded
     */
    @FXML
    public void handleNewUserGlobal() throws IOException{
        TObjectTable selectedTable = TestObjectTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
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

    /**
     * Method used to set the body of a stub function
     */
    @FXML
    public void setStubBody(){
        TObjectTable selectedTable = TestObjectTable.getSelectionModel().getSelectedItem();
        if(selectedTable == null){
            return;
        }
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
        if(selectedObject == null){
            return;
        }
        CFunction selectedFunction = StubCodeList.getSelectionModel().getSelectedItem();
        if(selectedFunction == null){
            return;
        }
        selectedObject.getTestInterface().getStubCode().put(selectedFunction,StubFunctionBody.getText());
    }

    /**
     * Method used to save the project
     */
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

    /**
     * Method used to close the project and return to the project list
     * @throws IOException If the project list form could not be loaded
     */
    @FXML
    public void closeProject() throws IOException {
        Stage formStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("project-list.fxml"));
        Parent root = loader.load();
        ProjectListController controller = loader.getController();
        controller.setup(formStage);
        controller.init();
        formStage.setScene(new Scene(root));
        formStage.setResizable(false);
        parentStage.close();
        formStage.show();
    }

    /**
     * Method used to run the static analysis on the selected test object
     */
    @FXML
    public void staticAnalysis(){
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
        if(selectedObject == null){
            return;
        }
        try{
            Console.setText("");
            Console.appendText("-----Static Analysis Log-----\n");
            List<String> log = StaticAnalyzer.staticAnalysis(selectedObject);
            if(log.isEmpty()) {
                Console.appendText("No issues found\n");
            }
            else{
                for(String line : log){
                    Console.appendText(line+"\n");
                }
            }
            Console.appendText("-----End of Static Analysis Log-----\n");
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Static Analysis");
            information.setHeaderText("Static Analysis Completed");
            information.setContentText("Check the console for log information");
            information.showAndWait();
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Static Analysis Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method used to set the prologue body of a test object
     */
    @FXML
    public void setPrologueBody(){
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
        if(selectedObject == null){
            return;
        }
        selectedObject.setPrologueCode(PrologueCode.getText());
    }

    /**
     * Method used to set the epilogue body of a test object
     */
    @FXML
    public void setEpilogueBody(){
        TObject selectedObject = TestObjectTable.getSelectionModel().getSelectedItem().getTestObject();
        if(selectedObject == null){
            return;
        }
        selectedObject.setEpilogueCode(EpilogueCode.getText());
    }

    /**
     * Method used to open this software's repository
     */
    @FXML
    public void openProjectRepo(){
        try{
            if(Desktop.isDesktopSupported()){
                Desktop.getDesktop().browse(URI.create("https://github.com/RaulStanciu1/c-test-wizard"));
            }
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open the Software repository");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Method used to add an entry to the user globals table
     * @param entry The entry to be added
     */
    public void addEntryToUserGlobalsTable(CElement entry){
        TableView<CElement> userGlobalsTable = (TableView<CElement>) InterfaceBox.getChildren().get(3);
        userGlobalsTable.getItems().add(entry);
    }

    /**
     * Method used to print the memory used by the application
     */
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
}