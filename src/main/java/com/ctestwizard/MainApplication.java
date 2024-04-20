package com.ctestwizard;

import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CVariable;
import com.ctestwizard.model.testdriver.TDriverUtils;
import com.ctestwizard.model.testdriver.TResults;
import com.ctestwizard.model.testentity.*;
import com.ctestwizard.view.TableFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TProject project = TProject.newTProject("src/main/resources/test_project/test.c", "src/main/resources/test_project");
        TObject object = project.getTestObjects().get(0);
        TInterface testInterface = object.getTestInterface();
        int index = 0;
        for(CElement element : testInterface.getGlobals().keySet()){
            if(index == 0){
                testInterface.getGlobals().put(element,TPassing.IN);
                index++;
            }else {
                testInterface.getGlobals().put(element,TPassing.INOUT);
            }

        }
        TCase testCase = object.getTestCases().get(0);
        testCase.newTStep();
        testCase.update();
        TreeTableView<CElement> parameterTable = TableFactory.createParameterTable(testCase);
        TreeTableView<CElement> outputTable = TableFactory.createOutputTable(testCase);
        TreeTableView<CElement> inputGlobalsTable = TableFactory.createInputGlobalsTable(testCase);
        TreeTableView<CElement> outputGlobalsTable = TableFactory.createOutputGlobalsTable(testCase);
        VBox vBox = new VBox();
        vBox.getChildren().add(TableFactory.createGlobalsTable(testInterface));
        vBox.getChildren().add(parameterTable);
        vBox.getChildren().add(outputTable);
        vBox.getChildren().add(inputGlobalsTable);
        vBox.getChildren().add(outputGlobalsTable);
        Button runTest = new Button("Run Test");
        runTest.setOnAction(e -> {
            try {
                List<TResults> resultsList = project.getTestDriver().executeTestObject(object);
                for(TResults results : resultsList){
                    System.out.println(results);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        vBox.getChildren().add(runTest);
        Scene scene = new Scene(vBox, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}