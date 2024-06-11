package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TCase;
import com.ctestwizard.model.test.entity.TObject;
import com.ctestwizard.model.test.entity.TProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TDriverUtils {

    public static void generateDefinesFile(TProject parent) throws Exception{
        File definesFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_defines.h");
        if(definesFile.exists()){
            if(!definesFile.delete()){
                throw new Exception("Failed to delete defines file");
            }
        }
        if(!definesFile.createNewFile()){
            throw new Exception("Failed to create defines file");
        }
        StringBuilder definesFileContent = new StringBuilder();
        definesFileContent.append("#ifndef CTW_TEST_DEFINES_H\n");
        definesFileContent.append("#define CTW_TEST_DEFINES_H\n");
        for(CDefine define : parent.getTestDriver().getDefines()){
            definesFileContent.append("#define ").append(define.name()).append(" ").append(define.value()).append("\n");
        }
        definesFileContent.append("#endif\n");
        FileUtils.writeStringToFile(definesFile,definesFileContent.toString(),"UTF-8",false);
    }

    public static void generateTestHeaderFile(TDriver testDriver) throws Exception{
        File testHeaderFile = new File(testDriver.getTestHeaderPath());
        if(testHeaderFile.exists()){
            return;
        }
        if(!testHeaderFile.createNewFile()){
            throw new Exception("Failed to create test header file");
        }
    }

    public static void generateTestDriverFile(TProject parent) throws IOException, NullPointerException {
        File testDriverFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_driver.c");
        if (!testDriverFile.exists()) {
            if (!testDriverFile.createNewFile()) {
                throw new IOException("Failed to create test driver file");
            }
        }
        String TEST_CASES_DEFINE = "#define TEST_CASES " + parent.getTestObjects().get(0).getTestCases().size() + "\n";
        File testHeaderFile = new File(parent.getTestDriver().getTestHeaderPath());
        if (!testHeaderFile.exists()) {
            throw new NullPointerException("Test header file not found");
        }
        String path = testHeaderFile.getAbsolutePath();
        String testDriverFileContent = """
                #include "ctw_test_defines.h"
                """+
                "#include \""+path+"\"\n"+"""
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

    public static void generateTestDriverCoverageFile(TProject parent) throws IOException, NullPointerException {
        File testDriverFile = new File(parent.getTestDriver().getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_driver.c");
        if (!testDriverFile.exists()) {
            if (!testDriverFile.createNewFile()) {
                throw new IOException("Failed to create test driver file");
            }
        }
        String TEST_CASES_DEFINE = "#define TEST_CASES " + parent.getTestObjects().get(0).getTestCases().size() + "\n";
        File testHeaderFile = new File(parent.getTestDriver().getTestHeaderPath());
        if (!testHeaderFile.exists()) {
            throw new NullPointerException("Test header file not found");
        }
        String path = testHeaderFile.getAbsolutePath();
        String testDriverFileContent = """
                #include "ctw_test_defines.h"
                """+
                "#include \""+path+"\"\n"+"""
                #include "ctw_cov.c"
                #include "ctw_src_pre_cov.c"
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
                
                void ctw_coverage_summary(void)
                {
                    double coverage = __ctw__coverage__percentage__();
                    FILE* COVERAGE_FILE = fopen("cov_results.txt","w");
                    fprintf(COVERAGE_FILE,"%f",coverage);
                    fclose(COVERAGE_FILE);
                    FILE* COVERAGE_DIAGRAM_FILE = fopen("cov_diagram.txt","w");
                    for(int i = 0; i < __CTW__DECISIONS_SIZE__; i++)
                    {
                        fprintf(COVERAGE_DIAGRAM_FILE,"%d", __CTW__GET__DECISION__(i));
                    }
                    fclose(COVERAGE_DIAGRAM_FILE);
                }

                int main(void)
                {
                    ctw_test_driver();
                    ctw_coverage_summary();
                    return 0;
                }""";
        FileUtils.writeStringToFile(testDriverFile, TEST_CASES_DEFINE + testDriverFileContent, "UTF-8", false);
    }

    public static void createStubCodeFile(TObject testObject, TProject parent) throws IOException {
        //Generate the stub file based on the test object's interface
        File stubCodeFile = getTestFile(parent, "ctw_stubs.c");
        for (CElement userGlobal : testObject.getTestInterface().getUserGlobals().keySet()) {
            String userGlobalContent = userGlobal.getType() + " " + userGlobal.getName() + ";\n";
            FileUtils.writeStringToFile(stubCodeFile, userGlobalContent, "UTF-8", true);
        }

        for (CFunction stubbedFunction : testObject.getTestInterface().getStubCode().keySet()) {
            String stubCode = testObject.getTestInterface().getStubCode().get(stubbedFunction);
            StringBuilder functionStub = new StringBuilder(stubbedFunction.getStrType() + " " +stubbedFunction.getName() + "(");
            for (int i = 0; i < stubbedFunction.getParameters().size(); i++) {
                functionStub.append(stubbedFunction.getParameters().get(i).getType()).append(" ").append(stubbedFunction.getParameters().get(i).getName());
                if (i != stubbedFunction.getParameters().size() - 1) {
                    functionStub.append(",");
                }
            }
            functionStub.append("){\n");
            functionStub.append(stubCode);
            functionStub.append("}\n");
            FileUtils.writeStringToFile(stubCodeFile, functionStub.toString(), "UTF-8", true);
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
                testStepsFileContent.append("FILE* TEST_DATA_FILE = fopen(\"test_data_output.txt\",\"a\");\n");
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
            TResults result = new TResults(output, outputGlobals, testCase.getTSteps());
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
            var.values.set(step, new CValue(elementValue,match ? 1 : -1));
        } else if (element instanceof CEnumInstance enumInstance) {
            enumInstance.values.set(step, new CValue(elementValue,match ? 1 : -1));
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
                    var1.values.set(step,new CValue(elementValue,match ? 1 : -1));
                    break;
                } else if(elementRef instanceof CEnumInstance enumInstance1){
                    enumInstance1.values.set(step,new CValue(elementValue,match ? 1 : -1));
                    break;
                }
                elementNameIndex++;
            }
            if(elementRef instanceof CVariable var1) {
                var1.values.set(step, new CValue(elementValue, match ? 1 : -1));
            }else if(elementRef instanceof CEnumInstance enumInstance1){
                enumInstance1.values.set(step,new CValue(elementValue,match ? 1 : -1));
            }else{
                throw new Exception("Failed to parse test results");
            }
            if(elementNameIndex != elementMembers.size() - 1){
                throw new Exception("Failed to parse test results");
            }
        }
    }

    public static void cleanUpTestDriverFiles(TDriver testDriver){
        File testDriverFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_driver.c");
        File testStepsFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_steps.c");
        File testDataFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_data.c");
        File stubCodeFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_stubs.c");
        File testDataOutputFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "test_data_output.txt");
        File testDriverExecutable = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_driver.exe");
        File testDefinesFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_test_defines.h");
        File coverageFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_cov.c");
        File coverageDiagramFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "cov_diagram.txt");
        File coverageResultsFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "cov_results.txt");
        File coverageSrcPreFile = new File(testDriver.getProjectPath() + File.separator + "ctw" + File.separator + "ctw_src_pre_cov.c");
        if(coverageFile.exists()){
            coverageFile.delete();
        }
        if(coverageDiagramFile.exists()){
            coverageDiagramFile.delete();
        }
        if(coverageResultsFile.exists()){
            coverageResultsFile.delete();
        }
        if(coverageSrcPreFile.exists()){
            coverageSrcPreFile.delete();
        }
        if(testDefinesFile.exists()){
            testDefinesFile.delete();
        }
        if(testDriverExecutable.exists()){
            testDriverExecutable.delete();
        }
        if(testDriverFile.exists()){
            testDriverFile.delete();
        }
        if(testStepsFile.exists()){
            testStepsFile.delete();
        }
        if(testDataFile.exists()){
            testDataFile.delete();
        }
        if(stubCodeFile.exists()){
            stubCodeFile.delete();
        }
        if(testDataOutputFile.exists()){
            testDataOutputFile.delete();
        }
    }
}
