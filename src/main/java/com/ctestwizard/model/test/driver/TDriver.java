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
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity used to store all the necessary information related to test execution
 */
public class TDriver implements Serializable {
    @Serial
    private static final long serialVersionUID = 105L;
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

    /**
     * Constructor for the test driver
     * @param parent The parent project
     * @param sourceFilePath The source file path
     * @param projectPath The project path
     * @param compiler The compiler
     */
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
     * @param consoleWriter The console writer(for UI)
     * @return 1 if the interface has changed, 0 otherwise
     * @throws IOException If there was an issue analyzing the source file
     * @throws InterruptedException If the process was interrupted
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

    /**
     * Execute the test object
     * @param testObject The test object
     * @param consoleWriter The console writer(for UI)
     * @return The summary of the test results
     * @throws Exception If there were issues executing the test object
     */
    public TSummary executeTestObject(TObject testObject,ConsoleWriter consoleWriter) throws Exception {
        if(!coverageEnabled){
            return executeNoCoverage(testObject,consoleWriter);
        }else{
            return executeCoverage(testObject,consoleWriter);
        }
    }

    /**
     * Execute the test object without coverage
     * @param testObject The test object
     * @param consoleWriter The console writer(for UI)
     * @return The summary of the test results
     * @throws Exception If there were issues executing the test object
     */
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

    /**
     * Execute the test object with coverage
     * @param testObject The test object
     * @param consoleWriter The console writer(for UI)
     * @return The summary of the test results
     * @throws Exception If there were issues executing the test object
     */
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

    /**
     * Get the source file path
     * @return The source file path
     */
    public String getSourceFilePath() {
        return sourceFilePath;
    }

    /**
     * Get the project path
     * @return The project path
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * Get the include directories
     * @return The include directories
     */
    public List<String> getIncludeDirectories() {
        return compiler.getIncludeDirectories();
    }

    /**
     * Get the linker files
     * @return The linker files
     */
    public List<String> getLinker() {
        return compiler.getLinkerFiles();
    }

    /**
     * Get the compiler
     * @return The compiler
     */
    public String getCompiler() {
        return compiler.getCompiler();
    }

    /**
     * Set the compiler
     * @param compiler The compiler
     */
    public void setCompiler(String compiler) {
        this.compiler.setCompiler(compiler);
    }

    /**
     * Get the preprocess flag
     * @return The preprocess flag
     */
    public String getPreprocessFlag() {
        return compiler.getPreprocessFlag();
    }

    /**
     * Set the preprocess flag
     * @param preprocessFlag The preprocess flag
     */
    public void setPreprocessFlag(String preprocessFlag) {
        this.compiler.setPreprocessFlag(preprocessFlag);
    }

    /**
     * Get the compile flag
     * @return The compile flag
     */
    public String getCompileFlag() {
        return this.compiler.getCompileFlag();
    }

    /**
     * Set the compile flag
     * @param compileFlag The compile flag
     */
    public void setCompileFlag(String compileFlag) {
        this.compiler.setCompileFlag(compileFlag);
    }

    /**
     * Get the source file path
     * @param sourceFilePath The source file path
     */
    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    /**
     * Set the project path
     * @param projectPath The project path
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * Get the defines list
     * @return The defines list
     */
    public List<CDefine> getDefines() {
        return defines;
    }

    /**
     * Get the test header path
     * @return The test header path
     */
    public String getTestHeaderPath() {
        return testHeaderPath;
    }

    /**
     * Set the test header path
     * @param testHeaderPath The test header path
     */
    public void setTestHeaderPath(String testHeaderPath) {
        this.testHeaderPath = testHeaderPath;
    }

    /**
     * Get the output flag
     * @return The output flag
     */
    public String getOutputFlag() {
        return compiler.getOutputFlag();
    }

    /**
     * Set the output flag
     * @param outputFlag The output flag
     */
    public void setOutputFlag(String outputFlag) {
        this.compiler.setOutputFlag(outputFlag);
    }

    /**
     * Get the linker flag
     * @return The linker flag
     */
    public String getLinkerFlag() {
        return compiler.getLinkerFlag();
    }

    /**
     * Set the linker flag
     * @param linkerFlag The linker flag
     */
    public void setLinkerFlag(String linkerFlag) {
        this.compiler.setLinkerFlag(linkerFlag);
    }

    /**
     * Get the include flag
     * @return The include flag
     */
    public String getIncludeFlag() {
        return compiler.getIncludeFlag();
    }

    /**
     * Set the include flag
     * @param includeFlag The include flag
     */
    public void setIncludeFlag(String includeFlag) {
        this.compiler.setIncludeFlag(includeFlag);
    }

    /**
     * Get the additional flags
     * @return  The additional flags
     */
    public String getAdditionalFlags() {
        return compiler.getAdditionalFlags();
    }

    /**
     * Set the additional flags
     * @param additionalFlags The additional flags
     */
    public void setAdditionalFlags(String additionalFlags) {
        this.compiler.setAdditionalFlags(additionalFlags);
    }

    /**
     * Get the coverage significance
     * @return The coverage significance
     */
    public Double getCoverageSignificance() {
        return coverageSignificance;
    }

    /**
     * Set the coverage significance
     * @param coverageSignificance The coverage significance
     */
    public void setCoverageSignificance(Double coverageSignificance) {
        this.coverageSignificance = coverageSignificance;
    }

    /**
     * Get the result significance
     * @return The result significance
     */
    public Double getResultSignificance() {
        return resultSignificance;
    }

    /**
     * Set the result significance
     * @param resultSignificance The result significance
     */
    public void setResultSignificance(Double resultSignificance) {
        this.resultSignificance = resultSignificance;
    }

    /**
     * Get if coverage is enabled
     * @return If coverage is enabled
     */
    public boolean isCoverageEnabled() {
        return coverageEnabled;
    }

    /**
     * Set if coverage is enabled
     * @param coverageEnabled If coverage is enabled
     */
    public void setCoverageEnabled(boolean coverageEnabled) {
        this.coverageEnabled = coverageEnabled;
    }

    /**
     * Get if report is enabled
     * @return If report is enabled
     */
    public boolean isReportEnabled() {
        return reportEnabled;
    }

    /**
     * Set if report is enabled
     * @param reportEnabled If report is enabled
     */
    public void setReportEnabled(boolean reportEnabled) {
        this.reportEnabled = reportEnabled;
    }

    /**
     * Add an Include Directory
     * @param includeDirectory The include directory
     */
    public void addIncludeDirectory(String includeDirectory){
        compiler.getIncludeDirectories().add(includeDirectory);
    }

    /**
     * Add a Linker File
     * @param linker The linker file
     */
    public void addLinker(String linker){
        compiler.getLinkerFiles().add(linker);
    }

    /**
     * Get the Object Files
     * @return The object files
     */
    public List<String> getObjectFiles(){
        return compiler.getObjectFiles();
    }

    /**
     * Add an Object File
     * @param objectFile The object file
     */
    public void addObjectFile(String objectFile){
        compiler.getObjectFiles().add(objectFile);
    }
}
