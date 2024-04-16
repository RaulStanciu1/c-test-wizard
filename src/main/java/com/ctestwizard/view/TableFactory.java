package com.ctestwizard.view;

import com.ctestwizard.model.entity.*;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TInterface;
import com.ctestwizard.model.testentity.TPassing;
import com.ctestwizard.model.testentity.TRoot;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;

import java.util.ArrayList;
import java.util.List;

public class TableFactory {
    public static TreeTableView<CElement> createParameterTable(TCase testCase){
        return createTable(testCase,testCase.getParameters(),"Parameters");
    }

    public static TreeTableView<CElement> createInputGlobalsTable(TCase testCase){
        return createTable(testCase,testCase.getInputGlobals(),"Input Globals");
    }

    public static TreeTableView<CElement> createOutputGlobalsTable(TCase testCase){
        return createTable(testCase,testCase.getOutputGlobals(),"Output Globals");
    }

    public static TreeTableView<CElement> createOutputTable(TCase testCase){
        TreeTableView<CElement> tableView = new TreeTableView<>();
        TreeTableColumn<CElement, String> inputGlobalColumn = new TreeTableColumn<>("");
        inputGlobalColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getType()+" "+param.getValue().getValue().getName()));
        tableView.getColumns().add(inputGlobalColumn);
        TreeItem<CElement> root = new TreeItem<>(new TRoot("Output"));
        if(testCase.getOutput().getType().strip().equals("void")){
            tableView.setEditable(false);
            tableView.setRoot(root);
            return tableView;
        }
        TreeItem<CElement> item = new TreeItem<>(testCase.getOutput());
        if(testCase.getOutput() instanceof CStructOrUnionInstance structOrUnionInstance){
            item = _getStructOrUnionChild(structOrUnionInstance);
        }else if(testCase.getOutput() instanceof CArray array){
            TreeItem<CElement> arrayItem = new TreeItem<>(array);
            for(CElement arrElement: array.getArrayMembers()){
                TreeItem<CElement> elementItem = new TreeItem<>(arrElement);
                if(arrElement instanceof CStructOrUnionInstance structOrUnionInstance){
                    elementItem = _getStructOrUnionChild(structOrUnionInstance);
                }
                arrayItem.getChildren().add(elementItem);
            }
            item = arrayItem;
        }
        root.getChildren().add(item);
        tableView.setEditable(true);
        tableView.setRoot(root);

        //Create the value columns based on the number of testSteps
        for(int i = 0; i < testCase.getTSteps(); i++){
            TreeTableColumn<CElement, String> valueColumn = getcElementStringTreeTableColumn(i);
            tableView.getColumns().add(valueColumn);
        }
        return tableView;
    }

    public static TableView<CElement> createExternalFunctionsTable(TInterface tInterface){
        return createFunctionTable(tInterface,tInterface.getExternalFunctions(),"External Functions");
    }

    public static TableView<CElement> createLocalFunctionsTable(TInterface tInterface){
        return createFunctionTable(tInterface,tInterface.getLocalFunctions(),"Local Functions");
    }

    public static TableView<CElement> createGlobalsTable(TInterface tInterface){
        ObservableList<TPassing> options = FXCollections.observableArrayList(TPassing.IN,TPassing.OUT,TPassing.INOUT,TPassing.NONE);
        TableColumn<CElement, String> globalColumn = new TableColumn<>("Globals");
        TableColumn<CElement, TPassing> passingColumn = new TableColumn<>("Passing");
        globalColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType()+" "+param.getValue().getName()));
        passingColumn.setCellFactory(ComboBoxTableCell.forTableColumn(options));
        passingColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(tInterface.getGlobals().get(param.getValue())));
        passingColumn.setOnEditCommit(event -> {
            tInterface.getGlobals().put(event.getRowValue(),event.getNewValue());
        });
        TableView<CElement> globalsTable = new TableView<>();
        globalsTable.getColumns().add(globalColumn);
        globalsTable.getColumns().add(passingColumn);
        globalsTable.getItems().addAll(new ArrayList<>(tInterface.getGlobals().keySet()));
        globalColumn.prefWidthProperty().bind(globalsTable.widthProperty().divide(2));
        globalsTable.setEditable(true);
        return globalsTable;
    }
    public static TableView<CElement> createUserGlobalsTable(TInterface tInterface){
        ObservableList<TPassing> options = FXCollections.observableArrayList(TPassing.IN,TPassing.OUT,TPassing.INOUT,TPassing.NONE);
        TableColumn<CElement, String> globalColumn = new TableColumn<>("Globals");
        TableColumn<CElement, TPassing> passingColumn = new TableColumn<>("Passing");
        globalColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType()+" "+param.getValue().getName()));
        passingColumn.setCellFactory(ComboBoxTableCell.forTableColumn(options));
        passingColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(tInterface.getUserGlobals().get(param.getValue())));
        passingColumn.setOnEditCommit(event -> {
            tInterface.getUserGlobals().put(event.getRowValue(),event.getNewValue());
        });
        TableView<CElement> globalsTable = new TableView<>();
        globalsTable.getColumns().add(globalColumn);
        globalsTable.getColumns().add(passingColumn);
        globalsTable.getItems().addAll(new ArrayList<>(tInterface.getGlobals().keySet()));
        globalColumn.prefWidthProperty().bind(globalsTable.widthProperty().divide(2));
        globalsTable.setEditable(true);
        return globalsTable;
    }

    private static TableView<CElement> createFunctionTable(TInterface tInterface,List<CFunction> functionsList, String columnName){
        TableView<CElement> functionsTable = new TableView<>();
        TableColumn<CElement, String> functionColumn = new TableColumn<>(columnName);
        functionColumn.setCellValueFactory(param -> new SimpleStringProperty(((CFunction)param.getValue()).getStrType()+" "+param.getValue().getName()+"()"));
        functionsTable.getColumns().add(functionColumn);
        functionsTable.getItems().addAll(functionsList);
        functionColumn.prefWidthProperty().bind(functionsTable.widthProperty());
        //Center the elements from the table columns
        functionColumn.setStyle("-fx-alignment: CENTER;");

        ContextMenu ctxMenu = new ContextMenu();
        MenuItem createStub = new MenuItem("Create Stub");
        MenuItem dontCreateStub = new MenuItem("Don't Create Stub");
        createStub.setOnAction(event -> {
            CFunction function = (CFunction) functionsTable.getSelectionModel().getSelectedItem();
            tInterface.getStubCode().put(function,"");
        });
        dontCreateStub.setOnAction(event -> {
            CFunction function = (CFunction) functionsTable.getSelectionModel().getSelectedItem();
            tInterface.getStubCode().remove(function);
        });

        ctxMenu.getItems().addAll(createStub,dontCreateStub);
        functionsTable.setContextMenu(ctxMenu);

        return functionsTable;
    }

    private static TreeTableView<CElement> createTable(TCase testCase,List<CElement> elements,String tableName){
        TreeTableView<CElement> tableView = new TreeTableView<>();
        TreeTableColumn<CElement, String> inputGlobalColumn = new TreeTableColumn<>("");
        inputGlobalColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getType()+" "+param.getValue().getValue().getName()));
        tableView.getColumns().add(inputGlobalColumn);
        TreeItem<CElement> root = new TreeItem<>(new TRoot(tableName));
        for(CElement element : elements){
            TreeItem<CElement> item = new TreeItem<>(element);
            if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                item = _getStructOrUnionChild(structOrUnionInstance);
            }else if(element instanceof CArray array){
                TreeItem<CElement> arrayItem = new TreeItem<>(array);
                for(CElement arrElement: array.getArrayMembers()){
                    TreeItem<CElement> elementItem = new TreeItem<>(arrElement);
                    if(arrElement instanceof CStructOrUnionInstance structOrUnionInstance){
                        elementItem = _getStructOrUnionChild(structOrUnionInstance);
                    }
                    arrayItem.getChildren().add(elementItem);
                }
                item = arrayItem;
            }
            root.getChildren().add(item);
        }
        tableView.setEditable(true);
        tableView.setRoot(root);

        //Create the value columns based on the number of testSteps
        for(int i = 0; i < testCase.getTSteps(); i++){
            TreeTableColumn<CElement, String> valueColumn = getcElementStringTreeTableColumn(i);
            tableView.getColumns().add(valueColumn);
        }
        return tableView;
    }

    private static TreeTableColumn<CElement, String> getcElementStringTreeTableColumn(int i) {
        TreeTableColumn<CElement, String> valueColumn = new TreeTableColumn<>(String.valueOf(i +1));
        int finalI = i;
        valueColumn.setCellValueFactory(param -> _valueColumnFactory(param, finalI));
        valueColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        //Make the column edit the values of the CElement when changed in the table
        valueColumn.setOnEditCommit(event -> {
            CElement element = event.getRowValue().getValue();
            if(element instanceof CVariable variable){
                variable.values.set(finalI, event.getNewValue());
            }else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers()!= 0){
                    structOrUnionInstance.values.set(finalI, event.getNewValue());
                }
            }
        });
        return valueColumn;
    }

    private static SimpleStringProperty _valueColumnFactory(TreeTableColumn.CellDataFeatures<CElement, String> param, int index){
        CElement element = param.getValue().getValue();
        if(element instanceof CVariable variable){
            return new SimpleStringProperty(variable.values.get(index));
        }else if(element instanceof CStructOrUnionInstance structOrUnionInstance) {
            if(structOrUnionInstance.getPointers()!= 0){
                return new SimpleStringProperty(structOrUnionInstance.values.get(index));
            }
        }
        return new SimpleStringProperty("");
    }
    private static TreeItem<CElement> _getStructOrUnionChild(CStructOrUnionInstance instance){
        TreeItem<CElement> root = new TreeItem<>(instance);
        for(CElement member : instance.getStructType().getMembers()){
            TreeItem<CElement> memberItem = new TreeItem<>(member);
            if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                memberItem = _getStructOrUnionChild(structOrUnionInstance);
            }else if(member instanceof CArray array){
                TreeItem<CElement> arrayItem = new TreeItem<>(array);
                for(CElement element : array.getArrayMembers()){
                    TreeItem<CElement> elementItem = new TreeItem<>(element);
                    if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                        elementItem = _getStructOrUnionChild(structOrUnionInstance);
                    }
                    arrayItem.getChildren().add(elementItem);
                }
                memberItem = arrayItem;
            }
            root.getChildren().add(memberItem);
        }
        return root;
    }
}
