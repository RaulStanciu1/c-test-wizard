package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TCase;
import com.ctestwizard.model.test.entity.TObject;

import java.util.ArrayList;
import java.util.List;

public class StaticAnalyzer {
    public static List<String> staticAnalysis(TObject object) {
        List<String> log = new ArrayList<>();
        //Analyze the title and description of each test case
        List<TCase> testCases = object.getTestCases();
        for (TCase testCase : testCases) {
            String title = testCase.getTitle();
            String description = testCase.getDescription();
            if (title == null || title.isEmpty()) {
                log.add("SEVERITY: LOW - No title for test case: " + testCase.getId());
            }
            if (description == null || description.isEmpty()) {
                log.add("SEVERITY: LOW - No description for test case: " + testCase.getId());
            }
        }
        //Check every value from every test case and check for empty values
        for (TCase testCase : testCases) {
            for(int i = 0; i < testCase.getTSteps(); i++){
                //Check the parameters
                List<CElement> parameters = testCase.getParameters();
                checkElementList(parameters, log, testCase.getId(), i,"HIGH", true);
                //Check the input globals
                List<CElement> inputGlobals = testCase.getInputGlobals();
                checkElementList(inputGlobals, log, testCase.getId(), i, "MEDIUM", false);
                //Check the output globals
                List<CElement> outputGlobals = testCase.getOutputGlobals();
                checkElementList(outputGlobals, log, testCase.getId(), i, "MEDIUM", false);
                //Check the output
                CElement output = testCase.getOutput();
                checkElementList(List.of(output), log, testCase.getId(), i, "HIGH", false);
            }
        }
        return log;
    }


    private static void checkElementList(List<CElement> elementList, List<String> log, int tCaseId, int tStep, String severity, boolean valueNeeded){
        for(CElement element : elementList) {
            if(element instanceof CVariable variable){
                List<CValue> valueList = variable.values;
                checkMissingValues(variable, valueList, log, tCaseId, tStep, severity, valueNeeded);
            }else if(element instanceof CEnumInstance enumInstance){
                List<CValue> valueList = enumInstance.values;
                checkMissingValuesEnum(enumInstance, valueList, log, tCaseId, tStep, severity, valueNeeded);
            }else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    checkElementList(structOrUnionInstance.getStructType().getMembers(), log, tCaseId, tStep, severity, valueNeeded);
                }else{
                    List<CValue> valueList = structOrUnionInstance.values;
                    checkMissingValues(structOrUnionInstance, valueList, log, tCaseId, tStep, severity, valueNeeded);
                }
            }else if(element instanceof CArray array){
                checkElementList(array.getArrayMembers(), log, tCaseId, tStep, severity,valueNeeded);
            }
        }
    }

    private static void checkMissingValues(CElement element,List<CValue> valueList, List<String> log, int tCaseId, int tStep, String severity, boolean valueNeeded){
        CValue value = valueList.get(tStep);
        if(value.value.isEmpty()){
            log.add("SEVERITY "+severity+" - Missing value for element: "+element.getName()+" in test case: "+tCaseId+" step: "+tStep+1);
        }else if(valueNeeded && value.value.equals("*none*")){
            log.add("SEVERITY "+severity+" - Missing value for element: "+element.getName()+" in test case: "+tCaseId+" step: "+tStep+1);
        }
    }

    private static void checkMissingValuesEnum(CEnumInstance enumInstance, List<CValue> valueList, List<String> log, int tCaseId, int tStep, String severity, boolean valueNeeded){
        CValue value = valueList.get(tStep);
        if(value.value.isEmpty()){
            log.add("SEVERITY "+ severity + " - Missing value for element: "+enumInstance.getName()+" in test case: "+tCaseId+" step: "+tStep+1);
        }else if(valueNeeded && value.value.equals("*none*")){
            log.add("SEVERITY " + severity + " - Missing value for element: "+enumInstance.getName()+" in test case: "+tCaseId+" step: "+tStep+1);
        }else if(!enumInstance.getEnumType().getMembers().contains(value.value)){
            log.add("SEVERITY " + severity + " - Invalid value for element: "+enumInstance.getName()+" in test case: "+tCaseId+" step: "+tStep+1);
        }
    }
}
