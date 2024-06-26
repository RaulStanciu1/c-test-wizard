package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Record that represents a define in C
 * @param name The name of the define
 * @param value The value of the define
 */
public record CDefine(String name, String value) implements Serializable {
    @Serial
    private static final long serialVersionUID = 9L;
    public String toString() {
        return name + " " + value;
    }
}
