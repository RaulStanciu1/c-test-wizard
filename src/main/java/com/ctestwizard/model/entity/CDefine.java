package com.ctestwizard.model.entity;

public record CDefine(String name, String value) {
    public String toString() {
        return name + " " + value;
    }
}
