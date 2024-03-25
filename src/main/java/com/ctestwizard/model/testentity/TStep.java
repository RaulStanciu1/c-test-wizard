package com.ctestwizard.model.testentity;

import com.ctestwizard.model.entity.CElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TStep {
    private final TCase parent;
    List<CElement> parameters;
    List<CElement> inputs;
    List<CElement> outputs;
    CElement out;
    public TStep(TCase parent){
        this.parent = parent;
    }
    public static TStep newTStep(TCase parent) throws CloneNotSupportedException {
        TStep newTStep = new TStep(parent);
        TInterface tInterface = parent.getParent().getTestInterface();
        Map<CElement,TPassing> globals = tInterface.getGlobals();
        Map<CElement,TPassing> userGlobals = tInterface.getUserGlobals();
        List<CElement> parameters = new ArrayList<>();
        CElement out = tInterface.getOutput().clone();;
        List<CElement> inputs = new ArrayList<>();
        List<CElement> outputs = new ArrayList<>();
        for(CElement parameter : tInterface.getParameters()){
            parameters.add(parameter.clone());
        }
        for(CElement userGlobal : userGlobals.keySet()){
            if(userGlobals.get(userGlobal) == TPassing.IN || userGlobals.get(userGlobal) == TPassing.INOUT){
                inputs.add(userGlobal.clone());
            }
            if(userGlobals.get(userGlobal) == TPassing.OUT || userGlobals.get(userGlobal) == TPassing.INOUT){
                outputs.add(userGlobal.clone());
            }
        }
        for(CElement global : globals.keySet()){
            if(globals.get(global) == TPassing.IN || globals.get(global) == TPassing.INOUT){
                inputs.add(global.clone());
            }
            if(globals.get(global) == TPassing.OUT || globals.get(global) == TPassing.INOUT){
                outputs.add(global.clone());
            }
        }
        newTStep.setInputs(inputs);
        newTStep.setOut(out);
        newTStep.setOutputs(outputs);
        newTStep.setParameters(parameters);
        return newTStep;
    }

    public List<CElement> getParameters() {
        return parameters;
    }

    public void setParameters(List<CElement> parameters) {
        this.parameters = parameters;
    }

    public List<CElement> getInputs() {
        return inputs;
    }

    public void setInputs(List<CElement> inputs) {
        this.inputs = inputs;
    }

    public List<CElement> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<CElement> outputs) {
        this.outputs = outputs;
    }

    public CElement getOut() {
        return out;
    }

    public void setOut(CElement out) {
        this.out = out;
    }
}
