package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.parser.CParserDetector;
import com.ctestwizard.model.code.entity.CElement;
import com.ctestwizard.model.code.entity.CFunction;
import com.ctestwizard.model.test.driver.TCompiler;
import com.ctestwizard.model.test.driver.TDriver;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity used to store all the user's information about a project
 */
public class TProject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private TDriver testDriver;
    private List<TObject> testObjects;
    private List<CElement> structOrUnionTypes;
    private List<CElement> enumTypes;

    /**
     * Constructor for the project
     * @param name The name of the project
     */
    public TProject(String name) {
        this.name = name;
        this.testObjects = new ArrayList<>();
        this.structOrUnionTypes = new ArrayList<>();
        this.enumTypes = new ArrayList<>();
    }

    /**
     * Load a project from a .ctw file using deserialization
     * @param projectPath The path to the project file
     * @return The project object
     * @throws Exception If there were issues loading the project
     */
    public static TProject loadProject(String projectPath) throws Exception{
        //Read the project object from a file
        try(FileInputStream fileIn = new FileInputStream(projectPath);
            ObjectInputStream in = new ObjectInputStream(fileIn)){
            return (TProject) in.readObject();
        } catch (Exception e) {
            throw new Exception("Could not load project");
        }
    }

    /**
     * Archive a project to a .ctw file using serialization
     * @param project The project to archive
     * @throws Exception If there were issues archiving the project
     */
    public static void archiveProject(TProject project) throws Exception{
        //Write the project object to a file
        String projectPath = project.getTestDriver().getProjectPath()+File.separator+project.getName()+".ctw";
        try(FileOutputStream fileOut = new FileOutputStream(projectPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(project);
        } catch (IOException e) {
            throw new Exception("Could not archive project");
        }
    }

    /**
     * Create a new project given the necessary data from the user
     * @param projectName The name of the project
     * @param sourceFilePath The path to the source file
     * @param projectPath The path to the project directory
     * @param compiler The compiler to use for the project
     * @return The new project object
     * @throws Exception If the source file cannot be found, the project directory cannot be created, or preprocessing fails
     */
    public static TProject newTProject(String projectName,String sourceFilePath, String projectPath, TCompiler compiler) throws Exception {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new Exception("Source File Cannot be found");
        }

        File projectFolder = new File(projectPath);
        if (!projectFolder.exists()) {
            if (!projectFolder.mkdir()) {
                throw new Exception("Could not create project directory");
            }
        }
        //Create the working directory
        File workingDir = new File(projectPath + File.separator + "ctw");

        if(workingDir.exists()) {
            FileUtils.deleteDirectory(workingDir);
        }
        if (!workingDir.mkdir()) {
            throw new Exception("Could not create project directory");
        }
        //Copy the source file to the project directory
        File sourceFileCopy = new File(workingDir.getAbsolutePath() + File.separator + "ctw_src.c");
        FileUtils.copyFile(sourceFile, sourceFileCopy);
        //Preprocess the source file copy and save it
        ProcessBuilder processBuilder = new ProcessBuilder(compiler.getCompiler());
        processBuilder.command().add(compiler.getPreprocessFlag());
        processBuilder.command().add(sourceFile.getAbsolutePath());
        processBuilder.directory(workingDir);
        processBuilder.command().add(compiler.getOutputFlag());
        processBuilder.command().add(workingDir.getAbsolutePath() + File.separator+"ctw_src_pre.c");
        for(String includeDirectory : compiler.getIncludeDirectories()){
            processBuilder.command().add(compiler.getIncludeFlag());
            processBuilder.command().add(includeDirectory);
        }
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Preprocessing failed");
        }
        //Parse the preprocessed file
        CParserDetector parser = new CParserDetector(workingDir.getAbsolutePath() + File.separator+"ctw_src_pre.c");
        parser.walkParseTree();
        List<TObject> testObjects = new ArrayList<>();
        TProject newTestProject = new TProject(projectName);
        for (CFunction testFunction : parser.getLocalFunctionDefinitions()) {
            TObject newTestObject = TObject.newTObject(newTestProject, parser, testFunction);
            testObjects.add(newTestObject);
        }
        newTestProject.setTestObjects(testObjects);
        newTestProject.setStructOrUnionTypes(parser.getStructAndUnionDefinitions());
        newTestProject.setEnumTypes(parser.getEnumDefinitions());
        newTestProject.setTestDriver(new TDriver(newTestProject, sourceFilePath, projectPath, compiler));
        //Add the project to the project list
        String projectListFolderPath = System.getProperty("user.home")+File.separator+".ctestwizard";
        String projectListFilePath = projectListFolderPath+File.separator+"ProjectList.lst";
        File projectListFolder = new File(projectListFolderPath);
        if(!projectListFolder.exists()){
            if(!projectListFolder.mkdir()){
                throw new Exception("Could not create project list folder");
            }
        }
        File projectListFile = new File(projectListFilePath);
        if(!projectListFile.exists()){
            if(!projectListFile.createNewFile()){
                throw new Exception("Could not create project list file");
            }
            FileUtils.writeStringToFile(projectListFile,newTestProject.testDriver.getProjectPath()+File.separator+newTestProject.getName()+".ctw\n","UTF-8",true);
        }else{
            FileUtils.writeStringToFile(projectListFile,newTestProject.testDriver.getProjectPath()+File.separator+newTestProject.getName()+".ctw\n","UTF-8",true);
        }
        return newTestProject;
    }

    /**
     * Get the test objects
     * @return The test objects
     */
    public List<TObject> getTestObjects() {
        return testObjects;
    }

    /**
     * Set the test objects
     * @param testObjects The test objects
     */
    public void setTestObjects(List<TObject> testObjects) {
        this.testObjects = testObjects;
    }

    /**
     *  Get the struct or union types
     * @return The struct or union types
     */
    public List<CElement> getStructOrUnionTypes() {
        return structOrUnionTypes;
    }

    /**
     *  Set the struct or union types
     * @param structOrUnionTypes The struct or union types
     */
    public void setStructOrUnionTypes(List<CElement> structOrUnionTypes) {
        this.structOrUnionTypes = structOrUnionTypes;
    }

    /**
     * Get the enum types
     * @return The enum types
     */
    public List<CElement> getEnumTypes() {
        return enumTypes;
    }

    /**
     * Set the enum types
     * @param enumTypes The enum types
     */
    public void setEnumTypes(List<CElement> enumTypes) {
        this.enumTypes = enumTypes;
    }

    /**
     * Get the test driver
     * @return The test driver
     */
    public TDriver getTestDriver() {
        return testDriver;
    }

    /**
     * Set the test driver
     * @param testDriver The test driver
     */
    public void setTestDriver(TDriver testDriver) {
        this.testDriver = testDriver;
    }

    /**
     * Get the name of the project
     * @return The name of the project
     */
    public String getName(){
        return name;
    }

    /**
     * Set the name of the project
     * @param name The name of the project
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Get the string representation of the project
     * @return The name of the project
     */
    public String toString(){
        return name;
    }
}
