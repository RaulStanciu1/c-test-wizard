package com.ctestwizard.model.testentity;

import com.ctestwizard.model.entity.CElement;

public class TRoot implements CElement {
    private String name;
    public TRoot(String name){
        this.name = name;
    }
    @Override
    public CElement clone() {
        try {
            TRoot clone = (TRoot) super.clone();
            clone.name = this.name;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "";
    }
}
