package com.ctestwizard.model.test.entity;

import com.ctestwizard.model.code.entity.CElement;


/**
 * Stub entity used for the root of the tree table view
 */
public class TRoot implements CElement {
    private String name;

    /**
     * Constructor for the root entity
     * @param name The name of the root entity
     */
    public TRoot(String name){
        this.name = name;
    }

    /**
     * Due to the fact that CElement implements Cloneable, this method is required
     * @return A clone of the root entity
     */
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

    /**
     * Set the name of the root entity
     * @param name The name of the root entity
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the root entity
     * @return The name of the root entity
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Stub method because CElement has getType as a method that needs implementation
     * @return  An empty string
     */
    @Override
    public String getType() {
        return "";
    }
}
