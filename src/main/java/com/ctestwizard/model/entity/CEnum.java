package com.ctestwizard.model.entity;

import java.util.List;

public class CEnum implements CElement{
    private String name;
    private List<String> members;
    public CEnum(String name, List<String> members){
        this.name = name;
        this.members = members;
    }

    public CEnum(CEnum obj){
        this.name = obj.name;
        this.members = obj.members;
    }

    public String getName() {
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

    public String toString(){
        StringBuilder str = new StringBuilder(name+"{\n");
        for(String member:members){
            str.append(member).append("\n");
        }
        str.append('}');
        return str.toString();
    }
}
