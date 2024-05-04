package com.ctestwizard.model.testentity;

import com.ctestwizard.model.cparser.CParserDetector;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;
import com.ctestwizard.model.testdriver.TCompiler;
import com.ctestwizard.model.testdriver.TDriver;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TProject implements Serializable {
    private String name;
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
        } catch (Exception e) {
            e.printStackTrace();
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
        processBuilder.command().add(sourceFileCopy.getAbsolutePath());
        processBuilder.directory(workingDir);
        processBuilder.command().add(compiler.getOutputFlag());
        processBuilder.command().add("ctw_src_pre.c");
        for(String includeDirectory : compiler.getIncludeDirectories()){
            processBuilder.command().add(compiler.getIncludeFlag()+includeDirectory);
            processBuilder.command().add(includeDirectory);
        }
        for(String linkerFile : compiler.getLinkerFiles()){
            processBuilder.command().add(compiler.getLinkerFlag()+linkerFile);
        }
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
        newTestProject.setTestDriver(new TDriver(newTestProject, sourceFilePath, projectPath, compiler));

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

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
