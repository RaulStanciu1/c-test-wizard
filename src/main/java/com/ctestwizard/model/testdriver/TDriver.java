package com.ctestwizard.model.testdriver;


import com.ctestwizard.model.cparser.CParserDetector;
import com.ctestwizard.model.testentity.TCase;
import com.ctestwizard.model.testentity.TObject;
import com.ctestwizard.model.testentity.TProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TDriver {
    private final TProject _parent;
    private final String sourceFilePath;
    private final String projectPath;
    private String compiler;
    private String preprocessFlag;
    private String compileFlag;
    private final List<String> includeDirectories;
    private final List<String> linker;
    public TDriver(TProject parent,String sourceFilePath, String projectPath){
        this._parent = parent;
        this.sourceFilePath = sourceFilePath;
        this.projectPath = projectPath;
        this.compiler = "gcc";
        this.preprocessFlag = "-E";
        this.compileFlag = "-c";
        this.includeDirectories = new ArrayList<>();
        this.linker = new ArrayList<>();
    }

    /**
     * Analyze the source file for any changes, if changes have been found, update every
     * TObject and TInterface with the new elements added or removed
     */
    public int analyze() throws IOException, InterruptedException {
       /*Check if the working directory exists and if it does
        check if the source file copy is the same as the source file*/
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new IOException("Source File Cannot be found");
        }
        File projectDir = new File(projectPath+File.separator+"ctw");
        if (!projectDir.exists()) {
            if(!projectDir.mkdir()){
                throw new IOException("Could not create project directory");
            }
            //Copy the source file to the project directory
            File sourceFileCopy = new File(projectDir.getAbsolutePath()+File.separator+"ctw_src.c");
            FileUtils.copyFile(sourceFile,sourceFileCopy);

            //Preprocess the source file copy and save it
            ProcessBuilder processBuilder = new ProcessBuilder(compiler);
            processBuilder.command().add(preprocessFlag);
            processBuilder.command().add(sourceFileCopy.getAbsolutePath());
            processBuilder.directory(projectDir);
            processBuilder.command().add("-o");
            processBuilder.command().add("ctw_src_pre.c");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new IOException("Preprocessing failed");
            }
        }
        //Check if the source file has changed and if the copy exists
        File sourceFileCopy = new File(projectDir.getAbsolutePath()+File.separator+"ctw_src.c");
        if(!sourceFileCopy.exists()){
            FileUtils.copyFile(sourceFile,sourceFileCopy);
        }
        File preprocessedFile = new File(projectDir.getAbsolutePath()+File.separator+"ctw_src_pre.c");
        if(!preprocessedFile.exists()){
            //Preprocess the source file copy and save it
            ProcessBuilder processBuilder = new ProcessBuilder(compiler);
            processBuilder.command().add(preprocessFlag);
            processBuilder.command().add(sourceFileCopy.getAbsolutePath());
            processBuilder.directory(projectDir);
            processBuilder.command().add("-o");
            processBuilder.command().add("ctw_src_pre.c");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new IOException("Preprocessing failed");
            }
        }
        if(!FileUtils.contentEquals(sourceFile,sourceFileCopy)){
            //Preprocess the source file copy and save it
            ProcessBuilder processBuilder = new ProcessBuilder(compiler);
            processBuilder.command().add(preprocessFlag);
            processBuilder.command().add(sourceFileCopy.getAbsolutePath());
            processBuilder.directory(projectDir);
            processBuilder.command().add("-o");
            processBuilder.command().add("ctw_src_pre.c");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new IOException("Preprocessing failed");
            }
        }

        int interfaceChanged = 0;

        //Parse the preprocessed source file
        CParserDetector parser = new CParserDetector(projectDir.getAbsolutePath()+File.separator+"ctw_src_pre.c");
        parser.walkParseTree();

        //Compare the enumDefinitions and structAndUnionDefinitions with the ones in the project
        if(TDriverUtils.compareEnumDefinitions(parser.getEnumDefinitions(),_parent.getEnumTypes())){
            _parent.setEnumTypes(parser.getEnumDefinitions());
        }
        if(TDriverUtils.compareStructAndUnionDefinitions(parser.getStructAndUnionDefinitions(),_parent.getStructOrUnionTypes())){
            _parent.setStructOrUnionTypes(parser.getStructAndUnionDefinitions());
        }

        //Update every TObject and TInterface with the new elements added or removed
        for(int i = 0; i < _parent.getTestObjects().size(); i++){
            TObject testObject = _parent.getTestObjects().get(i);
            //Check if the test function has changed
            if(TDriverUtils.compareCFunction(testObject.getTestFunction(),parser.getLocalFunctionDefinitions())){
                int updateStatus = testObject.updateTestFunction(parser.getLocalFunctionDefinitions());
                if(updateStatus == -1){
                    //Remove the test object if it has been removed
                    _parent.getTestObjects().remove(i);
                    i--;
                }
                interfaceChanged = 1;
            }
            //Compare the local functions in the test object with the local functions in the parser
            if(TDriverUtils.compareCFunctionList(testObject.getTestFunction(),testObject.getTestInterface().getLocalFunctions(),parser.getLocalFunctionDefinitions())){
                testObject.updateLocalFunctions(parser.getLocalFunctionDefinitions());
                interfaceChanged = 1;
            }
            //Compare the external functions in the test object with the external functions in the parser
            if(TDriverUtils.compareCFunctionList(testObject.getTestFunction(),testObject.getTestInterface().getExternalFunctions(),parser.getExternalFunctionDefinitions())){
                testObject.updateExternalFunctions(parser.getExternalFunctionDefinitions());
                interfaceChanged = 1;
            }
            //Compare the globals in the test object with the globals in the parser
            if(TDriverUtils.compareCElementList(testObject.getTestInterface().getGlobals().keySet(),parser.getGlobals())){
                testObject.updateGlobals(parser.getGlobals());
                interfaceChanged = 1;
            }

        }

        return interfaceChanged;
    }

    public List<TCase> executeTestObject(TObject testObject) throws Exception {
        //Step 1: Analyze to check for any source file changes
        int interfaceChanged = analyze();
        if(interfaceChanged == 1){
            //throw new Exception("Interface has changed! Please analyze the changes.");
        }

        //Step 2: Create the test driver file from the resource template
        TDriverUtils.generateTestDriverFile(_parent);
        //Step 3: Create the stub code file
        TDriverUtils.createStubCodeFile(testObject,_parent);
        //Step 4: Generate the test data file
        TDriverUtils.generateTestDataFile(testObject,_parent);
        //Step 5: Generate the test steps file
        TDriverUtils.generateTestStepsFile(testObject,_parent);
        //Step 6: Compile the test driver file
        TDriverUtils.compileTestDriverFile(this);
        //Step 7: Run the test driver file
        TDriverUtils.runTestDriverFile(this);
        //Step 8: Parse the test data file and return the test cases
        return TDriverUtils.parseTestDataOutputFile(testObject,_parent);
    }

    public void addIncludeDirectory(String includeDirectory){
        this.includeDirectories.add(includeDirectory);
    }

    public void addLinker(String linker){
        this.linker.add(linker);
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public List<String> getIncludeDirectories() {
        return includeDirectories;
    }
    public List<String> getLinker() {
        return linker;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getPreprocessFlag() {
        return preprocessFlag;
    }

    public void setPreprocessFlag(String preprocessFlag) {
        this.preprocessFlag = preprocessFlag;
    }

    public String getCompileFlag() {
        return compileFlag;
    }

    public void setCompileFlag(String compileFlag) {
        this.compileFlag = compileFlag;
    }
}
