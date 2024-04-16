package com.ctestwizard.model.entity;

import com.ctestwizard.model.testentity.TPassing;
import javafx.beans.value.ObservableValue;

public interface CElement extends Cloneable {
    CElement clone();
    void setName(String name);
    String getName();
    String getType();
}
