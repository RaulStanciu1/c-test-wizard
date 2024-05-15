package com.ctestwizard.model.code.entity;

import java.io.Serializable;

public class CValue implements Cloneable, Serializable {
    public String value;
    public Integer valueStatus; /* -1 = no status 0 = failed 1 = passed */
    public CValue(String value, int valueStatus){
        this.value = value;
        this.valueStatus = valueStatus;
    }
    public void setValueStatus(int valueStatus){
        this.valueStatus = valueStatus;
    }

    @Override
    public CValue clone() {
        try {
            CValue clone = (CValue) super.clone();
            clone.value = this.value;
            clone.valueStatus = this.valueStatus;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
