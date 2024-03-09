package com.ctestwizard.model.projectentity;

import com.ctestwizard.model.testentity.TObject;

import java.util.List;

public class Project {
    private String name;
    private String description;
    private String sourceFilePath;
    private String compiler;
    private String preprocessorFlag;
    private List<TObject> testObjects;
}
