package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.entity.CElement;
import com.ctestwizard.model.code.entity.CFunction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TInterface implements Serializable {
    private final TObject parent;
    private List<CFunction> externalFunc;
    private List<CFunction> localFunc;
    private Map<CElement,TPassing> globals;
    private final Map<CElement,TPassing> userGlobals;
    private List<CElement> parameters;
    private CElement output;
    private final Map<CFunction,String> stubCode;
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

    public List<CFunction> getExternalFunctions() {
        return externalFunc;
    }

    public void setExternalFunctions(List<CFunction> externalFunctions) {
        this.externalFunc = externalFunctions;
    }

    public List<CFunction> getLocalFunctions() {
        return localFunc;
    }

    public void setLocalFunctions(List<CFunction> localFunctions) {
        this.localFunc = localFunctions;
    }

    public Map<CElement, TPassing> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<CElement, TPassing> globals) {
        this.globals = globals;
    }

    public List<CElement> getParameters() {
        return parameters;
    }

    public void setParameters(List<CElement> parameters) {
        this.parameters = parameters;
    }

    public CElement getOutput() {
        return output;
    }

    public void setOutput(CElement output) {
        this.output = output;
    }

    public Map<CFunction, String> getStubCode() {
        return stubCode;
    }

    public Map<CElement, TPassing> getUserGlobals() {
        return userGlobals;
    }

    public TObject getParent() {
        return parent;
    }
}
