package com.ctestwizard.controller;

import com.ctestwizard.model.code.entity.CDefine;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the create define dialog
 */
public class CreateDefineController {

    private TestExecutionSettingsController parentController;
    private Stage stage;
    @FXML
    private TextField DefineName;
    @FXML
    private TextField DefineValue;

    /**
     * Set up the controller
     * @param parentController The parent controller
     * @param stage The stage
     */
    public void setup(TestExecutionSettingsController parentController, Stage stage){
        this.parentController = parentController;
        this.stage = stage;
    }

    /**
     * Method used to create a define
     */
    @FXML
    public void createDefine(){
        String name = DefineName.getText();
        if(name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Define Name cannot be empty");
            alert.showAndWait();
            return;
        }
        String value = DefineValue.getText();
        CDefine define = new CDefine(name, value);
        parentController.updateDefines(define);
        stage.close();
    }
}
