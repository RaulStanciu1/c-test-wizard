package com.ctestwizard.model.code.entity;

import java.io.Serializable;

public interface CElement extends Cloneable, Serializable {
    CElement clone();
    void setName(String name);
    String getName();
    String getType();
}
