package com.ctestwizard.model.testentity;

import com.ctestwizard.model.cparser.CParserDetector;
import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;
import com.ctestwizard.model.testdriver.TDriver;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TProject implements Serializable {
    private TDriver testDriver;
    private List<TObject> testObjects;
    private List<CElement> structOrUnionTypes;
    private List<CElement> enumTypes;

    public TProject() {
        this.testObjects = new ArrayList<>();
        this.structOrUnionTypes = new ArrayList<>();
        this.enumTypes = new ArrayList<>();
    }
    public static TProject newTProject(String sourceFilePath, String projectPath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new Exception("Source File Cannot be found");
        }
        CParserDetector parser = new CParserDetector(sourceFilePath);
        parser.walkParseTree();
        List<TObject> testObjects = new ArrayList<>();
        TProject newTestProject = new TProject();
        for (CFunction testFunction : parser.getLocalFunctionDefinitions()) {
            TObject newTestObject = TObject.newTObject(newTestProject, parser, testFunction);
            testObjects.add(newTestObject);
        }
        newTestProject.setTestObjects(testObjects);
        newTestProject.setStructOrUnionTypes(parser.getStructAndUnionDefinitions());
        newTestProject.setEnumTypes(parser.getEnumDefinitions());
        newTestProject.setTestDriver(new TDriver(newTestProject,sourceFilePath, projectPath));

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
}
