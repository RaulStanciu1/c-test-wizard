package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Class that represents the value of an element in the tests
 */
public class CValue implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String value;
    public transient int valueStatus; /* 0 = no status -1 = failed 1 = passed */

    /**
     * Constructor for the value
     * @param value The value
     * @param valueStatus The status of the value
     */
    public CValue(String value, int valueStatus){
        this.value = value;
        this.valueStatus = valueStatus;
    }

    /**
     * Set the value status
     * @param valueStatus The status of the value
     */
    public void setValueStatus(int valueStatus){
        this.valueStatus = valueStatus;
    }

    /**
     * Clone the value
     * @return The cloned value
     */
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
