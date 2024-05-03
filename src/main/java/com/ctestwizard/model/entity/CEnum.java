package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CEnum implements CElement{
    private String name;
    private List<String> members;
    private final Map<String, Integer> symbolMap;
    public CEnum(String name, List<String> members, Map<String, Integer> symbolMap){
        this.name = name;
        this.members = members;
        this.symbolMap = symbolMap;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setName(String name){
        this.name = name;
    }

    public Map<String, Integer> getSymbolMap() {
        return symbolMap;
    }
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
