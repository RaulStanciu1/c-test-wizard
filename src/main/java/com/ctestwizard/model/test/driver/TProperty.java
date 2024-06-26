package com.ctestwizard.model.test.driver;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity used to store properties of a project
 */
public class TProperty implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;
    private String property;
    private String value;

    /**
     * Constructor for the property
     * @param property The property
     * @param value The value
     */
    public TProperty(String property, String value){
        this.property = property;
        this.value = value;
    }

    /**
     * Get the property
     * @return The property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Get the value
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     *  Set the property
     * @param property The property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Set the value
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
