package com.ctestwizard.model.entity;

import java.util.List;

public record CFunction(CStorageClass storageClassSpecifier, boolean isInline, String type, String name,
                        List<CElement> parameters) implements CElement {

    public String toString() {
        StringBuilder str = new StringBuilder(CStorageClass.storageClassToStr(storageClassSpecifier) +
                " " + (isInline ? "inline" : "") + " " + type + " " + name);
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
}
