package com.ctestwizard.model.code.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFunction implements CElement , Serializable {
    private final CStorageClass storageClassSpecifier;
    private Boolean isInline;
    private CElement retType;
    private final String strType;
    private String name;
    private List<CElement> parameters;
    private final String functionPrototype;
    private final String functionSignature;
    public CFunction(CStorageClass storageClassSpecifier, boolean isInline, String type, String name,
                     List<CElement> parameters){
        this.storageClassSpecifier = storageClassSpecifier;
        this.isInline = isInline;
        this.strType = type;
        this.name = name.strip();
        this.parameters = parameters;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(type).append(" ").append(name).append("(");
        sb2.append(type).append(" ").append(name).append("(");
        for(int i = 0; i < parameters.size(); i++){
            sb.append(parameters.get(i).getType());
            sb2.append(parameters.get(i).getType());
            sb2.append(" ").append(parameters.get(i).getName());
            if(i != parameters.size() - 1){
                sb.append(", ");
                sb2.append(", ");
            }
        }
        sb.append(")");
        sb2.append(")");
        this.functionPrototype = sb.toString();
        this.functionSignature = sb2.toString();
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

    public String getFunctionSignature() {
        return functionSignature;
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

    public String toString(){
        return this.functionPrototype;
    }
}
