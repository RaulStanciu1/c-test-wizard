package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.parser.CParserDetector;
import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.driver.TResults;

import java.io.Serializable;
import java.util.*;

public class TObject implements Serializable {
    private CFunction testFunction;
    private TInterface testInterface;
    private List<TCase> testCases;
    private final TProject parent;
    private String prologueCode;
    private String epilogueCode;
    public TObject(TProject parent,CFunction testFunction){
        this.parent = parent;
        this.testFunction = testFunction;
        this.prologueCode = "";
        this.epilogueCode = "";
    }
    public static TObject newTObject(TProject parent,CParserDetector parser, CFunction testFunction){
        CParserDetector newParser = parser.clone();
        CFunction newTestFunction = testFunction.clone();
        TObject newTestObject = new TObject(parent, newTestFunction);
        List<CFunction> localFunctions = newParser.getLocalFunctionDefinitions();
        Iterator<CFunction> iterator = localFunctions.iterator();
        while(iterator.hasNext()){
            CFunction localFunction = iterator.next();
            if(localFunction.getName().strip().equals(newTestFunction.getName().strip())){
                iterator.remove();
                break;
            }
        }
        TInterface newTestObjectInterface;
        newTestObjectInterface = new TInterface(newTestObject, newParser.getExternalFunctionDefinitions(),newParser.getLocalFunctionDefinitions(),newParser.getGlobals(),newTestFunction.getParameters(),newTestFunction.getRetType());
        newTestObject.setTestInterface(newTestObjectInterface);
        List<TCase> newTestCases = new ArrayList<>();
        newTestObject.setTestCases(newTestCases);
        newTestCases.add(TCase.newTestCase(newTestObject));
        return newTestObject;
    }

    public CFunction getTestFunction() {
        return testFunction;
    }

    public TInterface getTestInterface() {
        return testInterface;
    }

    public void setTestInterface(TInterface testInterface) {
        this.testInterface = testInterface;
    }

    public List<TCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TCase> testCases) {
        this.testCases = testCases;
    }

    public TProject getParent() {
        return parent;
    }

    public String getPrologueCode() {
        return prologueCode;
    }

    public void setPrologueCode(String prologueCode) {
        this.prologueCode = prologueCode;
    }

    public String getEpilogueCode() {
        return epilogueCode;
    }

    public void setEpilogueCode(String epilogueCode) {
        this.epilogueCode = epilogueCode;
    }

    private List<CValue> nEmptyList(int n){
        List<CValue> emptyList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            emptyList.add(new CValue("",-1));
        }
        return emptyList;
    }

    public void __updateStructOrUnionMember(CElement member){
        if(member instanceof CVariable){
            ((CVariable)member).values = nEmptyList(this.getTestCases().get(0).getTSteps());
        }else if(member instanceof CEnumInstance){
            ((CEnumInstance)member).values = nEmptyList(this.getTestCases().get(0).getTSteps());
        }else if(member instanceof CStructOrUnionInstance){
            ((CStructOrUnionInstance)member).values = nEmptyList(this.getTestCases().get(0).getTSteps());
            for(CElement subMember : ((CStructOrUnionInstance)member).getStructType().getMembers()){
                __updateStructOrUnionMember(subMember);
            }
        }
    }

    public void _updateTestCaseOutput(TCase testCase){
        if (testCase.getOutput() instanceof CVariable) {
            testCase.setOutput(this.testFunction.getRetType().clone());
            ((CVariable)testCase.getOutput()).values = nEmptyList(this.getTestCases().get(0).getTSteps());
        }else if(testCase.getOutput() instanceof CEnumInstance){
            testCase.setOutput(this.testFunction.getRetType().clone());
            ((CEnumInstance)testCase.getOutput()).values = nEmptyList(this.getTestCases().get(0).getTSteps());
        }
        else if(testCase.getOutput() instanceof CStructOrUnionInstance){
            testCase.setOutput(this.testFunction.getRetType().clone());
            ((CStructOrUnionInstance)testCase.getOutput()).values = nEmptyList(this.getTestCases().get(0).getTSteps());
            for(CElement member :((CStructOrUnionInstance)testCase.getOutput()).getStructType().getMembers()){
                __updateStructOrUnionMember(member);
            }
        }
    }

    public void _updateTestCaseParameters(TCase testCase){
        List<CElement> parameters = this.testFunction.getParameters();
        List<CElement> newParameters = new ArrayList<>();
        for(CElement parameter:parameters){
            if(parameter instanceof CVariable){
                CVariable newParameter = ((CVariable)parameter).clone();
                newParameter.values = nEmptyList(this.getTestCases().get(0).getTSteps());
                newParameters.add(newParameter);
            }else if(parameter instanceof CEnumInstance){
                CEnumInstance newParameter = ((CEnumInstance)parameter).clone();
                newParameter.values = nEmptyList(this.getTestCases().get(0).getTSteps());
                newParameters.add(newParameter);
            }else if(parameter instanceof CStructOrUnionInstance){
                CStructOrUnionInstance newParameter = ((CStructOrUnionInstance)parameter).clone();
                newParameter.values = nEmptyList(this.getTestCases().get(0).getTSteps());
                for(CElement member : newParameter.getStructType().getMembers()){
                    __updateStructOrUnionMember(member);
                }
                newParameters.add(newParameter);
            }
        }
        testCase.setParameters(newParameters);
    }

    public int updateTestFunction(List<CFunction> localFunctionDefinitions) {
        for(CFunction function:localFunctionDefinitions){
            boolean nameMatch = function.getName().strip().equals(this.testFunction.getName().strip());
            if(nameMatch){
                //Update the test interface
                this.testFunction = function;
                this.testInterface.setOutput(function.getRetType());
                this.testInterface.setParameters(function.getParameters());
                //Update every parameter and output in every testcase
                for(TCase testCase:this.getTestCases()){
                    _updateTestCaseOutput(testCase);
                    _updateTestCaseParameters(testCase);
                }

                return 0;
            }
        }
        return -1;
    }

    private void _updateFunctions(List<CFunction> functionDefinitions,CFunction testFunction, List<CFunction> interfaceList, Map<CFunction,String>stubCodeList){
        // Remove any functions that are no longer present(with their respective stub)
        Iterator<CFunction> iterator = interfaceList.iterator();
        while(iterator.hasNext()){
            CFunction currentLocalFunction = iterator.next();
            boolean found = false;
            for(CFunction localFunction:functionDefinitions){
                if(localFunction.getName().strip().equals(currentLocalFunction.getName().strip())){
                    found = true;
                    break;
                }
            }
            if(!found){
                iterator.remove();
                stubCodeList.remove(currentLocalFunction);
            }
        }
        //Add any new local functions to the interface
        for(CFunction localFunction:functionDefinitions) {
            boolean found = false;
            for (CFunction currentLocalFunction : interfaceList) {
                if (localFunction.getName().strip().equals(currentLocalFunction.getName().strip())) {
                    found = true;
                    break;
                }
            }
            if (!found && !localFunction.getName().strip().equals(testFunction.getName().strip())) {
                interfaceList.add(localFunction);
            }
        }
    }

    public void updateLocalFunctions(List<CFunction> localFunctionDefinitions) {
        _updateFunctions(localFunctionDefinitions,this.getTestFunction(),this.testInterface.getLocalFunctions(),this.testInterface.getStubCode());
    }

    public void updateExternalFunctions(List<CFunction> externalFunctionDefinitions) {
        _updateFunctions(externalFunctionDefinitions,this.getTestFunction(),this.testInterface.getExternalFunctions(),this.testInterface.getStubCode());
    }

    public void updateGlobals(List<CElement> globals) {
        //Remove any globals that are no longer present from the interface
        Iterator<CElement> iterator = this.testInterface.getGlobals().keySet().iterator();
        while(iterator.hasNext()){
            CElement currentGlobal = iterator.next();
            boolean found = false;
            for(CElement global:globals){
                if(global.getName().strip().equals(currentGlobal.getName().strip())){
                    found = true;
                    break;
                }
            }
            if(!found){
                iterator.remove();
            }
        }
        //Add any new globals to the interface with NONE passing
        for(CElement global:globals){
            boolean found = false;
            for(CElement currentGlobal:this.testInterface.getGlobals().keySet()){
                if(global.getName().strip().equals(currentGlobal.getName().strip())){
                    found = true;
                    break;
                }
            }
            if(!found){
                this.testInterface.getGlobals().put(global,TPassing.NONE);
            }
        }

        //Update the values of the globals in the test cases
        for(TCase testCase:this.getTestCases()){
            testCase.update();
        }
    }

    public void compareTestResults(List<TResults> tResults){
        for(int i = 0; i < tResults.size(); i++){
            TResults result = tResults.get(i);
            TCase testCase = this.getTestCases().get(i);
            testCase.compareResults(result);
        }
    }
}
