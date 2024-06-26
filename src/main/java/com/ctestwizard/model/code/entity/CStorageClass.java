package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Enum representing the storage class specifier in C(NOT USED)
 */
public enum CStorageClass implements Serializable {
    TYPEDEF,EXTERN,STATIC,THREAD_LOCAL,AUTO,REGISTER,NONE;
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Convert a string to a storage class
     * @param str The string to convert
     * @return The storage class
     */
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
