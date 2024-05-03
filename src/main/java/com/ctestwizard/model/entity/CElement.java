package com.ctestwizard.model.entity;

import com.ctestwizard.model.testentity.TPassing;
import javafx.beans.value.ObservableValue;

import java.io.Serializable;

public interface CElement extends Cloneable, Serializable {
    CElement clone();
    void setName(String name);
    String getName();
    String getType();
}
