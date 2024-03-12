package com.ctestwizard.model.entity;

public enum CStorageClass {
    TYPEDEF,EXTERN,STATIC,THREAD_LOCAL,AUTO,REGISTER,NONE;
    public static String storageClassToStr(CStorageClass storageClass){
        return switch(storageClass){
            case TYPEDEF -> "typedef";
            case EXTERN -> "extern";
            case STATIC -> "static";
            case THREAD_LOCAL -> "_Thread_local";
            case AUTO -> "auto";
            case REGISTER -> "register";
            default -> "";
        };
    }
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
