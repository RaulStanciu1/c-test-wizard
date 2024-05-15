package com.ctestwizard.model.code.entity;

import java.io.Serializable;

public record CDefine(String name, String value) implements Serializable {
    public String toString() {
        return name + " " + value;
    }
}
