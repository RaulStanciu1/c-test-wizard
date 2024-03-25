package com.ctestwizard.model.testentity;

import com.ctestwizard.model.cparser.CParserImpl;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TProject implements Serializable {
    private String archivePath;
    private String sourceFilePath;
    private String preprocessCommand;
    private String linkerCommand;
    private List<TObject> testObjects;
    private List<CElement> structOrUnionTypes;
    private List<CElement> enumTypes;
    public TProject(String archivePath,String sourceFilePath, String preprocessCommand,String linkerCommand, List<TObject>testObjects,List<CElement>structOrUnionTypes,List<CElement>enumTypes){
        this.archivePath = archivePath;
        this.sourceFilePath = sourceFilePath;
        this.preprocessCommand = preprocessCommand;
        this.linkerCommand = linkerCommand;
        this.structOrUnionTypes = structOrUnionTypes;
        this.enumTypes = enumTypes;
        this.testObjects = testObjects;
    }

    public static TProject newTProject(String sourceFilePath, String projectPath,String compiler) throws Exception{
        File sourceFile = new File(sourceFilePath);
        if(!sourceFile.exists()){
            throw new Exception("Source File Cannot be found");
        }
        CParserImpl parser = new CParserImpl(sourceFilePath);
        parser.walkParseTree();
        List<TObject> testObjects = new ArrayList<>();
        for(CFunction testFunction : parser.getLocalFunctionDefinitions()){
            TObject newTestObject = TObject.newTObject(parser,testFunction);
            testObjects.add(newTestObject);
        }
        String preprocessCommand = switch(compiler){
            case "gcc","clang","icc" -> "<COMPILER_COMMAND> -E <SOURCE_FILE> -o <PREPROCESSED_FILE>";
            case "cl" -> "<COMPILER_COMMAND> /E <SOURCE_FILE> > <PREPROCESSED_FILE>";
            default -> throw new Exception("Unsupported compiler");
        };
        preprocessCommand = preprocessCommand.replace("<COMPILER_COMMAND>",compiler);
        preprocessCommand = preprocessCommand.replace("<SOURCE_FILE>",sourceFilePath);
        String preprocessedFilePath = projectPath+"/"+"pre_src.c";
        preprocessCommand = preprocessCommand.replace("<PREPROCESSED_FILE>",preprocessedFilePath);
        String linkerCommand = linkerCommand(compiler,projectPath,preprocessedFilePath);
        return new TProject(projectPath,sourceFilePath,preprocessCommand,linkerCommand,testObjects,parser.getStructAndUnionDefinitions(),parser.getEnumDefinitions());
    }

    private static String linkerCommand(String compiler, String projectPath,String preprocessedFilePath) throws Exception{
        String linkerCommand = switch(compiler){
            case "gcc","clang","icc" -> "<COMPILER_COMMAND> -c <SOURCE_FILE> -o <OBJ_FILE>";
            case "cl" -> "<COMPILER_COMMAND> /c <SOURCE_FILE>  /Fo<OBJ_FILE>";
            default -> throw new Exception("Unsupported compiler");
        };
        linkerCommand = linkerCommand.replace("<COMPILER_COMMAND>",compiler);
        linkerCommand = linkerCommand.replace("<SOURCE_FILE>",preprocessedFilePath);
        linkerCommand = linkerCommand.replace("<OBJ_FILE>",projectPath+"/src.o");
        return linkerCommand;
    }

    public String getPreprocessCommand() {
        return preprocessCommand;
    }

    public void setPreprocessCommand(String preprocessCommand) {
        this.preprocessCommand = preprocessCommand;
    }

    public String getLinkerCommand() {
        return linkerCommand;
    }

    public void setLinkerCommand(String linkerCommand) {
        this.linkerCommand = linkerCommand;
    }

    public List<TObject> getTestObjects(){
        return testObjects;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
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
}
