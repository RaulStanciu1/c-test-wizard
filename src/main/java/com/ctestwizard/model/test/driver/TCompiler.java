package com.ctestwizard.model.test.driver;

import java.io.Serializable;
import java.util.List;

public class TCompiler implements Serializable {
    private String compiler;
    private String preprocessFlag;
    private String compileFlag;
    private String outputFlag;
    private String linkerFlag;
    private String includeFlag;
    private List<String> includeDirectories;
    private List<String> linkerFiles;
    private String additionalFlags;
    public TCompiler(String compiler, String preprocessFlag, String compileFlag, String outputFlag, String linkerFlag, String includeFlag, List<String> includeDirectories, List<String> linkerFiles, String additionalFlags){
        this.compiler = compiler;
        this.preprocessFlag = preprocessFlag;
        this.compileFlag = compileFlag;
        this.outputFlag = outputFlag;
        this.linkerFlag = linkerFlag;
        this.includeFlag = includeFlag;
        this.includeDirectories = includeDirectories;
        this.linkerFiles = linkerFiles;
        this.additionalFlags = additionalFlags;
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

    public String getOutputFlag() {
        return outputFlag;
    }

    public void setOutputFlag(String outputFlag) {
        this.outputFlag = outputFlag;
    }

    public String getLinkerFlag() {
        return linkerFlag;
    }

    public void setLinkerFlag(String linkerFlag) {
        this.linkerFlag = linkerFlag;
    }

    public String getIncludeFlag() {
        return includeFlag;
    }

    public void setIncludeFlag(String includeFlag) {
        this.includeFlag = includeFlag;
    }

    public List<String> getIncludeDirectories() {
        return includeDirectories;
    }

    public void setIncludeDirectories(List<String> includeDirectories) {
        this.includeDirectories = includeDirectories;
    }

    public List<String> getLinkerFiles() {
        return linkerFiles;
    }

    public void setLinkerFiles(List<String> linkerFiles) {
        this.linkerFiles = linkerFiles;
    }

    public String getAdditionalFlags() {
        return additionalFlags;
    }

    public void setAdditionalFlags(String additionalFlags) {
        this.additionalFlags = additionalFlags;
    }
}
