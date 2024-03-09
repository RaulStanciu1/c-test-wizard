package com.ctestwizard.model.entity;


public record CVariable(CType type, String name) implements CElement {
    public String toString(){
        StringBuilder variable = new StringBuilder(type().getName()+" ");
        variable.append("*".repeat(Math.max(0, type().getNumberOfPointers())));
        variable.append(" ").append(name);
        for(Number num : type().getArraySpecifiers()){
            variable.append('[').append(num.toString()).append(']');
        }
        return variable.toString();
    }
}
