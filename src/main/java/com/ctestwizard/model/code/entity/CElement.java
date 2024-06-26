package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Interface representing an element in C, an element can consist of an: variable, enum, struct, array, etc.
 */
public interface CElement extends Cloneable, Serializable {
    @Serial
    long serialVersionUID = 40L;
    CElement clone();
    void setName(String name);
    String getName();
    String getType();
}
