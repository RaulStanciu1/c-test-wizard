package com.ctestwizard.model.testentity;

import com.ctestwizard.model.cparser.CParserDetector;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;
import com.ctestwizard.model.testdriver.TDriver;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TProject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private TDriver testDriver;
    private List<TObject> testObjects;
    private List<CElement> structOrUnionTypes;
    private List<CElement> enumTypes;

    public TProject(String name) {
        this.name = name;
        this.testObjects = new ArrayList<>();
        this.structOrUnionTypes = new ArrayList<>();
        this.enumTypes = new ArrayList<>();
    }

    public static TProject loadProject(String projectPath) throws Exception{
        //Read the project object from a file
        try(FileInputStream fileIn = new FileInputStream(projectPath);
            ObjectInputStream in = new ObjectInputStream(fileIn)){
            return (TProject) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
            throw new Exception("Could not load project");
        }
    }

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

    public static TProject newTProject(String projectName,String sourceFilePath, String projectPath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new Exception("Source File Cannot be found");
        }
        //Create the working directory
        File projectDir = new File(projectPath + File.separator + "ctw");

        if(projectDir.exists()) {
            FileUtils.deleteDirectory(projectDir);
        }
        if (!projectDir.mkdir()) {
            throw new Exception("Could not create project directory");
        }
        //Copy the source file to the project directory
        File sourceFileCopy = new File(projectDir.getAbsolutePath() + File.separator + "ctw_src.c");
        FileUtils.copyFile(sourceFile, sourceFileCopy);
        //Preprocess the source file copy and save it
        ProcessBuilder processBuilder = new ProcessBuilder("gcc");
        processBuilder.command().add("-E");
        processBuilder.command().add(sourceFileCopy.getAbsolutePath());
        processBuilder.directory(projectDir);
        processBuilder.command().add("-o");
        processBuilder.command().add("ctw_src_pre.c");
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Preprocessing failed");
        }

        CParserDetector parser = new CParserDetector(sourceFilePath);
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
        newTestProject.setTestDriver(new TDriver(newTestProject, sourceFilePath, projectPath));

        return newTestProject;
    }


    public List<TObject> getTestObjects() {
        return testObjects;
    }


    public void setTestObjects(List<TObject> testObjects) {
        this.testObjects = testObjects;
    }

    public List<CElement> getStructOrUnionTypes() {
        return structOrUnionTypes;
    }

    public void setStructOrUnionTypes(List<CElement> structOrUnionTypes) {
        this.structOrUnionTypes = structOrUnionTypes;
    }

    public List<CElement> getEnumTypes() {
        return enumTypes;
    }

    public void setEnumTypes(List<CElement> enumTypes) {
        this.enumTypes = enumTypes;
    }

    public TDriver getTestDriver() {
        return testDriver;
    }

    public void setTestDriver(TDriver testDriver) {
        this.testDriver = testDriver;
    }

    public String getName(){
        return name;
    }
}
