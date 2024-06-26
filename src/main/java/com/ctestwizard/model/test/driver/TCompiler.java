package com.ctestwizard.model.test.driver;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity used to store the compiler information

 */
public class TCompiler implements Serializable {
    @Serial
    private static final long serialVersionUID = 9L;
    private String compiler;
    private String preprocessFlag;
    private String compileFlag;
    private String outputFlag;
    private String linkerFlag;
    private String includeFlag;
    private final List<String> objectFiles;
    private List<String> includeDirectories;
    private List<String> linkerFiles;
    private String additionalFlags;

    /**
     * Constructor for the compiler
     * @param compiler The compiler
     * @param preprocessFlag The preprocess flag
     * @param compileFlag The compile flag
     * @param outputFlag The output flag
     * @param linkerFlag The linker flag
     * @param includeFlag The include flag
     * @param objectFiles The object files
     * @param includeDirectories The include directories
     * @param linkerFiles The linker files
     * @param additionalFlags The additional flags
     */
    public TCompiler(String compiler, String preprocessFlag, String compileFlag, String outputFlag, String linkerFlag, String includeFlag, List<String> objectFiles
            ,List<String> includeDirectories, List<String> linkerFiles, String additionalFlags){
        this.compiler = compiler;
        this.preprocessFlag = preprocessFlag;
        this.compileFlag = compileFlag;
        this.outputFlag = outputFlag;
        this.linkerFlag = linkerFlag;
        this.includeFlag = includeFlag;
        this.includeDirectories = includeDirectories;
        this.linkerFiles = linkerFiles;
        this.additionalFlags = additionalFlags;
        this.objectFiles = objectFiles;
    }

    /**
     * Get the compiler
     * @return The compiler
     */
    public String getCompiler() {
        return compiler;
    }

    /**
     * Set the compiler
     * @param compiler The compiler
     */
    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    /**
     * Get the preprocess flag
     * @return The preprocess flag
     */
    public String getPreprocessFlag() {
        return preprocessFlag;
    }

    /**
     * Set the preprocess flag
     * @param preprocessFlag The preprocess flag
     */
    public void setPreprocessFlag(String preprocessFlag) {
        this.preprocessFlag = preprocessFlag;
    }

    /**
     * Get the compile flag
     * @return The compile flag
     */
    public String getCompileFlag() {
        return compileFlag;
    }

    /**
     * Set the compile flag
     * @param compileFlag The compile flag
     */
    public void setCompileFlag(String compileFlag) {
        this.compileFlag = compileFlag;
    }

    /**
     * Get the output flag
     * @return The output flag
     */
    public String getOutputFlag() {
        return outputFlag;
    }

    /**
     * Set the output flag
     * @param outputFlag The output flag
     */
    public void setOutputFlag(String outputFlag) {
        this.outputFlag = outputFlag;
    }

    /**
     * Get the linker flag
     * @return The linker flag
     */
    public String getLinkerFlag() {
        return linkerFlag;
    }

    /**
     * Set the linker flag
     * @param linkerFlag The linker flag
     */
    public void setLinkerFlag(String linkerFlag) {
        this.linkerFlag = linkerFlag;
    }

    /**
     * Get the include flag
     * @return The include flag
     */
    public String getIncludeFlag() {
        return includeFlag;
    }

    /**
     *  Set the include flag
     * @param includeFlag The include flag
     */
    public void setIncludeFlag(String includeFlag) {
        this.includeFlag = includeFlag;
    }

    /**
     * Get the include directories
     * @return The include directories
     */
    public List<String> getIncludeDirectories() {
        return includeDirectories;
    }

    /**
     * Set the include directories
     * @param includeDirectories The include directories
     */
    public void setIncludeDirectories(List<String> includeDirectories) {
        this.includeDirectories = includeDirectories;
    }

    /**
     * Get the linker files
     * @return The linker files
     */
    public List<String> getLinkerFiles() {
        return linkerFiles;
    }

    /**
     * Set the linker files
     * @param linkerFiles The linker files
     */
    public void setLinkerFiles(List<String> linkerFiles) {
        this.linkerFiles = linkerFiles;
    }

    /**
     * Get the additional flags
     * @return The additional flags
     */
    public String getAdditionalFlags() {
        return additionalFlags;
    }

    /**
     * Set the additional flags
     * @param additionalFlags The additional flags
     */
    public void setAdditionalFlags(String additionalFlags) {
        this.additionalFlags = additionalFlags;
    }

    /**
     * Get the object files
     * @return The object files
     */
    public List<String> getObjectFiles() {
        return objectFiles;
    }

}
