package com.ctestwizard.model.testdriver;

public class TProperty {
    private String property;
    private String value;
    public TProperty(String property, String value){
        this.property = property;
        this.value = value;
    }
    public String getProperty() {
        return property;
    }
    public String getValue() {
        return value;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public void setValue(String value) {
        this.value = value;
    }
}