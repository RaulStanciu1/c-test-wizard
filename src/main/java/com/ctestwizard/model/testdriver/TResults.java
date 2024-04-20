package com.ctestwizard.model.testdriver;

import com.ctestwizard.model.entity.CElement;

import java.util.List;

public class TResults {
    private final CElement output;
    private final List<CElement> globalOutputs;
    public TResults(CElement output, List<CElement> globalOutputs){
        this.output = output;
        this.globalOutputs = globalOutputs;
    }

    public CElement getOutput() {
        return output;
    }

    public List<CElement> getGlobalOutputs() {
        return globalOutputs;
    }
}
