package com.ctestwizard.model.testdriver;

import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TDriverUtils {

    private static boolean compareDefinitions(List<CElement> definitions, List<CElement> types) {
        for(CElement definition : definitions){
            if(!types.contains(definition)){
                return false;
            }
        }
        return true;
    }
    public static boolean compareEnumDefinitions(List<CElement> enumDefinitions, List<CElement> enumTypes) {
        return compareDefinitions(enumDefinitions, enumTypes);
    }

    public static boolean compareStructAndUnionDefinitions(List<CElement> structAndUnionDefinitions, List<CElement> structOrUnionTypes) {
        return compareDefinitions(structAndUnionDefinitions, structOrUnionTypes);
    }

    public static boolean compareCFunction(CFunction testFunction, List<CFunction> localFunctionDefinitions) {
        //Search for the function in the list of local functions by name
        for (CFunction localFunction : localFunctionDefinitions) {
            boolean nameMatch = localFunction.getName().strip().equals(testFunction.getName().strip());
            boolean typeMatch = localFunction.getStrType().strip().equals(testFunction.getStrType().strip());
            boolean parameterSizeMatch = localFunction.getParameters().size() == testFunction.getParameters().size();
            if(nameMatch && typeMatch && parameterSizeMatch){
                for (int i = 0; i < localFunction.getParameters().size(); i++) {
                    boolean parameterNameMatch = localFunction.getParameters().get(i).getName().strip().equals(testFunction.getParameters().get(i).getName().strip());
                    boolean parameterTypeMatch = localFunction.getParameters().get(i).getType().strip().equals(testFunction.getParameters().get(i).getType().strip());
                    if (parameterNameMatch && parameterTypeMatch) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean compareCFunctionList(List<CFunction> localFunctions, List<CFunction> localFunctionDefinitions) {
        for(CFunction localFunction : localFunctions){
            if(!compareCFunction(localFunction,localFunctionDefinitions)){
                return false;
            }
        }
        return true;
    }

    public static boolean compareCElementList(Set<CElement> cElements, List<CElement> globals) {
        for(CElement cElement : cElements){
            if(!globals.contains(cElement)){
                return false;
            }
        }
        return true;
    }

    public static void generateTestDriverFile(TProject parent) throws IOException, NullPointerException {
        File testDriverFile = new File(parent.getTestDriver().getProjectPath() + File.separator +"ctw"+File.separator+ "ctw_test_driver.c");
        if(!testDriverFile.exists()){
            if(!testDriverFile.createNewFile()){
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
        FileUtils.writeStringToFile(testDriverFile,TEST_CASES_DEFINE+testDriverFileContent,"UTF-8",false);
    }

    public static void createStubCodeFile(TObject testObject, TProject parent) throws IOException {
        //Generate the stub file based on the test object's interface
        File stubCodeFile = getTestFile(parent,"ctw_stubs.c");

        for(CFunction stubbedFunction : testObject.getTestInterface().getStubCode().keySet()){
            String stubCode = testObject.getTestInterface().getStubCode().get(stubbedFunction);
            StringBuilder functionStub = new StringBuilder(stubbedFunction.getStrType() + " " +"CTW_" +stubbedFunction.getName() + "(");
            for(int i = 0; i < stubbedFunction.getParameters().size(); i++){
                functionStub.append(stubbedFunction.getParameters().get(i).getType()).append(" ").append(stubbedFunction.getParameters().get(i).getName());
                if(i != stubbedFunction.getParameters().size() - 1){
                    functionStub.append(",");
                }
            }
            functionStub.append("){\n");
            functionStub.append(stubCode);
            functionStub.append("}\n");
            functionStub.append("#define ").append(stubbedFunction.getName()).append(" CTW_").append(stubbedFunction.getName()).append("\n");
            FileUtils.writeStringToFile(stubCodeFile,functionStub.toString(),"UTF-8",true);
        }
        for(CElement userGlobal : testObject.getTestInterface().getGlobals().keySet()){
            String userGlobalContent = userGlobal.getType()+ " "+userGlobal.getName()+";\n";
            FileUtils.writeStringToFile(stubCodeFile,userGlobalContent,"UTF-8",true);
        }
    }

    public static void generateTestDataFile(TObject testObject, TProject parent) throws IOException {
        File testDataFile = getTestFile(parent,"ctw_test_data.c");
        StringBuilder testDataFileContent = new StringBuilder();

        //Create the get_test_steps_count function
        testDataFileContent.append("#include <stdio.h>\n");
        testDataFileContent.append("#include <stdlib.h>\n");
        testDataFileContent.append("#include \"ctw_test_steps.c\"\n");

        testDataFileContent.append("int get_test_steps_count(int test_case){\n");
        testDataFileContent.append("switch(test_case){\n");
        for(int i = 0; i < testObject.getTestCases().size(); i++){
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
        for(int i = 0; i < testObject.getTestCases().size(); i++){
            testDataFileContent.append("case ").append(i).append(":\n");
            testDataFileContent.append("switch(step){\n");
            for(int j = 0; j < testObject.getTestCases().get(i).getTSteps(); j++){
                testDataFileContent.append("case ").append(j).append(":\n");
                testDataFileContent.append("ctw_test_step_").append(i).append("_").append(j).append("();\n");
                testDataFileContent.append("break;\n");
            }
        }
        testDataFileContent.append("default:\n");
        testDataFileContent.append("break;\n");
        testDataFileContent.append("}\n");
        testDataFileContent.append("break;\n");
        testDataFileContent.append("}\n");
        testDataFileContent.append("}\n");
        FileUtils.writeStringToFile(testDataFile,testDataFileContent.toString(),"UTF-8",false);
    }

    private static File getTestFile(TProject parent,String fileName) throws IOException {
        File testFile = new File(parent.getTestDriver().getProjectPath() + File.separator+"ctw"+File.separator + fileName);
        if (testFile.exists()) {
            if (!testFile.delete()) {
                throw new IOException("Failed to delete test file");
            }
        }
        if(!testFile.createNewFile()){
            throw new IOException("Failed to create test file");
        }
        return testFile;
    }

    public static void generateTestStepsFile(TObject testObject, TProject parent) throws IOException{
        File testStepsFile = getTestFile(parent,"ctw_test_steps.c");
        StringBuilder testStepsFileContent = new StringBuilder();
        for(int i = 0; i < testObject.getTestCases().size(); i++){
            for(int j = 0; j < testObject.getTestCases().get(i).getTSteps(); j++){
                testStepsFileContent.append("void ctw_test_step_").append(i).append("_").append(j).append("(){\n");
                testStepsFileContent.append("FILE* TEST_DATA_FILE = fopen(\"test_data_output.txt\",\"w\");\n");
                testStepsFileContent.append(TWriter.getPreStepContent(testObject.getTestCases().get(i),j));
                testStepsFileContent.append(TWriter.getStepContent(testObject.getTestCases().get(i),j));
                testStepsFileContent.append(TWriter.getPostStepContent(i,testObject.getTestCases().get(i),j));
                testStepsFileContent.append("fclose(TEST_DATA_FILE);\n");
                testStepsFileContent.append("}\n");
            }
        }
        FileUtils.writeStringToFile(testStepsFile,testStepsFileContent.toString(),"UTF-8",true);
    }

    public static void compileTestDriverFile(TDriver driver) throws Exception {
        //Compile the test driver file using the given compiler
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(driver.getCompiler(),"ctw"+"/"+"ctw_test_driver.c");
        //Add the include directories and the linker to the compiler command
        for(String includeDirectory : driver.getIncludeDirectories()){
            processBuilder.command().add("-I");
            processBuilder.command().add(includeDirectory);
        }
        for(String linker : driver.getLinker()){
            processBuilder.command().add(linker);
        }
        processBuilder.command().add("-o");
        processBuilder.command().add("ctw"+"/"+"ctw_test_driver");
        //Set the current directory to the project path
        processBuilder.directory(new File(driver.getProjectPath()));
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if(exitCode != 0){
            throw new Exception("Failed to compile test driver file");
        }
        //Set the file permissions to allow execution
        File testDriverFile = new File(driver.getProjectPath()+"/"+"ctw"+"/"+"ctw_test_driver.exe");
        if(!testDriverFile.setExecutable(true)){
            throw new Exception("Failed to set test driver file permissions");
        }
    }

    public static void runTestDriverFile(TDriver driver) throws Exception {
        //Run the test driver file
        ProcessBuilder processBuilder = new ProcessBuilder(driver.getProjectPath()+"/ctw/ctw_test_driver.exe");
        File workingDirectory = new File(driver.getProjectPath()+"/"+"ctw");
        processBuilder.directory(workingDirectory);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if(exitCode != 0){
            throw new Exception("Failed to run test driver file: Exit code: "+exitCode);
        }
    }

    public static List<TCase> parseTestDataOutputFile(TObject testObject, TProject parent) {
        return null;
    }


}
