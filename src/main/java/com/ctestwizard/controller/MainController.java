package com.ctestwizard.controller;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CEnumInstance;
import com.ctestwizard.model.entity.CVariable;
import com.ctestwizard.model.testdriver.TDriver;
import com.ctestwizard.model.testdriver.TResults;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import com.ctestwizard.view.TableFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


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
        InterfaceBox.getChildren().add(TableFactory.createExternalFunctionsTable(selectedObject.getTestInterface()));
        InterfaceBox.getChildren().add(TableFactory.createLocalFunctionsTable(selectedObject.getTestInterface()));
        InterfaceBox.getChildren().add(TableFactory.createGlobalsTable(selectedObject.getTestInterface()));
        InterfaceBox.getChildren().add(TableFactory.createUserGlobalsTable(selectedObject.getTestInterface()));

        //Add the test cases to the test case list
        TestCaseList.getItems().clear();
        selectedObject.getTestCases().forEach(testCase -> TestCaseList.getItems().add(testCase));
    }

    @FXML
    public void handleTCaseClick(){
        //Add the test data to the test data box
        TestDataBox.getChildren().clear();
        TCase selectedCase = TestCaseList.getSelectionModel().getSelectedItem();
        if(selectedCase == null){
            return;
        }
        NewTestStepBtn.setDisable(false);
        TestDataBox.getChildren().add(TableFactory.createParameterTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createInputGlobalsTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createOutputGlobalsTable(selectedCase));
        TestDataBox.getChildren().add(TableFactory.createOutputTable(selectedCase));
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

    public void addEntryToUserGlobalsTable(CElement entry){
        TableView<CElement> userGlobalsTable = (TableView<CElement>) InterfaceBox.getChildren().get(4);
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
}