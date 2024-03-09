package com.ctestwizard.model.entity;

import java.util.List;

public class CEnum implements CElement{
    private final String name;
    private final List<String> members;
    public CEnum(String name, List<String> members){
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public String toString(){
        StringBuilder str = new StringBuilder(name+"{\n");
        for(String member:members){
            str.append(member).append("\n");
        }
        str.append('}');
        return str.toString();
    }
}
