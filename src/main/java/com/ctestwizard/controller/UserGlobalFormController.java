package com.ctestwizard.controller;

import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TObject;
import com.ctestwizard.model.test.entity.TPassing;
import com.ctestwizard.model.test.entity.TProject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserGlobalFormController {
    private TProject project;
    private List<String> globalNames;
    private TObject testObject;
    private Stage stage;
    private MainController parentController;
    @FXML
    private TreeView<String> TypeTreeView;
    @FXML
    private TextField NameTextField;
    @FXML
    private Label OneD_OneDSize_Label;
    @FXML
    private TextField OneD_OneDSize;
    @FXML
    private Label TwoD_OneDSize_Label;
    @FXML
    private TextField TwoD_OneDSize;
    @FXML
    private Label TwoD_TwoDSize_Label;
    @FXML
    private TextField TwoD_TwoDSize;
    @FXML
    private Label ThreeD_OneDSize_Label;
    @FXML
    private TextField ThreeD_OneDSize;
    @FXML
    private Label ThreeD_TwoDSize_Label;
    @FXML
    private TextField ThreeD_TwoDSize;
    @FXML
    private Label ThreeD_ThreeDSize_Label;
    @FXML
    private TextField ThreeD_ThreeDSize;
    @FXML
    private RadioButton VariableBtn;
    @FXML
    private RadioButton PointerToVariableBtn;
    @FXML
    private RadioButton PointerToPointerBtn;
    @FXML
    private RadioButton NoArrayBtn;
    @FXML
    private RadioButton OneDArrayBtn;
    @FXML
    private RadioButton TwoDArrayBtn;
    @FXML
    private RadioButton ThreeDArrayBtn;

    public void setup(TObject testObject, TProject project, Stage stage, MainController parentController){
        this.parentController = parentController;
        this.project = project;
        this.testObject = testObject;
        this.stage = stage;
        globalNames = new ArrayList<>();
        for(CElement global : testObject.getTestInterface().getGlobals().keySet()){
            globalNames.add(global.getName());
        }
        for(CElement userGlobal : testObject.getTestInterface().getUserGlobals().keySet()){
            globalNames.add(userGlobal.getName());
        }
    }
    public void init(){
        setTypeTreeView();
    }

    @FXML
    public void selectNoArray(){
        disableAllArrayProperties();
    }

    @FXML
    public void select1DArray(){
        enable1DArrayProperties();
    }

    @FXML
    public void select2DArray(){
        enable2DArrayProperties();
    }

    @FXML
    public void select3DArray(){
        enable3DArrayProperties();
    }

    @FXML
    public void createUserGlobal(){
        try{
            checkNameValidity();
            String name = NameTextField.getText();
            checkTypeValidity();
            String type = TypeTreeView.getSelectionModel().getSelectedItem().getValue();
            int pointers = getPointers();
            CElement userGlobal;
            if(NoArrayBtn.isSelected()){
                userGlobal = createVariable(name,type,pointers);
            }else if(OneDArrayBtn.isSelected()) {
                check1DArrayProperties();
                userGlobal = create1DArray(name, type, pointers);
            }else if(TwoDArrayBtn.isSelected()) {
                check2DArrayProperties();
                userGlobal = create2DArray(name, type, pointers);
            }else if(ThreeDArrayBtn.isSelected()) {
                check3DArrayProperties();
                userGlobal = create3DArray(name, type, pointers);
            }else{
                throw new IllegalArgumentException("Please select an array type");
            }
            testObject.getTestInterface().getUserGlobals().put(userGlobal,TPassing.NONE);
            //Get parent stage and update the user global table
            parentController.addEntryToUserGlobalsTable(userGlobal);
            stage.close();
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void check3DArrayProperties(){
        if(ThreeD_OneDSize.getText().isEmpty() || ThreeD_TwoDSize.getText().isEmpty() || ThreeD_ThreeDSize.getText().isEmpty()){
            throw new IllegalArgumentException("Size cannot be empty");
        }
        if(!ThreeD_OneDSize.getText().matches("[0-9]+") || !ThreeD_TwoDSize.getText().matches("[0-9]+") || !ThreeD_ThreeDSize.getText().matches("[0-9]+")){
            throw new IllegalArgumentException("Invalid size");
        }
    }

    private CElement create3DArray(String name, String type, int pointers){
        int size1;
        int size2;
        int size3;
        try{
            size1 = Integer.parseInt(ThreeD_OneDSize.getText());
            size2 = Integer.parseInt(ThreeD_TwoDSize.getText());
            size3 = Integer.parseInt(ThreeD_ThreeDSize.getText());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid size");
        }
        if(size1 <= 0 || size2 <= 0 || size3 <= 0){
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        CElement variable = createVariable(name,type,pointers);
        variable.setName(variable.getName()+"["+size1+"]["+size2+"]["+size3+"]");
        return new CArray(variable);
    }

    private void check2DArrayProperties(){
        if(TwoD_OneDSize.getText().isEmpty() || TwoD_TwoDSize.getText().isEmpty()){
            throw new IllegalArgumentException("Size cannot be empty");
        }
        if(!TwoD_OneDSize.getText().matches("[0-9]+") || !TwoD_TwoDSize.getText().matches("[0-9]+")){
            throw new IllegalArgumentException("Invalid size");
        }
    }

    private CElement create2DArray(String name, String type, int pointers){
        int size1;
        int size2;
        try{
            size1 = Integer.parseInt(TwoD_OneDSize.getText());
            size2 = Integer.parseInt(TwoD_TwoDSize.getText());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid size");
        }
        if(size1 <= 0 || size2 <= 0){
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        CElement variable = createVariable(name,type,pointers);
        variable.setName(variable.getName()+"["+size1+"]["+size2+"]");
        return new CArray(variable);
    }

    private void check1DArrayProperties(){
        if(OneD_OneDSize.getText().isEmpty()){
            throw new IllegalArgumentException("Size cannot be empty");
        }
        if(!OneD_OneDSize.getText().matches("[0-9]+")){
            throw new IllegalArgumentException("Invalid size");
        }
    }

    private CElement create1DArray(String name, String type, int pointers){
        int size;
        try{
            size = Integer.parseInt(OneD_OneDSize.getText());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid size");
        }
        if(size <= 0){
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        CElement variable = createVariable(name,type,pointers);
        variable.setName(variable.getName()+"["+size+"]");
        return new CArray(variable);
    }

    private CElement createVariable(String name, String type, int pointers){
        //Check if the variable type is a struct or enum
        for(int i = 0; i < project.getStructOrUnionTypes().size(); i++){
            if(project.getStructOrUnionTypes().get(i).getName().equals(type)){
                CStructOrUnion structType = (CStructOrUnion) project.getStructOrUnionTypes().get(i);
                return new CStructOrUnionInstance(structType,name,pointers);
            }
        }
        for(int i = 0; i < project.getEnumTypes().size(); i++){
            if(project.getEnumTypes().get(i).getName().equals(type)){
                CEnum enumType = (CEnum) project.getEnumTypes().get(i);
                return new CEnumInstance(enumType,name,pointers);
            }
        }
        return new CVariable(type,name,pointers);
    }

    private int getPointers(){
        if(VariableBtn.isSelected()){
            return 0;
        }
        if(PointerToVariableBtn.isSelected()){
            return 1;
        }
        if(PointerToPointerBtn.isSelected()){
            return 2;
        }
        throw new IllegalArgumentException("Please select a pointer type");
    }

    private void checkTypeValidity(){
        if(TypeTreeView.getSelectionModel().getSelectedItem() == null){
            throw new IllegalArgumentException("Please select a type");
        }
        String selectedType = TypeTreeView.getSelectionModel().getSelectedItem().getValue();
        if(selectedType == null){
            throw new IllegalArgumentException("Please select a type");
        }
    }

    private void checkNameValidity(){
        String name = NameTextField.getText();
        if(name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if(globalNames.contains(name)){
            throw new IllegalArgumentException("Name already exists");
        }
        if(!name.matches("[a-zA-Z_][a-zA-Z0-9_]*")){
            throw new IllegalArgumentException("Invalid name");
        }
    }

    private void disableAllArrayProperties(){
        OneD_OneDSize_Label.setDisable(true);
        OneD_OneDSize.setDisable(true);
        TwoD_OneDSize_Label.setDisable(true);
        TwoD_OneDSize.setDisable(true);
        TwoD_TwoDSize_Label.setDisable(true);
        TwoD_TwoDSize.setDisable(true);
        ThreeD_OneDSize_Label.setDisable(true);
        ThreeD_OneDSize.setDisable(true);
        ThreeD_TwoDSize_Label.setDisable(true);
        ThreeD_TwoDSize.setDisable(true);
        ThreeD_ThreeDSize_Label.setDisable(true);
    }

    private void enable1DArrayProperties(){
        OneD_OneDSize_Label.setDisable(false);
        OneD_OneDSize.setDisable(false);
        TwoD_OneDSize_Label.setDisable(true);
        TwoD_OneDSize.setDisable(true);
        TwoD_TwoDSize_Label.setDisable(true);
        TwoD_TwoDSize.setDisable(true);
        ThreeD_OneDSize_Label.setDisable(true);
        ThreeD_OneDSize.setDisable(true);
        ThreeD_TwoDSize_Label.setDisable(true);
        ThreeD_TwoDSize.setDisable(true);
        ThreeD_ThreeDSize_Label.setDisable(true);
    }

    private void enable2DArrayProperties(){
        OneD_OneDSize_Label.setDisable(true);
        OneD_OneDSize.setDisable(true);
        TwoD_OneDSize_Label.setDisable(false);
        TwoD_OneDSize.setDisable(false);
        TwoD_TwoDSize_Label.setDisable(false);
        TwoD_TwoDSize.setDisable(false);
        ThreeD_OneDSize_Label.setDisable(true);
        ThreeD_OneDSize.setDisable(true);
        ThreeD_TwoDSize_Label.setDisable(true);
        ThreeD_TwoDSize.setDisable(true);
        ThreeD_ThreeDSize_Label.setDisable(true);
    }

    private void enable3DArrayProperties(){
        OneD_OneDSize_Label.setDisable(true);
        OneD_OneDSize.setDisable(true);
        TwoD_OneDSize_Label.setDisable(true);
        TwoD_OneDSize.setDisable(true);
        TwoD_TwoDSize_Label.setDisable(true);
        TwoD_TwoDSize.setDisable(true);
        ThreeD_OneDSize_Label.setDisable(false);
        ThreeD_OneDSize.setDisable(false);
        ThreeD_TwoDSize_Label.setDisable(false);
        ThreeD_TwoDSize.setDisable(false);
        ThreeD_ThreeDSize_Label.setDisable(false);
        ThreeD_ThreeDSize.setDisable(false);
    }

    private void setTypeTreeView(){
        TreeItem<String> root = new TreeItem<>("Types");
        TreeItem<String> basicType = new TreeItem<>("Basic Types");
        TreeItem<String> structType = new TreeItem<>("Struct or Union Types");
        TreeItem<String> enumType = new TreeItem<>("Enum Types");
        String[] basicTypes = {"unsigned char","unsigned short","unsigned int","unsigned long",
                "char","short","int","long","float","double"};
        for(String type : basicTypes){
            basicType.getChildren().add(new TreeItem<>(type));
        }
        List<String> structTypes = new ArrayList<>();
        List<String> enumTypes = new ArrayList<>();
        for(int i = 0; i < project.getStructOrUnionTypes().size();i++){
            structTypes.add(project.getStructOrUnionTypes().get(i).getName());
        }
        for(int i = 0; i < project.getEnumTypes().size();i++){
            enumTypes.add(project.getEnumTypes().get(i).getName());
        }
        for(String type : structTypes){
            structType.getChildren().add(new TreeItem<>(type));
        }
        for(String type : enumTypes){
            enumType.getChildren().add(new TreeItem<>(type));
        }
        root.getChildren().addAll(basicType,structType,enumType);
        TypeTreeView.setRoot(root);
    }
}
