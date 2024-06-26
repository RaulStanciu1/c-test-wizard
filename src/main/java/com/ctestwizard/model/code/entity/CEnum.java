package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing an enum in C
 */
public class CEnum implements CElement, Serializable {
    @Serial
    private static final long serialVersionUID = 109L;
    private String name;
    private List<String> members;
    private final Map<String, Integer> symbolMap;

    /**
     * Constructor for the enum
     * @param name The name of the enum
     * @param members The members of the enum
     * @param symbolMap The symbol map of the enum
     */
    public CEnum(String name, List<String> members, Map<String, Integer> symbolMap){
        this.name = name;
        this.members = members;
        this.symbolMap = symbolMap;
    }

    /**
     * Get the name of the enum
     * @return The name of the enum
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the enum
     * @return The type of the enum
     */
    @Override
    public String getType() {
        return name;
    }

    /**
     * Get the members of the enum
     * @return The members of the enum
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * Set the name of the enum
     * @param name The name of the enum
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Get the symbol map of the enum
     * @return The symbol map of the enum
     */
    public Map<String, Integer> getSymbolMap() {
        return symbolMap;
    }

    /**
     * Clone the enum
     * @return The cloned enum
     */
    @Override
    public CEnum clone() {
        try {
            CEnum clone = (CEnum) super.clone();
            clone.members = new ArrayList<>(this.members.size());
            clone.members.addAll(this.members);
            clone.name = this.name;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
