package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TCase;

/**
 * Class used to write the test driver code
 */
public class TWriter {
    /**
     * Method that returns the content of the test step before the test function call
     * (setting the inputs and the prologue cod)
     * @param tCase The test case
     * @param j The test step index
     * @return The content of the test step before the test function call
     */
    public static String getPreStepContent(TCase tCase, int j) {
        //Convert the input parameters and globals in the C file to a string
        StringBuilder sb = new StringBuilder();
        //Add the input parameters
        int index = 0;
        for(CElement parameter: tCase.getParameters()){
            String parameterName = "__CTW__PARAM__"+index;
            index++;
            if(parameter instanceof CVariable && !((CVariable)parameter).values.get(j).value.isEmpty() && !((CVariable)parameter).values.get(j).value.equals("*none*")){
                String pointers;
                if(((CVariable) parameter).getPointers() == 0) {
                    pointers = "";
                }else{
                    pointers = "*".repeat(((CVariable) parameter).getPointers() - 1);
                }
                sb.append(parameter.getType()).append(pointers).append(" ");
                sb.append(parameterName).append(" = ");
                sb.append(((CVariable) parameter).values.get(j).value).append(";\n");
            }else if(parameter instanceof CEnumInstance && !((CEnumInstance)parameter).values.get(j).value.isEmpty() && !((CEnumInstance)parameter).values.get(j).value.equals("*none*")){
                String pointers;
                if(((CEnumInstance) parameter).getPointers() == 0){
                    pointers = "";
                }else{
                    pointers = "*".repeat(((CEnumInstance) parameter).getPointers() - 1);
                }
                sb.append(parameter.getType()).append(pointers).append(" ");
                sb.append(parameterName).append(" = ");
                sb.append(((CEnumInstance) parameter).values.get(j).value).append(";\n");
            } else if(parameter instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    sb.append(_addStructOrUnionInstance(parameter.getType()+" "+parameterName+".",structOrUnionInstance, j));
                }else if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                    String pointers;
                    if(structOrUnionInstance.getPointers() == 0) {
                        pointers = "";
                    }else{
                        pointers = "*".repeat(structOrUnionInstance.getPointers() - 1);
                    }
                    sb.append(structOrUnionInstance.getType()).append(pointers).append(" ");
                    sb.append(parameterName).append(" = ");
                    sb.append(structOrUnionInstance.values.get(j).value).append(";\n");
                }
            }else if(parameter instanceof CArray array){
                for(CElement element: array.getArrayMembers()){
                    if(element instanceof CVariable && !((CVariable)element).values.get(j).value.isEmpty() && !((CVariable)element).values.get(j).value.equals("*none*")){
                        sb.append(element.getName()).append(" = ");
                        sb.append(((CVariable) element).values.get(j).value).append(";\n");
                    }else if(element instanceof CEnumInstance && !((CEnumInstance)element).values.get(j).value.isEmpty() && !((CEnumInstance)element).values.get(j).value.equals("*none*")){
                        sb.append(element.getName()).append(" = ");
                        sb.append(((CEnumInstance) element).values.get(j).value).append(";\n");
                    } else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                        if(structOrUnionInstance.getPointers() == 0){
                            sb.append(_addStructOrUnionInstance(element.getName()+".",structOrUnionInstance, j));
                        }else if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                            sb.append(structOrUnionInstance.getName()).append(" = ");
                            sb.append(structOrUnionInstance.values.get(j).value).append(";\n");
                        }
                    }
                }
            }
        }

        //Add the input globals
        for(CElement inputGlobal: tCase.getInputGlobals()){
            if(inputGlobal instanceof CVariable && !((CVariable)inputGlobal).values.get(j).value.isEmpty() && !((CVariable)inputGlobal).values.get(j).value.equals("*none*")){
                sb.append(inputGlobal.getName()).append(" = ");
                sb.append(((CVariable) inputGlobal).values.get(j).value).append(";\n");
            }else if(inputGlobal instanceof CEnumInstance && !((CEnumInstance)inputGlobal).values.get(j).value.isEmpty() && !((CEnumInstance)inputGlobal).values.get(j).value.equals("*none*")){
                sb.append(inputGlobal.getName()).append(" = ");
                sb.append(((CEnumInstance) inputGlobal).values.get(j).value).append(";\n");
            } else if(inputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    sb.append(_addStructOrUnionInstance(inputGlobal.getName()+".",structOrUnionInstance, j));
                }else if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                    sb.append(structOrUnionInstance.getName()).append(" = ");
                    sb.append(structOrUnionInstance.values.get(j).value).append(";\n");
                }
            }else if(inputGlobal instanceof CArray array){
                for(CElement element: array.getArrayMembers()){
                    if(element instanceof CVariable && !((CVariable)element).values.get(j).value.isEmpty() && !((CVariable)element).values.get(j).value.equals("*none*")){
                        sb.append(element.getName()).append(" = ");
                        sb.append(((CVariable) element).values.get(j).value).append(";\n");
                    }else if(element instanceof CEnumInstance && !((CEnumInstance)element).values.get(j).value.isEmpty() && !((CEnumInstance)element).values.get(j).value.equals("*none*")){
                        sb.append(element.getName()).append(" = ");
                        sb.append(((CEnumInstance) element).values.get(j).value).append(";\n");
                    } else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                        if(structOrUnionInstance.getPointers() == 0){
                            sb.append(_addStructOrUnionInstance(element.getName()+".",structOrUnionInstance, j));
                        }else if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                            sb.append(structOrUnionInstance.getName()).append(" = ");
                            sb.append(structOrUnionInstance.values.get(j).value).append(";\n");
                        }
                    }
                }
            }
        }
        sb.append("__ctw__prologue__();\n");
        return sb.toString();
    }

    /**
     * Private method used to add the struct or union instance to the string
     * @param parameterName The name of the parameter
     * @param structOrUnionInstance The struct or union instance
     * @param j The test step index
     * @return The string with the struct or union instance
     */
    private static String _addStructOrUnionInstance(String parameterName,CStructOrUnionInstance structOrUnionInstance, int j){
        StringBuilder sb = new StringBuilder();
        for(CElement element: structOrUnionInstance.getStructType().getMembers()){
            if(element instanceof CVariable && !((CVariable)element).values.get(j).value.isEmpty() && !((CVariable)element).values.get(j).value.equals("*none*")){
                sb.append(parameterName).append(element.getName()).append(" = ");
                sb.append(((CVariable) element).values.get(j).value).append(";\n");
            }else if(element instanceof CEnumInstance && !((CEnumInstance)element).values.get(j).value.isEmpty() && !((CEnumInstance)element).values.get(j).value.equals("*none*")){
                sb.append(parameterName).append(element.getName()).append(" = ");
                sb.append(((CEnumInstance) element).values.get(j).value).append(";\n");
            } else if(element instanceof CStructOrUnionInstance structOrUnionInstance1){
                if(structOrUnionInstance1.getPointers() == 0){
                    sb.append(_addStructOrUnionInstance(parameterName+element.getName()+".",structOrUnionInstance1, j));
                }else if(!structOrUnionInstance1.values.get(j).value.isEmpty() && !structOrUnionInstance1.values.get(j).value.equals("*none*")){
                    sb.append(parameterName).append(structOrUnionInstance1.getName()).append(" = ");
                    sb.append(structOrUnionInstance1.values.get(j).value).append(";\n");
                }
            }else if(element instanceof CArray array){
                for(CElement arrayElement: array.getArrayMembers()){
                    if(arrayElement instanceof CVariable && !((CVariable)arrayElement).values.get(j).value.isEmpty() && !((CVariable)arrayElement).values.get(j).value.equals("*none*")){
                        sb.append(parameterName).append(arrayElement.getName()).append(" = ");
                        sb.append(((CVariable) arrayElement).values.get(j).value).append(";\n");
                    }else if(arrayElement instanceof CEnumInstance && !((CEnumInstance)arrayElement).values.get(j).value.isEmpty() && !((CEnumInstance)arrayElement).values.get(j).value.equals("*none*")){
                        sb.append(parameterName).append(arrayElement.getName()).append(" = ");
                        sb.append(((CEnumInstance) arrayElement).values.get(j).value).append(";\n");
                    } else if(arrayElement instanceof CStructOrUnionInstance structOrUnionInstance1){
                        if(structOrUnionInstance1.getPointers() == 0){
                            sb.append(_addStructOrUnionInstance(parameterName+arrayElement.getName()+".",structOrUnionInstance1, j));
                        }else if(!structOrUnionInstance1.values.get(j).value.isEmpty() && !structOrUnionInstance1.values.get(j).value.equals("*none*")){
                            sb.append(structOrUnionInstance1.getName()).append(" = ");
                            sb.append(structOrUnionInstance1.values.get(j).value).append(";\n");
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Method that returns the content of the test step that makes the function call and the epilogue code
     * @param tCase The test case
     * @param j The test step index
     * @return The content of the test step that makes the function call and the epilogue code
     */
    public static String getStepContent(TCase tCase, int j) {
        // Call the function with the input parameters
        // and if the type is not void store it to a variable of its return type
        StringBuilder sb = new StringBuilder();
        if(tCase.getParent().getTestFunction().getStrType().equals("void")){
            sb.append(tCase.getParent().getTestFunction().getName()).append("(");
            for(int i = 0; i < tCase.getParameters().size(); i++){
                sb.append("__CTW__PARAM__").append(i).append(",");
            }
            if(!tCase.getParent().getTestFunction().getParameters().isEmpty()){
                sb.deleteCharAt(sb.length()-1);
            }

            sb.append(");\n");
        }else {
            sb.append(tCase.getParent().getTestFunction().getStrType()).append(" __CTW__RETURN = ");
            sb.append(tCase.getParent().getTestFunction().getName()).append("(");
            for(int i = 0; i < tCase.getParameters().size(); i++){
                sb.append("__CTW__PARAM__").append(i).append(",");
            }
            if(!tCase.getParent().getTestFunction().getParameters().isEmpty()){
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append(");\n");
        }
        sb.append("__ctw__epilogue__();\n");
        return sb.toString();
    }

    /**
     * Method that returns the content of the test step after the test function call (Checks the outputs)
     * @param testCaseIndex The index of the test case
     * @param tCase The test case
     * @param j The test step index
     * @return The content of the test step after the test function call
     */
    public static String getPostStepContent(int testCaseIndex,TCase tCase, int j) {
        StringBuilder sb = new StringBuilder();

        //Write the return type if it is not void
        if(!tCase.getParent().getTestFunction().getStrType().equals("void")){
            CElement returnType = tCase.getOutput();
            if(returnType instanceof CVariable returnTypeVar && !returnTypeVar.values.get(j).value.isEmpty() && !returnTypeVar.values.get(j).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                            +returnType.getName()+" %g %d\\n\", (double)__CTW__RETURN, "+
                                            returnTypeVar.values.get(j).value+"== __CTW__RETURN);\n";
                sb.append(fprintfCallString);
            }else if(returnType instanceof CEnumInstance returnTypeEnum && !returnTypeEnum.values.get(j).value.isEmpty() && !returnTypeEnum.values.get(j).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \"" + testCaseIndex + "." + j + " "
                        + returnType.getName() + " %g %d\\n\", (double)__CTW__RETURN," +
                        returnTypeEnum.values.get(j).value+"==__CTW__RETURN);\n";
                sb.append(fprintfCallString);
            }else if(returnType instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    sb.append(_writeStructOrUnionInstance(returnType.getName()+".",structOrUnionInstance,testCaseIndex,j,"__CTW__RETURN."));
                }else{
                    if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                +returnType.getName()+" %g %d\\n\", (double)__CTW__RETURN," +
                                structOrUnionInstance.values.get(j).value+"==__CTW__RETURN);\n";
                        sb.append(fprintfCallString);
                    }
                }
            }
        }
        //Write the output globals and the output user globals
        for(CElement outputGlobal: tCase.getOutputGlobals()){
            if(outputGlobal instanceof CVariable outputGlobalVar && !outputGlobalVar.values.get(j).value.isEmpty() && !outputGlobalVar.values.get(j).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                        +outputGlobal.getName()+" %g %d\\n\", (double)"+outputGlobal.getName()+","+
                        outputGlobalVar.values.get(j).value+"=="+outputGlobalVar.getName()+");\n";
                sb.append(fprintfCallString);
            }else if(outputGlobal instanceof CEnumInstance outputGlobalEnum && !outputGlobalEnum.values.get(j).value.isEmpty() && !outputGlobalEnum.values.get(j).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                        +outputGlobal.getName()+" %g %d\\n\", (double)"+outputGlobal.getName()+
                        ","+outputGlobalEnum.values.get(j).value+"=="+outputGlobal.getName()+");\n";
                sb.append(fprintfCallString);
            }else if(outputGlobal instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    sb.append(_writeStructOrUnionInstance(outputGlobal.getName()+".",structOrUnionInstance,testCaseIndex,j,outputGlobal.getName()+"."));
                }else{
                    if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                +outputGlobal.getName()+" %g %d\\n\", (double)"+outputGlobal.getName()+
                                ","+structOrUnionInstance.values.get(j).value+"=="+outputGlobal.getName()+");\n";
                        sb.append(fprintfCallString);
                    }
                }
            }else if(outputGlobal instanceof CArray array){
                for(CElement element: array.getArrayMembers()){
                    if(element instanceof CVariable outputGlobalArrayVar && !outputGlobalArrayVar.values.get(j).value.isEmpty() && !outputGlobalArrayVar.values.get(j).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                +element.getName()+" %g %d\\n\", (double)"+element.getName()+
                                ","+outputGlobalArrayVar.values.get(j).value+"=="+element.getName()+");\n";
                        sb.append(fprintfCallString);
                    }else if(element instanceof CEnumInstance outputGlobalArrayEnum && !outputGlobalArrayEnum.values.get(j).value.isEmpty() && !outputGlobalArrayEnum.values.get(j).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                +element.getName()+" %g %d\\n\", (double)"+element.getName()+
                                ","+outputGlobalArrayEnum.values.get(j).value+"=="+element.getName()+");\n";
                        sb.append(fprintfCallString);
                    }else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                        if(structOrUnionInstance.getPointers() == 0){
                            sb.append(_writeStructOrUnionInstance(element.getName()+".",structOrUnionInstance,testCaseIndex,j,element.getName()+"."));
                        }else{
                            if(!structOrUnionInstance.values.get(j).value.isEmpty() && !structOrUnionInstance.values.get(j).value.equals("*none*")){
                                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCaseIndex+"."+j+" "
                                        +element.getName()+" %g %d\\n\", (double)"+element.getName()+
                                        ","+structOrUnionInstance.values.get(j).value+"=="+element.getName()+");\n";
                                sb.append(fprintfCallString);
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Private method used to write the struct or union instance to the string
     * @param parameterName The name of the parameter
     * @param instance The struct or union instance
     * @param testCase The index of the test case
     * @param testStep The index of the test step
     * @param variableName The name of the variable
     * @return The string with the struct or union instance
     */
    private static String _writeStructOrUnionInstance(String parameterName,CStructOrUnionInstance instance, int testCase,int testStep,String variableName){
        StringBuilder sb = new StringBuilder();
        for(CElement element: instance.getStructType().getMembers()){
            if(element instanceof CVariable elementVar && !elementVar.values.get(testStep).value.isEmpty() && !elementVar.values.get(testStep).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                        +parameterName+element.getName()+" %g %d\\n\", (double)"+variableName+element.getName()+
                        ","+elementVar.values.get(testStep).value+"=="+variableName+element.getName()+");\n";
                sb.append(fprintfCallString);
            }else if(element instanceof CEnumInstance elementStruct && !elementStruct.values.get(testStep).value.isEmpty() && !elementStruct.values.get(testStep).value.equals("*none*")){
                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                        +parameterName+element.getName()+" %g %d\\n\", (double)"+variableName+element.getName()+
                        ","+elementStruct.values.get(testStep).value+"=="+variableName+element.getName()+");\n";
                sb.append(fprintfCallString);
            }else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() == 0){
                    sb.append(_writeStructOrUnionInstance(parameterName+element.getName()+".",structOrUnionInstance,testCase,testStep,variableName+element.getName()+"."));
                }else{
                    if(!structOrUnionInstance.values.get(testStep).value.isEmpty() && !structOrUnionInstance.values.get(testStep).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                                +parameterName+element.getName()+" %g %d\\n\", (double)"+variableName+element.getName()+
                                ","+structOrUnionInstance.values.get(testStep).value+"=="+variableName+element.getName()+");\n";
                        sb.append(fprintfCallString);
                    }
                }
            }else if(element instanceof CArray array){
                for(CElement arrayElement: array.getArrayMembers()){
                    if(arrayElement instanceof CVariable arrayElementVar && !arrayElementVar.values.get(testStep).value.isEmpty() && !arrayElementVar.values.get(testStep).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                                +parameterName+arrayElement.getName()+" %g %d\\n\", (double)"+variableName+arrayElement.getName()+
                                ","+arrayElementVar.values.get(testStep).value+"=="+variableName+arrayElement.getName()+");\n";
                        sb.append(fprintfCallString);
                    }else if(arrayElement instanceof CEnumInstance arrayElementEnum && !arrayElementEnum.values.get(testStep).value.isEmpty() && !arrayElementEnum.values.get(testStep).value.equals("*none*")){
                        String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                                +parameterName+arrayElement.getName()+" %g %d\\n\", (double)"+variableName+arrayElement.getName()+
                                ","+arrayElementEnum.values.get(testStep).value+"=="+variableName+arrayElement.getName()+");\n";
                        sb.append(fprintfCallString);
                    }else if(arrayElement instanceof CStructOrUnionInstance structOrUnionInstance && !structOrUnionInstance.values.get(testStep).value.isEmpty() && !structOrUnionInstance.values.get(testStep).value.equals("*none*")){
                        if(structOrUnionInstance.getPointers() == 0){
                            sb.append(_writeStructOrUnionInstance(parameterName+arrayElement.getName()+".",structOrUnionInstance,testCase,testStep,variableName+arrayElement.getName()+"."));
                        }else{
                            if(!structOrUnionInstance.values.get(testStep).value.isEmpty() && !structOrUnionInstance.values.get(testStep).value.equals("*none*")){
                                String fprintfCallString = "fprintf(TEST_DATA_FILE, \""+testCase+"."+testStep+" "
                                        +parameterName+arrayElement.getName()+" %g %d\\n\", (double)"+variableName+arrayElement.getName()+
                                        ","+structOrUnionInstance.values.get(testStep).value+"=="+variableName+arrayElement.getName()+");\n";
                                sb.append(fprintfCallString);
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
