package com.ctestwizard.model.entity;

public record CTypedef(String initialType, String typedefName) {
    public String toString(){
        return initialType+":"+typedefName;
    }
}
