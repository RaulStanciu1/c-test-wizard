package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.entity.CElement;
import com.ctestwizard.model.code.entity.CFunction;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity used to store all the important information about the tests related to a test object
 */
public class TInterface implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
    private final TObject parent;
    private List<CFunction> externalFunc;
    private List<CFunction> localFunc;
    private Map<CElement,TPassing> globals;
    private final Map<CElement,TPassing> userGlobals;
    private List<CElement> parameters;
    private CElement output;
    private final Map<CFunction,String> stubCode;

    /**
     * Constructor for the interface
     * @param parent The parent object
     * @param externalFunctions The external functions
     * @param localFunctions The local functions
     * @param globals The global variables
     * @param parameters The parameters
     * @param output The output
     */
    public TInterface(TObject parent,List<CFunction> externalFunctions,
                      List<CFunction> localFunctions, List<CElement> globals,
                      List<CElement> parameters, CElement output){
        this.parent = parent;
        this.externalFunc = externalFunctions;
        this.localFunc = localFunctions;
        this.parameters = parameters;
        this.output = output;
        this.stubCode = new HashMap<>();
        this.globals = new HashMap<>();
        this.userGlobals = new HashMap<>();
        for(CElement global : globals){
            this.globals.put(global,TPassing.NONE);
        }
    }

    /**
     * Get the external functions
     * @return The external functions
     */
    public List<CFunction> getExternalFunctions() {
        return externalFunc;
    }

    /**
     * Set the external functions
     * @param externalFunctions The external functions
     */
    public void setExternalFunctions(List<CFunction> externalFunctions) {
        this.externalFunc = externalFunctions;
    }

    /**
     * Get the local functions
     * @return The local functions
     */
    public List<CFunction> getLocalFunctions() {
        return localFunc;
    }

    /**
     * Set the local functions
     * @param localFunctions The local functions
     */
    public void setLocalFunctions(List<CFunction> localFunctions) {
        this.localFunc = localFunctions;
    }

    /**
     * Get the global variables
     * @return The global variables
     */
    public Map<CElement, TPassing> getGlobals() {
        return globals;
    }

    /**
     * Set the global variables
     * @param globals The global variables
     */
    public void setGlobals(Map<CElement, TPassing> globals) {
        this.globals = globals;
    }

    /**
     * Get the parameters
     * @return The parameters
     */
    public List<CElement> getParameters() {
        return parameters;
    }

    /**
     * Set the parameters
     * @param parameters The parameters
     */
    public void setParameters(List<CElement> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the output
     * @return The output
     */
    public CElement getOutput() {
        return output;
    }

    /**
     * Set the output
     * @param output The output
     */
    public void setOutput(CElement output) {
        this.output = output;
    }

    /**
     * Get the stub code
     * @return The stub code
     */
    public Map<CFunction, String> getStubCode() {
        return stubCode;
    }

    /**
     * Get the user globals
     * @return The user globals
     */
    public Map<CElement, TPassing> getUserGlobals() {
        return userGlobals;
    }

    /**
     * Get the parent object
     * @return The parent object
     */
    public TObject getParent() {
        return parent;
    }
}
