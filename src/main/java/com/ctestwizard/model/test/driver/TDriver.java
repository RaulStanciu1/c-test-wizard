package com.ctestwizard.model.test.driver;


import com.ctestwizard.model.code.entity.CFunction;
import com.ctestwizard.model.code.parser.CParserDetector;
import com.ctestwizard.model.code.entity.CDefine;
import com.ctestwizard.model.coverage.CoverageInstrumenter;
import com.ctestwizard.model.exception.InterfaceChangedException;
import com.ctestwizard.model.test.entity.TObject;
import com.ctestwizard.model.test.entity.TProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TDriver implements Serializable {
    private final TProject _parent;
    private String sourceFilePath;
    private  String projectPath;
    private String testHeaderPath;
    private final TCompiler compiler;
    private final List<CDefine> defines;
    private Double coverageSignificance; /* Percentage that decides when code coverage is considered passed */
    private Double resultSignificance; /* Percentage that decides when test results are considered passed */
    private boolean coverageEnabled;
    private boolean reportEnabled;
    public TDriver(TProject parent,String sourceFilePath, String projectPath, TCompiler compiler){
        this._parent = parent;
        this.sourceFilePath = sourceFilePath;
        this.projectPath = projectPath;
        this.testHeaderPath = projectPath+File.separator+"ctw_test.h";
        this.compiler = compiler;
        this.defines = new ArrayList<>();
        this.defines.add(new CDefine("const",""));
        this.defines.add(new CDefine("volatile",""));
        this.coverageSignificance = 80.0;
        this.resultSignificance = 100.0;
        this.coverageEnabled = false;
        this.reportEnabled = false;
    }

    /**
     * Analyze the source file for any changes, if changes have been found, update every
     * TObject and TInterface with the new elements added or removed
     */
    public int analyze(ConsoleWriter consoleWriter) throws IOException, InterruptedException {
       /*Check if the working directory exists and if it does
        check if the source file copy is the same as the source file*/
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new IOException("Source File Cannot be found");
        }
        File projectDir = new File(projectPath+File.separator+ "ctw");
        if (!projectDir.exists()) {
            if(!projectDir.mkdir()){
                throw new IOException("Could not create project directory");
            }
            //Copy the source file to the project directory
            File sourceFileCopy = new File(projectDir.getAbsolutePath()+File.separator+"ctw_src.c");
            FileUtils.copyFile(sourceFile,sourceFileCopy);

            //Preprocess the source file copy and save it
            ProcessBuilder processBuilder = new ProcessBuilder(compiler.getCompiler());
            processBuilder.command().add(compiler.getPreprocessFlag());
            processBuilder.command().add(sourceFileCopy.getAbsolutePath());
            processBuilder.directory(projectDir);
            for(String includeDir : compiler.getIncludeDirectories()){
                processBuilder.command().add(compiler.getIncludeFlag()+includeDir);
            }
            for(String linkerFile : compiler.getLinkerFiles()){
                processBuilder.command().add(compiler.getLinkerFlag()+linkerFile);
            }
            processBuilder.command().add(compiler.getOutputFlag());
            processBuilder.command().add("ctw_src_pre.c");
            if(compiler.getAdditionalFlags() != null && !compiler.getAdditionalFlags().isEmpty()){
                processBuilder.command().add(compiler.getAdditionalFlags());
            }
            Process process = processBuilder.start();
            consoleWriter.redirectOutput(process);
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
            ProcessBuilder processBuilder = new ProcessBuilder(compiler.getCompiler());
            processBuilder.command().add(compiler.getPreprocessFlag());
            processBuilder.command().add(sourceFileCopy.getAbsolutePath());
            for(String includeDir : compiler.getIncludeDirectories()){
                processBuilder.command().add(compiler.getIncludeFlag()+includeDir);
            }
            for(String linkerFile : compiler.getLinkerFiles()){
                processBuilder.command().add(compiler.getLinkerFlag()+linkerFile);
            }
            processBuilder.directory(projectDir);
            processBuilder.command().add(compiler.getOutputFlag());
            processBuilder.command().add("ctw_src_pre.c");
            if(compiler.getAdditionalFlags() != null && !compiler.getAdditionalFlags().isEmpty()){
                processBuilder.command().add(compiler.getAdditionalFlags());
            }
            Process process = processBuilder.start();
            consoleWriter.redirectOutput(process);
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new IOException("Preprocessing failed");
            }
        }
        if(!FileUtils.contentEquals(sourceFile,sourceFileCopy)){
            FileUtils.copyFile(sourceFile,sourceFileCopy);
            //Preprocess the source file copy and save it
            ProcessBuilder processBuilder = new ProcessBuilder(compiler.getCompiler());
            processBuilder.command().add(compiler.getPreprocessFlag());
            processBuilder.command().add(sourceFile.getAbsolutePath());
            for(String includeDir : compiler.getIncludeDirectories()){
                processBuilder.command().add(compiler.getIncludeFlag()+includeDir);
            }
            processBuilder.directory(projectDir);
            processBuilder.command().add(compiler.getOutputFlag());
            processBuilder.command().add("ctw_src_pre.c");
            if(compiler.getAdditionalFlags() != null && !compiler.getAdditionalFlags().isEmpty()){
                processBuilder.command().add(compiler.getAdditionalFlags());
            }
            Process process = processBuilder.start();
            consoleWriter.redirectOutput(process);
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
        if(TComparator.compareEnumDefinitions(parser.getEnumDefinitions(),_parent.getEnumTypes())){
            _parent.setEnumTypes(parser.getEnumDefinitions());
        }
        if(TComparator.compareStructAndUnionDefinitions(parser.getStructAndUnionDefinitions(),_parent.getStructOrUnionTypes())){
            _parent.setStructOrUnionTypes(parser.getStructAndUnionDefinitions());
        }

        //Update every TObject and TInterface with the new elements added or removed
        for(int i = 0; i < _parent.getTestObjects().size(); i++){
            TObject testObject = _parent.getTestObjects().get(i);
            //Check if the test function has changed
            if(TComparator.compareCFunction(testObject.getTestFunction(),parser.getLocalFunctionDefinitions())){
                int updateStatus = testObject.updateTestFunction(parser.getLocalFunctionDefinitions());
                if(updateStatus == -1){
                    //Remove the test object if it has been removed
                    _parent.getTestObjects().remove(i);
                    i--;
                }
                interfaceChanged = 1;
            }
            //Compare the local functions in the test object with the local functions in the parser
            if(TComparator.compareCFunctionList(testObject.getTestFunction(),testObject.getTestInterface().getLocalFunctions(),parser.getLocalFunctionDefinitions())){
                testObject.updateLocalFunctions(parser.getLocalFunctionDefinitions());
                interfaceChanged = 1;
            }
            //Compare the external functions in the test object with the external functions in the parser
            if(TComparator.compareCFunctionList(testObject.getTestFunction(),testObject.getTestInterface().getExternalFunctions(),parser.getExternalFunctionDefinitions())){
                testObject.updateExternalFunctions(parser.getExternalFunctionDefinitions());
                interfaceChanged = 1;
            }
            //Compare the globals in the test object with the globals in the parser
            if(TComparator.compareCElementList(testObject.getTestInterface().getGlobals().keySet(),parser.getGlobals())){
                testObject.updateGlobals(parser.getGlobals());
                interfaceChanged = 1;
            }

        }

        //Remove test objects not present in the source file anymore
        for(TObject testObject : _parent.getTestObjects()){
            boolean found = false;
            for(CFunction localFunction : parser.getLocalFunctionDefinitions()){
                if(testObject.getTestFunction().getName().strip().equals(localFunction.getName().strip())){
                    found = true;
                    break;
                }
            }
            if(!found){
                _parent.getTestObjects().remove(testObject);
                interfaceChanged = 1;
            }
        }

        //Add any new test objects present in the source file
        for(CFunction localFunction : parser.getLocalFunctionDefinitions()){
            boolean found = false;
            for(TObject testObject : _parent.getTestObjects()){
                if(testObject.getTestFunction().getName().strip().equals(localFunction.getName().strip())){
                    found = true;
                    break;
                }
            }
            if(!found){
                TObject newTestObject = TObject.newTObject(_parent,parser,localFunction);
                _parent.getTestObjects().add(newTestObject);
                interfaceChanged = 1;
            }
        }

        return interfaceChanged;
    }

    public TSummary executeTestObject(TObject testObject,ConsoleWriter consoleWriter) throws Exception {
        if(!coverageEnabled){
            return executeNoCoverage(testObject,consoleWriter);
        }else{
            return executeCoverage(testObject,consoleWriter);
        }
    }

    private TSummary executeNoCoverage(TObject testObject, ConsoleWriter consoleWriter) throws Exception{
        try{
            //Step 1: Analyze to check for any source file changes
            int interfaceChanged = analyze(consoleWriter);
            if(interfaceChanged == 1){
                throw new InterfaceChangedException("Interface has changed! Analyze Changes.");
            }
            //Step 2: Create the defines header file
            TDriverUtils.generateDefinesFile(_parent);
            //Step 3: Create the test header file
            TDriverUtils.generateTestHeaderFile(this);
            //Step 4: Create the test driver file from the resource template
            TDriverUtils.generateTestDriverFile(_parent);
            //Step 5: Create the stub code file
            TDriverUtils.createStubCodeFile(testObject,_parent);
            //Step 6: Create the prologue and epilogue file
            TDriverUtils.generatePrologueEpilogueFile(testObject,_parent);
            //Step 7: Generate the test data file
            TDriverUtils.generateTestDataFile(testObject,_parent);
            //Step 8: Generate the test steps file
            TDriverUtils.generateTestStepsFile(testObject,_parent);
            //Step 9: Compile the test driver file
            TDriverUtils.compileTestDriverFile(this,consoleWriter);
            //Step 10: Run the test driver file
            TDriverUtils.runTestDriverFile(this,consoleWriter);
            //Step 11: Parse the test data file and return the test cases
            List<TResults> results = TDriverUtils.parseTestDataOutputFile(testObject,_parent);
            //Step 12: Get a summary of the results
            return new TSummary(testObject,results,false);
        } finally{
            TDriverUtils.cleanUpTestDriverFiles(this);
        }

    }

    private TSummary executeCoverage(TObject testObject, ConsoleWriter consoleWriter) throws Exception{
        try{
            //Step 1: Analyze to check for any source file changes
            int interfaceChanged = analyze(consoleWriter);
            if(interfaceChanged == 1){
                throw new InterfaceChangedException("Interface has changed! Analyze Changes.");
            }
            //Step 2: Create the defines header file
            TDriverUtils.generateDefinesFile(_parent);
            //Step 3: Create the test header file
            TDriverUtils.generateTestHeaderFile(this);
            //Step 4: Create the test driver coverage file from the resource template
            TDriverUtils.generateTestDriverCoverageFile(_parent);
            //Step 5: Create the stub code file
            TDriverUtils.createStubCodeFile(testObject,_parent);
            //Step 6: Create the prologue and epilogue file
            TDriverUtils.generatePrologueEpilogueFile(testObject,_parent);
            //Step 7: Generate the test data file
            TDriverUtils.generateTestDataFile(testObject,_parent);
            //Step 8: Generate the test steps file
            TDriverUtils.generateTestStepsFile(testObject,_parent);
            //Step 9: Instrument the source file with the decisions
            CoverageInstrumenter.instrumentObject(testObject);
            //Step 10: Compile the test driver file
            TDriverUtils.compileTestDriverFile(this,consoleWriter);
            //Step 11: Run the test driver file
            TDriverUtils.runTestDriverFile(this,consoleWriter);
            //Step 12: Parse the test data file and return the test cases
            List<TResults> results = TDriverUtils.parseTestDataOutputFile(testObject,_parent);
            //Step 13: Get a summary of the results
            return new TSummary(testObject,results,true);
        }finally{
            TDriverUtils.cleanUpTestDriverFiles(this);
        }

    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public List<String> getIncludeDirectories() {
        return compiler.getIncludeDirectories();
    }
    public List<String> getLinker() {
        return compiler.getLinkerFiles();
    }

    public String getCompiler() {
        return compiler.getCompiler();
    }

    public void setCompiler(String compiler) {
        this.compiler.setCompiler(compiler);
    }

    public String getPreprocessFlag() {
        return compiler.getPreprocessFlag();
    }

    public void setPreprocessFlag(String preprocessFlag) {
        this.compiler.setPreprocessFlag(preprocessFlag);
    }

    public String getCompileFlag() {
        return this.compiler.getCompileFlag();
    }

    public void setCompileFlag(String compileFlag) {
        this.compiler.setCompileFlag(compileFlag);
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public List<CDefine> getDefines() {
        return defines;
    }

    public String getTestHeaderPath() {
        return testHeaderPath;
    }

    public void setTestHeaderPath(String testHeaderPath) {
        this.testHeaderPath = testHeaderPath;
    }

    public String getOutputFlag() {
        return compiler.getOutputFlag();
    }

    public void setOutputFlag(String outputFlag) {
        this.compiler.setOutputFlag(outputFlag);
    }

    public String getLinkerFlag() {
        return compiler.getLinkerFlag();
    }

    public void setLinkerFlag(String linkerFlag) {
        this.compiler.setLinkerFlag(linkerFlag);
    }

    public String getIncludeFlag() {
        return compiler.getIncludeFlag();
    }

    public void setIncludeFlag(String includeFlag) {
        this.compiler.setIncludeFlag(includeFlag);
    }

    public String getAdditionalFlags() {
        return compiler.getAdditionalFlags();
    }

    public void setAdditionalFlags(String additionalFlags) {
        this.compiler.setAdditionalFlags(additionalFlags);
    }

    public Double getCoverageSignificance() {
        return coverageSignificance;
    }

    public void setCoverageSignificance(Double coverageSignificance) {
        this.coverageSignificance = coverageSignificance;
    }

    public Double getResultSignificance() {
        return resultSignificance;
    }

    public void setResultSignificance(Double resultSignificance) {
        this.resultSignificance = resultSignificance;
    }

    public boolean isCoverageEnabled() {
        return coverageEnabled;
    }

    public void setCoverageEnabled(boolean coverageEnabled) {
        this.coverageEnabled = coverageEnabled;
    }

    public boolean isReportEnabled() {
        return reportEnabled;
    }

    public void setReportEnabled(boolean reportEnabled) {
        this.reportEnabled = reportEnabled;
    }

    public void addIncludeDirectory(String includeDirectory){
        compiler.getIncludeDirectories().add(includeDirectory);
    }

    public void addLinker(String linker){
        compiler.getLinkerFiles().add(linker);
    }

    public List<String> getObjectFiles(){
        return compiler.getObjectFiles();
    }
}
