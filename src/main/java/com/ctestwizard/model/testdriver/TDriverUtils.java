package com.ctestwizard.model.testdriver;

import com.ctestwizard.model.entity.*;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TDriverUtils {
    private static boolean compareEnumInstances(CElement enumInstance, CElement enumDefinition) {
        CEnumInstance enumInstance1 = (CEnumInstance) enumInstance;
        CEnumInstance enumDefinition1 = (CEnumInstance) enumDefinition;
        return !enumInstance1.getName().equals(enumDefinition1.getName()) || compareEnums(enumInstance1.getEnumType(), enumDefinition1.getEnumType());
    }

    private static boolean compareEnums(CElement enumDefinition, CElement enumType) {
        CEnum enumDef = (CEnum) enumDefinition;
        CEnum enumT = (CEnum) enumType;
        if (enumDef.getMembers().size() != enumT.getMembers().size()) {
            return true;
        }
        for (int i = 0; i < enumDef.getMembers().size(); i++) {
            if (!enumDef.getMembers().get(i).equals(enumT.getMembers().get(i))) {
                return true;
            }
        }
        return !enumDefinition.getName().equals(enumType.getName());
    }

    private static boolean compareVariables(CElement variableDefinition, CElement variableType) {
        CVariable varDef = (CVariable) variableDefinition;
        CVariable varType = (CVariable) variableType;
        return !varDef.getName().equals(varType.getName()) || !varDef.getType().equals(varType.getType());
    }

    private static boolean compareArrays(CElement arrayDefinition, CElement arrayType) {
        CArray arrayDef = (CArray) arrayDefinition;
        CArray arrayT = (CArray) arrayType;

        boolean nameMatch = !arrayDef.getName().equals(arrayT.getName());
        boolean typeMatch = !arrayDef.getType().equals(arrayT.getType());
        boolean sizeMatch = arrayDef.getArrayMembers().size() != arrayT.getArrayMembers().size();
        return nameMatch || typeMatch || sizeMatch;
    }

    private static boolean compareStructAndUnionInstances(CElement structOrUnionDef, CElement structOrUnionType) {
        CStructOrUnionInstance structOrUnionDef1 = (CStructOrUnionInstance) structOrUnionDef;
        CStructOrUnionInstance structOrUnionType1 = (CStructOrUnionInstance) structOrUnionType;
        return !structOrUnionDef.getName().equals(structOrUnionType.getName()) || compareStructAndUnions(structOrUnionDef1.getStructType(), structOrUnionType1.getStructType());
    }

    private static boolean compareStructAndUnions(CElement structOrUnionDef, CElement structOrUnionType) {
        if (!structOrUnionDef.getName().equals(structOrUnionType.getName())) {
            return true;
        }
        CStructOrUnion structOrUnionDef1 = (CStructOrUnion) structOrUnionDef;
        CStructOrUnion structOrUnionType1 = (CStructOrUnion) structOrUnionType;
        if (structOrUnionDef1.getMembers().size() != structOrUnionType1.getMembers().size()) {
            return true;
        }

        for (int i = 0; i < structOrUnionDef1.getMembers().size(); i++) {
            if (structOrUnionDef1.getMembers().get(i) instanceof CVariable varMemberDef) {
                if (compareVariables(varMemberDef, structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CStructOrUnionInstance) {
                if (compareStructAndUnionInstances(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CEnumInstance) {
                if (compareEnumInstances(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CArray) {
                if (compareArrays(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean compareEnumDefinitions(List<CElement> enumDefinitions, List<CElement> enumTypes) {
        if (enumDefinitions.isEmpty() && enumTypes.isEmpty()) {
            return false;
        }
        if (enumDefinitions.size() != enumTypes.size()) {
            return true;
        }
        for (int i = 0; i < enumDefinitions.size(); i++) {
            if (compareEnums(enumDefinitions.get(i), enumTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareStructAndUnionDefinitions(List<CElement> structAndUnionDefinitions, List<CElement> structOrUnionTypes) {
        if (structAndUnionDefinitions.isEmpty() && structOrUnionTypes.isEmpty()) {
            return false;
        }
        if (structAndUnionDefinitions.size() != structOrUnionTypes.size()) {
            return true;
        }
        for (int i = 0; i < structAndUnionDefinitions.size(); i++) {
            if (compareStructAndUnions(structAndUnionDefinitions.get(i), structOrUnionTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean compareFunction(CFunction function, CFunction functionDefinition) {
        if (!function.getName().equals(functionDefinition.getName())) {
            return false;
        }
        if (!function.getStrType().equals(functionDefinition.getStrType())) {
            return false;
        }
        if (function.getParameters().size() != functionDefinition.getParameters().size()) {
            return false;
        }
        for (int i = 0; i < function.getParameters().size(); i++) {
            if (function.getParameters().get(i) instanceof CVariable var && functionDefinition.getParameters().get(i) instanceof CVariable var1) {
                if (compareVariables(var, var1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CArray array && functionDefinition.getParameters().get(i) instanceof CArray array1) {
                if (compareArrays(array, array1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CStructOrUnionInstance structOrUnion && functionDefinition.getParameters().get(i) instanceof CStructOrUnionInstance structOrUnion1) {
                if (compareStructAndUnionInstances(structOrUnion, structOrUnion1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CEnumInstance enum1 && functionDefinition.getParameters().get(i) instanceof CEnumInstance enum2) {
                if (compareEnumInstances(enum1, enum2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    public static boolean compareCFunction(CFunction testFunction, List<CFunction> localFunctionDefinitions) {
        boolean found = false;
        for (CFunction localFunction : localFunctionDefinitions) {
            if (compareFunction(testFunction, localFunction)) {
                found = true;
                break;
            }
        }
        return !found;
    }

    public static boolean compareCFunctionList(CFunction testFunction, List<CFunction> localFunctions, List<CFunction> localFunctionDefinitions) {
        //Remove the testFunction from the localFunctionDefinitions list copy
        List<CFunction> localFunctionDefsCopy = new ArrayList<>(List.copyOf(localFunctionDefinitions));
        for (CFunction localFunctionDef : localFunctionDefsCopy) {
            if (compareFunction(testFunction, localFunctionDef)) {
                localFunctionDefsCopy.remove(localFunctionDef);
                break;
            }
        }
        //Compare the localFunctions with the localFunctionDefinitions copy
        if (localFunctions.size() != localFunctionDefsCopy.size()) {
            return true;
        }
        for (int i = 0; i < localFunctions.size(); i++) {
            if (!compareFunction(localFunctions.get(i), localFunctionDefsCopy.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareCElementList(Set<CElement> cElements, List<CElement> globals) {
        if (cElements.size() != globals.size()) {
            return true;
        }
        for (CElement cElement : cElements) {
            boolean found = false;
            for (CElement global : globals) {
                if (cElement instanceof CVariable var && global instanceof CVariable var1) {
                    if (!compareVariables(var, var1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CArray array && global instanceof CArray array1) {
                    if (!compareArrays(array, array1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CStructOrUnionInstance structOrUnion && global instanceof CStructOrUnionInstance structOrUnion1) {
                    if (!compareStructAndUnionInstances(structOrUnion, structOrUnion1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CEnumInstance enum1 && global instanceof CEnumInstance enum2) {
                    if (!compareEnumInstances(enum1, enum2)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return true;
            }
        }
        return false;
    }

    public static void generateTestDriverFile(TProject parent) throws IOException, NullPointerException {
        File testDriverFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_driver.c");
        if (!testDriverFile.exists()) {
            if (!testDriverFile.createNewFile()) {
                throw new IOException("Failed to create test driver file");
            }
        }
        String TEST_CASES_DEFINE = "#define TEST_CASES " + parent.getTestObjects().get(0).getTestCases().size() + "\n";
        String testDriverFileContent = """
                #include "ctw_src_pre.c"
                #include "ctw_stubs.c"
                #include "ctw_test_data.c"



                void ctw_test_driver(void)
                {
                    for(int i = 0; i < TEST_CASES; i++)
                    {
                        int TEST_STEPS = get_test_steps_count(i);
                        for(int j = 0 ; j < TEST_STEPS; j++)
                        {
                            ctw_test_case(i, j);
                        }
                    }
                }

                int main(void)
                {
                    ctw_test_driver();
                    return 0;
                }""";
        FileUtils.writeStringToFile(testDriverFile, TEST_CASES_DEFINE + testDriverFileContent, "UTF-8", false);
    }

    public static void createStubCodeFile(TObject testObject, TProject parent) throws IOException {
        //Generate the stub file based on the test object's interface
        File stubCodeFile = getTestFile(parent, "ctw_stubs.c");

        for (CFunction stubbedFunction : testObject.getTestInterface().getStubCode().keySet()) {
            String stubCode = testObject.getTestInterface().getStubCode().get(stubbedFunction);
            StringBuilder functionStub = new StringBuilder(stubbedFunction.getStrType() + " " + "CTW_" + stubbedFunction.getName() + "(");
            for (int i = 0; i < stubbedFunction.getParameters().size(); i++) {
                functionStub.append(stubbedFunction.getParameters().get(i).getType()).append(" ").append(stubbedFunction.getParameters().get(i).getName());
                if (i != stubbedFunction.getParameters().size() - 1) {
                    functionStub.append(",");
                }
            }
            functionStub.append("){\n");
            functionStub.append(stubCode);
            functionStub.append("}\n");
            functionStub.append("#define ").append(stubbedFunction.getName()).append(" CTW_").append(stubbedFunction.getName()).append("\n");
            FileUtils.writeStringToFile(stubCodeFile, functionStub.toString(), "UTF-8", true);
        }
        for (CElement userGlobal : testObject.getTestInterface().getUserGlobals().keySet()) {
            String userGlobalContent = userGlobal.getType() + " " + userGlobal.getName() + ";\n";
            FileUtils.writeStringToFile(stubCodeFile, userGlobalContent, "UTF-8", true);
        }
    }

    public static void generateTestDataFile(TObject testObject, TProject parent) throws IOException {
        File testDataFile = getTestFile(parent, "ctw_test_data.c");
        StringBuilder testDataFileContent = new StringBuilder();

        //Create the get_test_steps_count function
        testDataFileContent.append("#include <stdio.h>\n");
        testDataFileContent.append("#include <stdlib.h>\n");
        testDataFileContent.append("#include \"ctw_test_steps.c\"\n");

        testDataFileContent.append("int get_test_steps_count(int test_case){\n");
        testDataFileContent.append("switch(test_case){\n");
        for (int i = 0; i < testObject.getTestCases().size(); i++) {
            testDataFileContent.append("case ").append(i).append(":\n");
            testDataFileContent.append("return ").append(testObject.getTestCases().get(i).getTSteps()).append(";\n");
        }
        testDataFileContent.append("default:\n");
        testDataFileContent.append("return -1;\n");
        testDataFileContent.append("}\n");
        testDataFileContent.append("}\n");
        //Create the ctw_test_case function
        testDataFileContent.append("void ctw_test_case(int test_case, int step){\n");
        testDataFileContent.append("switch(test_case){\n");
        for (int i = 0; i < testObject.getTestCases().size(); i++) {
            testDataFileContent.append("case ").append(i).append(":\n");
            testDataFileContent.append("switch(step){\n");
            for (int j = 0; j < testObject.getTestCases().get(i).getTSteps(); j++) {
                testDataFileContent.append("case ").append(j).append(":\n");
                testDataFileContent.append("ctw_test_step_").append(i).append("_").append(j).append("();\n");
                testDataFileContent.append("break;\n");
            }
            testDataFileContent.append("}\n");
        }
        testDataFileContent.append("}\n");
        testDataFileContent.append("}\n");
        FileUtils.writeStringToFile(testDataFile, testDataFileContent.toString(), "UTF-8", false);
    }

    private static File getTestFile(TProject parent, String fileName) throws IOException {
        File testFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + fileName);
        if (testFile.exists()) {
            if (!testFile.delete()) {
                throw new IOException("Failed to delete test file");
            }
        }
        if (!testFile.createNewFile()) {
            throw new IOException("Failed to create test file");
        }
        return testFile;
    }

    public static void generateTestStepsFile(TObject testObject, TProject parent) throws IOException {
        File testStepsFile = getTestFile(parent, "ctw_test_steps.c");
        StringBuilder testStepsFileContent = new StringBuilder();
        for (int i = 0; i < testObject.getTestCases().size(); i++) {
            for (int j = 0; j < testObject.getTestCases().get(i).getTSteps(); j++) {
                testStepsFileContent.append("void ctw_test_step_").append(i).append("_").append(j).append("(){\n");
                testStepsFileContent.append("FILE* TEST_DATA_FILE = fopen(\"test_data_output.txt\",\"w\");\n");
                testStepsFileContent.append(TWriter.getPreStepContent(testObject.getTestCases().get(i), j));
                testStepsFileContent.append(TWriter.getStepContent(testObject.getTestCases().get(i), j));
                testStepsFileContent.append(TWriter.getPostStepContent(i, testObject.getTestCases().get(i), j));
                testStepsFileContent.append("fclose(TEST_DATA_FILE);\n");
                testStepsFileContent.append("}\n");
            }
        }
        FileUtils.writeStringToFile(testStepsFile, testStepsFileContent.toString(), "UTF-8", true);
    }

    public static void compileTestDriverFile(TDriver driver,ConsoleWriter consoleWriter) throws Exception {
        //Compile the test driver file using the given compiler
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(driver.getCompiler(), "ctw/ctw_test_driver.c");
        //Add the include directories and the linker to the compiler command
        for (String includeDirectory : driver.getIncludeDirectories()) {
            processBuilder.command().add("-I");
            processBuilder.command().add(includeDirectory);
        }
        for (String linker : driver.getLinker()) {
            processBuilder.command().add(linker);
        }
        processBuilder.command().add("-o");
        processBuilder.command().add("ctw/ctw_test_driver");
        //Set the current directory to the project path
        processBuilder.directory(new File(driver.getProjectPath()));
        Process process = processBuilder.start();
        consoleWriter.redirectOutput(process);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to compile test driver file");
        }
        //Set the file permissions to allow execution
        File testDriverFile = new File(driver.getProjectPath() + "/ctw/ctw_test_driver.exe");
        if (!testDriverFile.setExecutable(true)) {
            throw new Exception("Failed to set test driver file permissions");
        }
    }

    public static void runTestDriverFile(TDriver driver,ConsoleWriter consoleWriter) throws Exception {
        //Run the test driver file
        ProcessBuilder processBuilder = new ProcessBuilder(driver.getProjectPath() + "/ctw/ctw_test_driver.exe");
        File workingDirectory = new File(driver.getProjectPath() + "/ctw");
        processBuilder.directory(workingDirectory);
        Process process = processBuilder.start();
        consoleWriter.redirectOutput(process);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to run test driver file: Exit code: " + exitCode);
        }
    }

    public static List<TResults> parseTestDataOutputFile(TObject testObject, TProject parent) throws Exception {
        //Step 1: Open the test data output file
        File testDataOutputFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + "test_data_output.txt");
        if (!testDataOutputFile.exists()) {
            throw new Exception("Test data output not generated");
        }
        //Step 2: Create a copy of all the outputs and output globals from every test case in a results list
        List<TResults> results = new ArrayList<>();
        for (TCase testCase : testObject.getTestCases()) {
            List<CElement> outputGlobals = new ArrayList<>();
            CElement output = testCase.getOutput().clone();
            for (CElement global : testCase.getOutputGlobals()) {
                outputGlobals.add(global.clone());
            }
            TResults result = new TResults(output, outputGlobals);
            results.add(result);
        }

        //Step 3: Go over the file contents line by line and update the results list
        List<String> fileContents = FileUtils.readLines(testDataOutputFile, "UTF-8");
        // Test Output data format: <TestCase>.<TestStep> <TestElement> <TestElementValue>
        for (String line : fileContents) {
            String[] lineContents = line.split(" ");
            int testCaseIndex = Integer.parseInt(lineContents[0].split("\\.")[0]);
            int testStepIndex = Integer.parseInt(lineContents[0].split("\\.")[1]);
            String elementName = lineContents[1];
            String elementValue = lineContents[2];
            boolean match = lineContents[3].equals("1");
            TResults result = results.get(testCaseIndex);

            // The return of the function will be represented as <FunctionName>()... in the output file
            if (elementName.contains("()")) {
                _handleOutput(result, testStepIndex, elementName, elementValue, match);
            } else {
                _handleGlobalOutput(result, testStepIndex, elementName, elementValue, match);
            }
        }
        return results;
    }

    private static void _handleOutput(TResults result, int step, String elementName, String elementValue, boolean match) throws Exception {
        CElement output = result.getOutput();
        __handleElement(output, step, elementName, elementValue, match);
    }

    private static void _handleGlobalOutput(TResults result, int step, String elementName, String elementValue, boolean match) throws Exception {
        for (CElement global : result.getGlobalOutputs()){
            __handleElement(global,step,elementName,elementValue,match);
        }
    }

    private static void __handleElement(CElement element,int step, String elementName, String elementValue, boolean match) throws Exception{
        if (element instanceof CVariable var) {
            var.values.set(step, new CValue(elementValue,match ? 1 : 0));
        } else if (element instanceof CEnumInstance enumInstance) {
            enumInstance.values.set(step, new CValue(elementValue,match ? 1 : 0));
        } else if (element instanceof CStructOrUnionInstance structOrUnionInstance) {
            // Check which member of the struct or union is being updated
            List<String> elementMembers = List.of(elementName.split("\\."));
            CElement elementRef = structOrUnionInstance;
            int elementNameIndex = 0;
            for (int i = 1; i < elementMembers.size(); i++) {
                if (elementRef instanceof CStructOrUnionInstance structOrUnionInstance1) {
                    for (CElement member : structOrUnionInstance1.getStructType().getMembers()) {
                        if (member.getName().equals(elementMembers.get(i))) {
                            elementRef = member;
                            break;
                        }
                    }
                } else if (elementRef instanceof CArray array) {
                    for(CElement element1:array.getArrayMembers()){
                        if(element1.getName().equals(elementMembers.get(i))){
                            elementRef = element1;
                            break;
                        }
                    }
                } else if(elementRef instanceof CVariable var1){
                    var1.values.set(step,new CValue(elementValue,match ? 1 : 0));
                    break;
                } else if(elementRef instanceof CEnumInstance enumInstance1){
                    enumInstance1.values.set(step,new CValue(elementValue,match ? 1 : 0));
                    break;
                }
                elementNameIndex++;
            }
            if(elementRef instanceof CVariable var1) {
                var1.values.set(step, new CValue(elementValue, match ? 1 : 0));
            }else if(elementRef instanceof CEnumInstance enumInstance1){
                enumInstance1.values.set(step,new CValue(elementValue,match ? 1 : 0));
            }else{
                throw new Exception("Failed to parse test results");
            }
            if(elementNameIndex != elementMembers.size() - 1){
                throw new Exception("Failed to parse test results");
            }
        }
    }
}
