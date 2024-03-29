package com.ctestwizard.model.entity;

public enum CStorageClass {
    TYPEDEF,EXTERN,STATIC,THREAD_LOCAL,AUTO,REGISTER,NONE;

    public static CStorageClass strToStorageClass(String str){
        return switch(str){
            case "typedef" -> TYPEDEF;
            case "extern" -> EXTERN;
            case "static" -> STATIC;
            case "_Thread_local" -> THREAD_LOCAL;
            case "auto" -> AUTO;
            case "register" -> REGISTER;
            default -> NONE;
        };
    }
}
