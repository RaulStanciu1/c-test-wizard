package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;

public class CEnum implements CElement{
    private String name;
    private List<String> members;
    public CEnum(String name, List<String> members){
        this.name = name;
        this.members = members;
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
    public void setMembers(List<String>members){
        this.members = members;
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
