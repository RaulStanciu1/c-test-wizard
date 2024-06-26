package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a function in C
 */
public class CFunction implements CElement , Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final CStorageClass storageClassSpecifier;
    private Boolean isInline;
    private CElement retType;
    private final String strType;
    private String name;
    private List<CElement> parameters;
    private final String functionPrototype;
    private final String functionSignature;

    /**
     * Constructor for the function
     * @param storageClassSpecifier The storage class specifier of the function
     * @param isInline Whether the function is inline
     * @param type The return type of the function
     * @param name The name of the function
     * @param parameters The parameters of the function
     */
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

    /**
     * Set the parameters of the function
     * @param parameters The parameters of the function
     */
    public void setParameters(List<CElement> parameters){
        this.parameters = parameters;
    }

    /**
     * Get the storage class specifier of the function
     * @return The storage class specifier of the function
     */
    public CStorageClass getStorageClassSpecifier() {
        return storageClassSpecifier;
    }

    /**
     * Get whether the function is inline
     * @return Whether the function is inline
     */
    public boolean isInline() {
        return isInline;
    }

    /**
     * Set whether the function is inline
     * @return Whether the function is inline
     */
    public CElement getRetType() {
        return retType;
    }

    /**
     * Get the name of the function
     * @return The name of the function
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the function
     * @return The type of the function
     */
    @Override
    public String getType() {
        return retType.getType();
    }

    /**
     * Set the name of the function
     * @param name The name of the function
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the parameters of the function
     * @return The parameters of the function
     */
    public List<CElement> getParameters() {
        return parameters;
    }

    /**
     * Set the return type of the function
     * @param type The return type of the function
     */
    public void setRetType(CElement type){
        this.retType = type;
    }

    /**
     * Get the return type of the function as string
     * @return The return type of the function as string
     */
    public String getStrType() {
        return strType;
    }

    /**
     * Get the function signature
     * @return The function signature
     */
    public String getFunctionSignature() {
        return functionSignature;
    }

    /**
     * Clone the function
     * @return The cloned function
     */
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
