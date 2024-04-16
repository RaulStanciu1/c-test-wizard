package com.ctestwizard.model.testentity;


import com.ctestwizard.model.entity.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCase {
    private final TObject parent;
    private String title;
    private String description;
    private List<CElement> parameters;
    private List<CElement> inputGlobals;
    private List<CElement> outputGlobals;
    private CElement output;
    private int tSteps;
    public TCase(TObject parent){
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

    public void newTStep(){
        this.tSteps++;
        // Add a new test step to the test case(creating a new element in every values list of each CElement)
        for(CElement parameter : parameters){
            if(parameter instanceof CVariable variable){
                variable.values.add("");
            }else if(parameter instanceof CEnumInstance enumInstance){
                enumInstance.values.add("");
            }else if(parameter instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add("");
                }else{
                    structOrUnionInstance.values.add("");
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
                variable.values.add("");
            }else if(inputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add("");
            }else if(inputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add("");
                }else{
                    structOrUnionInstance.values.add("");
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
                variable.values.add("");
            }else if(outputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add("");
            }else if(outputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add("");
                }else{
                    structOrUnionInstance.values.add("");
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }else if(outputGlobal instanceof CArray array){
                //Go recursively through the array members and add a new value for each member
                _newTStepArray(array);
            }
        }
        if(output instanceof CVariable variable) {
            variable.values.add("");
        }else if(output instanceof CEnumInstance enumInstance) {
            enumInstance.values.add("");
        }else if(output instanceof CStructOrUnionInstance structOrUnionInstance) {
            // It's a pointer to a struct
            if (structOrUnionInstance.getPointers() != 0) {
                structOrUnionInstance.values.add("");
            } else {
                structOrUnionInstance.values.add("");
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
                if(!inputGlobals.contains(global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    inputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(!outputGlobals.contains(global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    outputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.NONE){
                inputGlobals.remove(global);
                outputGlobals.remove(global);
            }
        }
        //Update the user globals
        for(CElement userGlobal: tInterface.getUserGlobals().keySet()){
            TPassing globalPassing = tInterface.getUserGlobals().get(userGlobal);
            if(globalPassing == TPassing.IN || globalPassing == TPassing.INOUT){
                if(!inputGlobals.contains(userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    inputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(!outputGlobals.contains(userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    outputGlobals.add(updatedGlobal);
                }
            }
            if(globalPassing == TPassing.NONE){
                inputGlobals.remove(userGlobal);
                outputGlobals.remove(userGlobal);
            }
        }
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
                variable.values.add("");
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add("");
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add("");
                } else {
                    structOrUnionInstance.values.add("");
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
                variable.values.add("");
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add("");
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add("");
                } else {
                    structOrUnionInstance.values.add("");
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }
        }
    }

    private List<String> nEmptyList(int n){
        List<String> emptyList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            emptyList.add("");
        }
        return emptyList;
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
}
