package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;

public class CFunction implements CElement {
    private final CStorageClass storageClassSpecifier;
    private boolean isInline;
    private CElement type;
    private String name;
    private List<CElement> parameters;
    public CFunction(CStorageClass storageClassSpecifier, boolean isInline, CElement type, String name,
                     List<CElement> parameters){
        this.storageClassSpecifier = storageClassSpecifier;
        this.isInline = isInline;
        this.type = type;
        this.name = name.strip();
        this.parameters = parameters;
    }
    public void setParameters(List<CElement> parameters){
        this.parameters = parameters;
    }
    public String toString() {
        StringBuilder str = new StringBuilder(this.type + " "+(isInline ? "inline" : "") + " " + name);
        str.append("(");
        for (CElement element : parameters) {
            str.append(element).append(",");
        }
        if (str.charAt(str.length() - 1) == ',') {
            str.deleteCharAt(str.length() - 1);
        }
        str.append(")");
        return str.toString();
    }

    public CStorageClass getStorageClassSpecifier() {
        return storageClassSpecifier;
    }

    public boolean isInline() {
        return isInline;
    }

    public CElement getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<CElement> getParameters() {
        return parameters;
    }
    public void setType(CElement type){
        this.type = type;
    }

    @Override
    public CFunction clone() throws CloneNotSupportedException {
        CFunction cFunction = (CFunction) super.clone();
        cFunction.name = this.name;
        cFunction.type = this.getType().clone();
        cFunction.parameters = new ArrayList<>(this.parameters.size());
        for(CElement parameter : this.parameters){
            cFunction.parameters.add(parameter.clone());
        }
        cFunction.isInline = this.isInline;
        return cFunction;
    }
}
