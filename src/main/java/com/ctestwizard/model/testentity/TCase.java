package com.ctestwizard.model.testentity;


import com.ctestwizard.model.entity.*;
import com.ctestwizard.model.testdriver.TResults;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TCase implements Serializable {
    private final TObject parent;
    private int id;
    private String title;
    private String description;
    private List<CElement> parameters;
    private List<CElement> inputGlobals;
    private List<CElement> outputGlobals;
    private CElement output;
    private int tSteps;
    public TCase(TObject parent, int id){
        this.id = id;
        this.tSteps = 0;
        this.parent = parent;
        this.title = "";
        this.description = "";
        List<CElement> parameters = new ArrayList<>();
        List<CElement> inputGlobals = new ArrayList<>();
        List<CElement> outputGlobals = new ArrayList<>();
        this.output = parent.getTestInterface().getOutput().clone();
        for(CElement parameter : parent.getTestInterface().getParameters()) {
            parameters.add(parameter.clone());
        }
        for(CElement global : parent.getTestInterface().getGlobals().keySet()) {
            if(parent.getTestInterface().getGlobals().get(global) == TPassing.IN ||
                parent.getTestInterface().getGlobals().get(global) == TPassing.INOUT){
                inputGlobals.add(global.clone());
            }else if(parent.getTestInterface().getGlobals().get(global) == TPassing.OUT ||
                parent.getTestInterface().getGlobals().get(global) == TPassing.INOUT){
                outputGlobals.add(global.clone());
            }
        }
        for(CElement userGlobal : parent.getTestInterface().getUserGlobals().keySet()){
            if(parent.getTestInterface().getUserGlobals().get(userGlobal) == TPassing.IN ||
                    parent.getTestInterface().getGlobals().get(userGlobal) == TPassing.INOUT){
                inputGlobals.add(userGlobal.clone());
            }else if(parent.getTestInterface().getGlobals().get(userGlobal) == TPassing.OUT ||
                    parent.getTestInterface().getGlobals().get(userGlobal) == TPassing.INOUT){
                outputGlobals.add(userGlobal.clone());
            }
        }
        this.parameters=parameters;
        this.inputGlobals=inputGlobals;
        this.outputGlobals=outputGlobals;
    }

    public static TCase newTestCase(TObject parent){
        int id = parent.getTestCases().size() + 1;
        return new TCase(parent, id);
    }

    public void newTStep(){
        this.tSteps++;
        // Add a new test step to the test case(creating a new element in every values list of each CElement)
        for(CElement parameter : parameters){
            if(parameter instanceof CVariable variable){
                variable.values.add(new CValue("",-1));
            }else if(parameter instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",-1));
            }else if(parameter instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",-1));
                }else{
                    structOrUnionInstance.values.add(new CValue("",-1));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }else if(parameter instanceof CArray array){
                //Go recursively through the array members and add a new value for each member
                _newTStepArray(array);
            }
        }
        for(CElement inputGlobal : inputGlobals){
            if(inputGlobal instanceof CVariable variable){
                variable.values.add(new CValue("",-1));
            }else if(inputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",-1));
            }else if(inputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",-1));
                }else{
                    structOrUnionInstance.values.add(new CValue("",-1));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }else if(inputGlobal instanceof CArray array){
                //Go recursively through the array members and add a new value for each member
                _newTStepArray(array);
            }
        }
        for(CElement outputGlobal : outputGlobals){
            if(outputGlobal instanceof CVariable variable){
                variable.values.add(new CValue("",-1));
            }else if(outputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",-1));
            }else if(outputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",-1));
                }else{
                    structOrUnionInstance.values.add(new CValue("",-1));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }else if(outputGlobal instanceof CArray array){
                //Go recursively through the array members and add a new value for each member
                _newTStepArray(array);
            }
        }
        if(output instanceof CVariable variable) {
            variable.values.add(new CValue("",-1));
        }else if(output instanceof CEnumInstance enumInstance) {
            enumInstance.values.add(new CValue("",-1));
        }else if(output instanceof CStructOrUnionInstance structOrUnionInstance) {
            // It's a pointer to a struct
            if (structOrUnionInstance.getPointers() != 0) {
                structOrUnionInstance.values.add(new CValue("",-1));
            } else {
                structOrUnionInstance.values.add(new CValue("",-1));
                //Go recursively through the struct members and add a new value for each member
                _newTStepStructOrUnion(structOrUnionInstance);
            }
        }else if(output instanceof CArray array){
            //Go recursively through the array members and add a new value for each member
            _newTStepArray(array);
        }

    }
    public void update(){
        // Update the test case elements based on the modified test interface
        TInterface tInterface = this.parent.getTestInterface();
        //Update the globals
        for(CElement global: tInterface.getGlobals().keySet()){
            TPassing globalPassing = tInterface.getGlobals().get(global);
            if(globalPassing == TPassing.IN || globalPassing == TPassing.INOUT){
                if(containsGlobal(inputGlobals, global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    inputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(containsGlobal(outputGlobals, global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    outputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.NONE){
                removeGlobal(inputGlobals,global);
                removeGlobal(outputGlobals,global);
            }
        }
        //Update the user globals
        for(CElement userGlobal: tInterface.getUserGlobals().keySet()){
            TPassing globalPassing = tInterface.getUserGlobals().get(userGlobal);
            if(globalPassing == TPassing.IN || globalPassing == TPassing.INOUT){
                if(containsGlobal(inputGlobals, userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    inputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(containsGlobal(outputGlobals, userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    outputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.NONE){
                removeGlobal(inputGlobals,userGlobal);
                removeGlobal(outputGlobals,userGlobal);
            }
        }
    }

    private void removeGlobal(List<CElement> globals, CElement global){
        for(CElement element : globals){
            if(element.getName().equals(global.getName())){
                globals.remove(element);
                break;
            }
        }
    }

    private boolean containsGlobal(List<CElement> globals, CElement global){
        for(CElement element : globals){
            if(element.getName().equals(global.getName())){
                return false;
            }
        }
        return true;
    }

    private CElement updateGlobal(CElement global){
        if(global instanceof CVariable variable){
            variable.values = nEmptyList(this.tSteps);
        }else if(global instanceof CEnumInstance enumInstance){
            enumInstance.values = nEmptyList(this.tSteps);
        }else if(global instanceof CStructOrUnionInstance structOrUnionInstance){
            structOrUnionInstance.values = nEmptyList(this.tSteps);
            for(CElement member : structOrUnionInstance.getStructType().getMembers()){
                _updateStructOrUnionMember(member);
            }
        }else if(global instanceof CArray array){
            for(CElement member : array.getArrayMembers()){
                if(member instanceof CVariable variableMember){
                    variableMember.values = nEmptyList(this.tSteps);
                }else if(member instanceof CEnumInstance enumInstanceMember){
                    enumInstanceMember.values = nEmptyList(this.tSteps);
                }else if(member instanceof CStructOrUnionInstance structOrUnionInstanceMember){
                    structOrUnionInstanceMember.values = nEmptyList(this.tSteps);
                    for(CElement subMember : structOrUnionInstanceMember.getStructType().getMembers()){
                        _updateStructOrUnionMember(subMember);
                    }
                }
            }
        }
        return global;
    }

    private void _updateStructOrUnionMember(CElement member){
        if(member instanceof CVariable){
            ((CVariable)member).values = nEmptyList(this.tSteps);
        }else if(member instanceof CEnumInstance){
            ((CEnumInstance)member).values = nEmptyList(this.tSteps);
        }else if(member instanceof CStructOrUnionInstance){
            ((CStructOrUnionInstance)member).values = nEmptyList(this.tSteps);
            for(CElement subMember : ((CStructOrUnionInstance)member).getStructType().getMembers()){
                _updateStructOrUnionMember(subMember);
            }
        }
    }

    private void _newTStepStructOrUnion(CStructOrUnionInstance instance){
        for(CElement member : instance.getStructType().getMembers()){
            if(member instanceof CArray array){
                _newTStepArray(array);
            } else if(member instanceof CVariable variable){
                variable.values.add(new CValue("",-1));
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",-1));
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add(new CValue("",-1));
                } else {
                    structOrUnionInstance.values.add(new CValue("",-1));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }
        }
    }

    private void _newTStepArray(CArray array){
        for(CElement member : array.getArrayMembers()){
            if(member instanceof CArray arrayMember){
                _newTStepArray(arrayMember);
            } else if(member instanceof CVariable variable){
                variable.values.add(new CValue("",-1));
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",-1));
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add(new CValue("",-1));
                } else {
                    structOrUnionInstance.values.add(new CValue("",-1));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }
        }
    }

    private List<CValue> nEmptyList(int n){
        List<CValue> emptyList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            emptyList.add(new CValue("",-1));
        }
        return emptyList;
    }

    public void compareResults(TResults result){
        CElement output = this.output;
        CElement resultOutput = result.getOutput();
        List<CElement> globalOutputs = this.outputGlobals;
        List<CElement> resultGlobalOutputs = result.getGlobalOutputs();
        for(int i = 0; i < this.tSteps; i++){
            if(output instanceof CVariable variable && resultOutput instanceof CVariable resultVariable){
                variable.values.get(i).setValueStatus(resultVariable.values.get(i).valueStatus);
            } else if(output instanceof CEnumInstance enumInstance && resultOutput instanceof CEnumInstance resultEnumInstance){
                enumInstance.values.get(i).setValueStatus(resultEnumInstance.values.get(i).valueStatus);
            } else if(output instanceof CStructOrUnionInstance structOrUnionInstance && resultOutput instanceof CStructOrUnionInstance resultStructOrUnionInstance){
                if(structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.get(i).setValueStatus(resultStructOrUnionInstance.values.get(i).valueStatus);
                }else{
                    _compareStructOrUnionMembers(structOrUnionInstance, resultStructOrUnionInstance, i);
                }
            } else if(output instanceof CArray array && resultOutput instanceof CArray resultArray){
                _compareArrayMembers(array, resultArray, i);
            }
            for(int j = 0; j < globalOutputs.size(); j++){
                CElement globalOutput = globalOutputs.get(j);
                CElement resultGlobalOutput = resultGlobalOutputs.get(j);
                if(globalOutput instanceof CVariable variable && resultGlobalOutput instanceof CVariable resultVariable){
                    variable.values.get(i).setValueStatus(resultVariable.values.get(i).valueStatus);
                } else if(globalOutput instanceof CEnumInstance enumInstance && resultGlobalOutput instanceof CEnumInstance resultEnumInstance){
                    enumInstance.values.get(i).setValueStatus(resultEnumInstance.values.get(i).valueStatus);
                } else if(globalOutput instanceof CStructOrUnionInstance structOrUnionInstance && resultGlobalOutput instanceof CStructOrUnionInstance resultStructOrUnionInstance){
                    if(structOrUnionInstance.getPointers() != 0) {
                        structOrUnionInstance.values.get(i).setValueStatus(resultStructOrUnionInstance.values.get(i).valueStatus);
                    }else{
                        _compareStructOrUnionMembers(structOrUnionInstance, resultStructOrUnionInstance, i);
                    }
                } else if(globalOutput instanceof CArray array && resultGlobalOutput instanceof CArray resultArray){
                    _compareArrayMembers(array, resultArray, i);
                }
            }

        }
    }
    private void _compareStructOrUnionMembers(CStructOrUnionInstance instance, CStructOrUnionInstance resultInstance, int tStep){
        for(int i = 0; i < instance.getStructType().getMembers().size(); i++){
            CElement member = instance.getStructType().getMembers().get(i);
            CElement resultMember = resultInstance.getStructType().getMembers().get(i);
            if(member instanceof CVariable variable && resultMember instanceof CVariable resultVariable){
                variable.values.get(tStep).setValueStatus(resultVariable.values.get(tStep).valueStatus);
            } else if(member instanceof CEnumInstance enumInstance && resultMember instanceof CEnumInstance resultEnumInstance){
                enumInstance.values.get(tStep).setValueStatus(resultEnumInstance.values.get(tStep).valueStatus);
            } else if(member instanceof CStructOrUnionInstance structOrUnionInstance && resultMember instanceof CStructOrUnionInstance resultStructOrUnionInstance){
                if(structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.get(tStep).setValueStatus(resultStructOrUnionInstance.values.get(tStep).valueStatus);
                }else{
                    _compareStructOrUnionMembers(structOrUnionInstance, resultStructOrUnionInstance, tStep);
                }
            } else if(member instanceof CArray array && resultMember instanceof CArray resultArray){
                _compareArrayMembers(array, resultArray, tStep);
            }
        }
    }
    private void _compareArrayMembers(CArray array, CArray resultArray, int tStep){
        for(int i = 0; i < array.getArrayMembers().size(); i++){
            CElement member = array.getArrayMembers().get(i);
            CElement resultMember = resultArray.getArrayMembers().get(i);
            if(member instanceof CVariable variable && resultMember instanceof CVariable resultVariable){
                variable.values.get(tStep).setValueStatus(resultVariable.values.get(tStep).valueStatus);
            } else if(member instanceof CEnumInstance enumInstance && resultMember instanceof CEnumInstance resultEnumInstance){
                enumInstance.values.get(tStep).setValueStatus(resultEnumInstance.values.get(tStep).valueStatus);
            } else if(member instanceof CStructOrUnionInstance structOrUnionInstance && resultMember instanceof CStructOrUnionInstance resultStructOrUnionInstance){
                if(structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.get(tStep).setValueStatus(resultStructOrUnionInstance.values.get(tStep).valueStatus);
                }else{
                    _compareStructOrUnionMembers(structOrUnionInstance, resultStructOrUnionInstance, tStep);
                }
            } else if(member instanceof CArray arrayMember && resultMember instanceof CArray resultArrayMember){
                _compareArrayMembers(arrayMember, resultArrayMember, tStep);
            }
        }
    }


    public TObject getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CElement> getParameters() {
        return parameters;
    }

    public void setParameters(List<CElement> parameters) {
        this.parameters = parameters;
    }

    public List<CElement> getInputGlobals() {
        return inputGlobals;
    }

    public void setInputGlobals(List<CElement> inputGlobals) {
        this.inputGlobals = inputGlobals;
    }

    public List<CElement> getOutputGlobals() {
        return outputGlobals;
    }

    public void setOutputGlobals(List<CElement> outputGlobals) {
        this.outputGlobals = outputGlobals;
    }

    public CElement getOutput() {
        return output;
    }

    public void setOutput(CElement output) {
        this.output = output;
    }

    public int getTSteps() {
        return tSteps;
    }

    public int getId(){
        return id;
    }
}
