package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.CElement;

import java.io.Serializable;
import java.util.List;

public class TResults implements Serializable {
    private final CElement output;
    private final List<CElement> globalOutputs;
    private Boolean resultsPassed;
    private Integer testSteps;
    public TResults(CElement output, List<CElement> globalOutputs, Integer testSteps){
        this.output = output;
        this.globalOutputs = globalOutputs;
        resultsPassed = false;
        this.testSteps = testSteps;
    }

    public CElement getOutput() {
        return output;
    }

    public List<CElement> getGlobalOutputs() {
        return globalOutputs;
    }

    public Boolean getResultsPassed() {
        return resultsPassed;
    }
    public void setResultsPassed(Boolean resultsPassed) {
        this.resultsPassed = resultsPassed;
    }

    public Integer getTestSteps() {
        return testSteps;
    }
}
