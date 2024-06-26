package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.parser.CParserDetector;
import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.driver.TResults;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Entity used to store all the necessary information to create tests for a function in the source file
 */
public class TObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private CFunction testFunction;
    private TInterface testInterface;
    private List<TCase> testCases;
    private final TProject parent;
    private String prologueCode;
    private String epilogueCode;

    /**
     * Constructor for the test object
     * @param parent The parent project
     * @param testFunction The function to test
     */
    public TObject(TProject parent,CFunction testFunction){
        this.parent = parent;
        this.testFunction = testFunction;
        this.prologueCode = "";
        this.epilogueCode = "";
    }

    /**
     * Static method to create a new test object
     * @param parent The parent project
     * @param parser The parser for the source file
     * @param testFunction The function to test
     * @return The new test object
     */
    public static TObject newTObject(TProject parent,CParserDetector parser, CFunction testFunction){
        //Clone the parser and test function to avoid conflicts
        CParserDetector newParser = parser.clone();
        CFunction newTestFunction = testFunction.clone();
        TObject newTestObject = new TObject(parent, newTestFunction);

        //Remove the test function from the local functions
        List<CFunction> localFunctions = newParser.getLocalFunctionDefinitions();
        Iterator<CFunction> iterator = localFunctions.iterator();
        while(iterator.hasNext()){
            CFunction localFunction = iterator.next();
            if(localFunction.getName().strip().equals(newTestFunction.getName().strip())){
                iterator.remove();
                break;
            }
        }
        //Create a new test interface for the test object
        TInterface newTestObjectInterface;
        newTestObjectInterface = new TInterface(newTestObject, newParser.getExternalFunctionDefinitions(),newParser.getLocalFunctionDefinitions(),newParser.getGlobals(),newTestFunction.getParameters(),newTestFunction.getRetType());
        newTestObject.setTestInterface(newTestObjectInterface);
        //Create a new test case for the test object
        List<TCase> newTestCases = new ArrayList<>();
        newTestObject.setTestCases(newTestCases);
        newTestCases.add(TCase.newTestCase(newTestObject));
        return newTestObject;
    }

    /**
     * Get the test function
     * @return The test function
     */
    public CFunction getTestFunction() {
        return testFunction;
    }

    /**
     * Set the test function
     * @return The test function
     */
    public TInterface getTestInterface() {
        return testInterface;
    }

    /**
     * Set the test function
     * @param testInterface The test interface
     */
    public void setTestInterface(TInterface testInterface) {
        this.testInterface = testInterface;
    }

    /**
     * Get the test cases
     * @return The test cases
     */
    public List<TCase> getTestCases() {
        return testCases;
    }

    /**
     * Set the test cases
     * @param testCases The test cases
     */
    public void setTestCases(List<TCase> testCases) {
        this.testCases = testCases;
    }

    /**
     * Get the parent project
     * @return The parent project
     */
    public TProject getParent() {
        return parent;
    }

    /**
     * Get the prologue code
     * @return The prologue code
     */
    public String getPrologueCode() {
        return prologueCode;
    }

    /**
     * Set the prologue code
     * @param prologueCode The prologue code
     */
    public void setPrologueCode(String prologueCode) {
        this.prologueCode = prologueCode;
    }

    /**
     * Get the epilogue code
     * @return The epilogue code
     */
    public String getEpilogueCode() {
        return epilogueCode;
    }

    /**
     * Set the epilogue code
     * @param epilogueCode The epilogue code
     */
    public void setEpilogueCode(String epilogueCode) {
        this.epilogueCode = epilogueCode;
    }

    /**
     * Create an empty list of n CValues
     * @param n The number of CValues to create
     * @return The list of n empty CValues
     */
    private List<CValue> nEmptyList(int n){
        List<CValue> emptyList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            emptyList.add(new CValue("",0));
        }
        return emptyList;
    }

    /**
     * Private method to update the values of the members of a struct or union
     * @param member The member to update
     */
    private void __updateStructOrUnionMember(CElement member){
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

    /**
     * Private method to update the output of a test case
     * @param testCase The test case to update
     */
    private void _updateTestCaseOutput(TCase testCase){
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

    /**
     * Private method to update the parameters of a test case
     * @param testCase The test case to update
     */
    private void _updateTestCaseParameters(TCase testCase){
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

    /**
     *  Update the test function upon some modifications from the parser
     * @param localFunctionDefinitions the list of local functions from the parser
     * @return  0 if the test function was updated, -1 otherwise
     */
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

    /**
     * Private method to update the functions in the interface
     * @param functionDefinitions The list of function definitions from the parser
     * @param testFunction The test function
     * @param interfaceList The list of functions in the interface
     * @param stubCodeList The list of stub codes
     */
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

    /**
     * Update the local functions in the test interface
     * @param localFunctionDefinitions The list of local functions from the parser
     */
    public void updateLocalFunctions(List<CFunction> localFunctionDefinitions) {
        _updateFunctions(localFunctionDefinitions,this.getTestFunction(),this.testInterface.getLocalFunctions(),this.testInterface.getStubCode());
    }

    /**
     * Update the external functions in the test interface
     * @param externalFunctionDefinitions The list of external functions from the parser
     */
    public void updateExternalFunctions(List<CFunction> externalFunctionDefinitions) {
        _updateFunctions(externalFunctionDefinitions,this.getTestFunction(),this.testInterface.getExternalFunctions(),this.testInterface.getStubCode());
    }

    /**
     * Update the globals in the test interface
     * @param globals The list of globals from the parser
     */
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

    /**
     * Method used to compare the test results with the expected results
     * @param tResults The test results
     */
    public void compareTestResults(List<TResults> tResults){
        for(int i = 0; i < tResults.size(); i++){
            TResults result = tResults.get(i);
            TCase testCase = this.getTestCases().get(i);
            testCase.compareResults(result);
        }
    }
}
