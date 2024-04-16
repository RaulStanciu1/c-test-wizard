package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;

public class CFunction implements CElement {
    private final CStorageClass storageClassSpecifier;
    private boolean isInline;
    private CElement retType;
    private final String strType;
    private String name;
    private List<CElement> parameters;
    public CFunction(CStorageClass storageClassSpecifier, boolean isInline, String type, String name,
                     List<CElement> parameters){
        this.storageClassSpecifier = storageClassSpecifier;
        this.isInline = isInline;
        this.strType = type;
        this.name = name.strip();
        this.parameters = parameters;
    }
    public void setParameters(List<CElement> parameters){
        this.parameters = parameters;
    }

    public CStorageClass getStorageClassSpecifier() {
        return storageClassSpecifier;
    }

    public boolean isInline() {
        return isInline;
    }

    public CElement getRetType() {
        return retType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return retType.getType();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<CElement> getParameters() {
        return parameters;
    }
    public void setRetType(CElement type){
        this.retType = type;
    }

    public String getStrType() {
        return strType;
    }

    @Override
    public CFunction clone(){
        try {
            CFunction cFunction = (CFunction) super.clone();
            cFunction.name = this.name;
            cFunction.retType = this.getRetType().clone();
            cFunction.parameters = new ArrayList<>(this.parameters.size());
            for (CElement parameter : this.parameters) {
                cFunction.parameters.add(parameter.clone());
            }
            cFunction.isInline = this.isInline;
            return cFunction;
        }catch(CloneNotSupportedException e){
            throw new AssertionError();
        }
    }
}
