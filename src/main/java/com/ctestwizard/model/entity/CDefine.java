package com.ctestwizard.model.entity;

import java.io.Serializable;

public record CDefine(String name, String value) implements Serializable {
    public String toString() {
        return name + " " + value;
    }
}
