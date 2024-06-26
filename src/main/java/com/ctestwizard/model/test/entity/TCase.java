package com.ctestwizard.model.test.entity;


import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.driver.TResults;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity used to store all the inputs and outputs for the test steps
 */
public class TCase implements Serializable {
    @Serial
    private static final long serialVersionUID = 5L;
    private final TObject parent;
    private Integer tCaseId;
    private String title;
    private String description;
    private List<CElement> parameters;
    private List<CElement> inputGlobals;
    private List<CElement> outputGlobals;
    private CElement output;
    private Integer tSteps;

    /**
     * Constructor for the test case
     * @param parent The parent object
     * @param id The test case id
     */
    public TCase(TObject parent, int id){
        this.tCaseId = id;
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

        for(Map.Entry<CElement,TPassing> entry : parent.getTestInterface().getGlobals().entrySet()){
            if(entry.getValue() == TPassing.IN || entry.getValue() == TPassing.INOUT){
                inputGlobals.add(entry.getKey().clone());
            }
            if(entry.getValue() == TPassing.OUT || entry.getValue() == TPassing.INOUT){
                outputGlobals.add(entry.getKey().clone());
            }
        }

        for(Map.Entry<CElement,TPassing> entry : parent.getTestInterface().getUserGlobals().entrySet()){
            if(entry.getValue() == TPassing.IN || entry.getValue() == TPassing.INOUT){
                inputGlobals.add(entry.getKey().clone());
            }
            if(entry.getValue() == TPassing.OUT || entry.getValue() == TPassing.INOUT){
                outputGlobals.add(entry.getKey().clone());
            }
        }


        this.parameters=parameters;
        this.inputGlobals=inputGlobals;
        this.outputGlobals=outputGlobals;
    }

    /**
     * Static method to create a new test case
     * @param parent The parent object
     * @return The new test case
     */
    public static TCase newTestCase(TObject parent){
        int id = parent.getTestCases().size() + 1;
        return new TCase(parent, id);
    }

    /**
     * Add a new test step to the test case
     */
    public void newTStep(){
        this.tSteps++;
        // Add a new test step to the test case(creating a new element in every values list of each CElement)
        for(CElement parameter : parameters){
            if(parameter instanceof CVariable variable){
                variable.values.add(new CValue("",0));
            }else if(parameter instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",0));
            }else if(parameter instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",0));
                }else{
                    structOrUnionInstance.values.add(new CValue("",0));
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
                variable.values.add(new CValue("",0));
            }else if(inputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",0));
            }else if(inputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",0));
                }else{
                    structOrUnionInstance.values.add(new CValue("",0));
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
                variable.values.add(new CValue("",0));
            }else if(outputGlobal instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",0));
            }else if(outputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                // It's a pointer to a struct
                if(structOrUnionInstance.getPointers() != 0){
                    structOrUnionInstance.values.add(new CValue("",0));
                }else{
                    structOrUnionInstance.values.add(new CValue("",0));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }else if(outputGlobal instanceof CArray array){
                //Go recursively through the array members and add a new value for each member
                _newTStepArray(array);
            }
        }
        if(output instanceof CVariable variable) {
            variable.values.add(new CValue("",0));
        }else if(output instanceof CEnumInstance enumInstance) {
            enumInstance.values.add(new CValue("",0));
        }else if(output instanceof CStructOrUnionInstance structOrUnionInstance) {
            // It's a pointer to a struct
            if (structOrUnionInstance.getPointers() != 0) {
                structOrUnionInstance.values.add(new CValue("",0));
            } else {
                structOrUnionInstance.values.add(new CValue("",0));
                //Go recursively through the struct members and add a new value for each member
                _newTStepStructOrUnion(structOrUnionInstance);
            }
        }else if(output instanceof CArray array){
            //Go recursively through the array members and add a new value for each member
            _newTStepArray(array);
        }

    }

    /**
     * Update the test case elements based on the modified test interface
     */
    public void update(){
        // Update the test case elements based on the modified test interface
        TInterface tInterface = this.parent.getTestInterface();
        //Update the globals
        for(Map.Entry<CElement,TPassing> entry : tInterface.getGlobals().entrySet()){
            TPassing globalPassing = entry.getValue();
            CElement global = entry.getKey();
            if(globalPassing == TPassing.IN || globalPassing == TPassing.INOUT){
                if(!containsGlobal(inputGlobals, global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    inputGlobals.add(updatedGlobal);
                }
                if(globalPassing == TPassing.IN){
                    removeGlobal(outputGlobals,global);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(!containsGlobal(outputGlobals, global)){
                    CElement updatedGlobal = updateGlobal(global.clone());
                    outputGlobals.add(updatedGlobal);
                }
                if(globalPassing == TPassing.OUT){
                    removeGlobal(inputGlobals,global);
                }
            }
            if(globalPassing == TPassing.NONE){
                removeGlobal(inputGlobals,global);
                removeGlobal(outputGlobals,global);
            }
        }
        //Update the user globals
        for(Map.Entry<CElement,TPassing> entry : tInterface.getUserGlobals().entrySet()){
            CElement userGlobal = entry.getKey();
            TPassing globalPassing = entry.getValue();
            if(globalPassing == TPassing.IN || globalPassing == TPassing.INOUT){
                if(!containsGlobal(inputGlobals, userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    inputGlobals.add(updatedGlobal);
                }
                if(globalPassing == TPassing.IN){
                    removeGlobal(outputGlobals,userGlobal);
                }
            }
            if(globalPassing == TPassing.OUT || globalPassing == TPassing.INOUT){
                if(!containsGlobal(outputGlobals, userGlobal)){
                    CElement updatedGlobal = updateGlobal(userGlobal.clone());
                    outputGlobals.add(updatedGlobal);
                }
                if(globalPassing == TPassing.OUT){
                    removeGlobal(inputGlobals,userGlobal);
                }
            }
            if(globalPassing == TPassing.NONE){
                removeGlobal(inputGlobals,userGlobal);
                removeGlobal(outputGlobals,userGlobal);
            }
        }
    }

    /**
     * Private method used to remove a global from the list of globals
     * @param globals The list of globals
     * @param global The global to remove
     */
    private void removeGlobal(List<CElement> globals, CElement global){
        for(CElement element : globals){
            if(element.getName().equals(global.getName())){
                globals.remove(element);
                break;
            }
        }
    }

    /**
     * Private method used to check if a global is already in the list of globals
     * @param globals The list of globals
     * @param global The global to check
     * @return True if the global is in the list, false otherwise
     */
    private boolean containsGlobal(List<CElement> globals, CElement global){
        for(CElement element : globals){
            if(element.getName().equals(global.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Private method used to update a global
     * @param global The global to update
     * @return The updated global
     */
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

    /**
     * Private method used to update a struct or union member
     * @param member The member to update
     */
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

    /**
     * Private method used to add a new test step to a struct or union
     * @param instance The struct or union instance
     */
    private void _newTStepStructOrUnion(CStructOrUnionInstance instance){
        for(CElement member : instance.getStructType().getMembers()){
            if(member instanceof CArray array){
                _newTStepArray(array);
            } else if(member instanceof CVariable variable){
                variable.values.add(new CValue("",0));
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",0));
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add(new CValue("",0));
                } else {
                    structOrUnionInstance.values.add(new CValue("",0));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }
        }
    }

    /**
     * Private method used to add a new test step to an array
     * @param array The array
     */
    private void _newTStepArray(CArray array){
        for(CElement member : array.getArrayMembers()){
            if(member instanceof CArray arrayMember){
                _newTStepArray(arrayMember);
            } else if(member instanceof CVariable variable){
                variable.values.add(new CValue("",0));
            }else if(member instanceof CEnumInstance enumInstance){
                enumInstance.values.add(new CValue("",0));
            }else if(member instanceof CStructOrUnionInstance structOrUnionInstance){
                if (structOrUnionInstance.getPointers() != 0) {
                    structOrUnionInstance.values.add(new CValue("",0));
                } else {
                    structOrUnionInstance.values.add(new CValue("",0));
                    //Go recursively through the struct members and add a new value for each member
                    _newTStepStructOrUnion(structOrUnionInstance);
                }
            }
        }
    }

    /**
     * Private method used to create a list of n empty values
     * @param n The number of values
     * @return The list of n empty values
     */
    private List<CValue> nEmptyList(int n){
        List<CValue> emptyList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            emptyList.add(new CValue("",0));
        }
        return emptyList;
    }

    /**
     * Compare the results of the test case with the actual results of the test and updates the status of the values
     * @param result The actual results of the test
     */
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

    /**
     * Private method used to compare the members of a struct or union
     * @param instance The struct or union instance
     * @param resultInstance The result struct or union instance
     * @param tStep The test step
     */
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

    /**
     * Private method used to compare the members of an array
     * @param array The array
     * @param resultArray The result array
     * @param tStep The test step
     */
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

    /**
     * Get the parent object
     * @return The parent object
     */
    public TObject getParent() {
        return parent;
    }

    /**
     * Get the title of the test case
     * @return The title of the test case
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the test case
     * @param title The title of the test case
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the description of the test case
     * @return  The description of the test case
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the test case
     * @param description The description of the test case
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the parameters of the test case
     * @return The parameters of the test case
     */
    public List<CElement> getParameters() {
        return parameters;
    }

    /**
     * Set the parameters of the test case
     * @param parameters The parameters of the test case
     */
    public void setParameters(List<CElement> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the input globals of the test case
     * @return The input globals of the test case
     */
    public List<CElement> getInputGlobals() {
        return inputGlobals;
    }

    /**
     * Set the input globals of the test case
     * @param inputGlobals The input globals of the test case
     */
    public void setInputGlobals(List<CElement> inputGlobals) {
        this.inputGlobals = inputGlobals;
    }

    /**
     * Get the output globals of the test case
     * @return The output globals of the test case
     */
    public List<CElement> getOutputGlobals() {
        return outputGlobals;
    }

    /**
     * Set the output globals of the test case
     * @param outputGlobals The output globals of the test case
     */
    public void setOutputGlobals(List<CElement> outputGlobals) {
        this.outputGlobals = outputGlobals;
    }

    /**
     * Get the output of the test case
     * @return The output of the test case
     */
    public CElement getOutput() {
        return output;
    }

    /**
     * Set the output of the test case
     * @param output The output of the test case
     */
    public void setOutput(CElement output) {
        this.output = output;
    }

    /**
     * Get the number of test steps
     * @return The number of test steps
     */
    public int getTSteps() {
        return tSteps;
    }

    /**
     * Set the number of test steps
     * @return The number of test steps
     */
    public int getId(){
        return tCaseId;
    }

    /**
     * Set the id of the test case
     * @param id The id of the test case
     */
    public void setId(int id){
        this.tCaseId = id;
    }
}
