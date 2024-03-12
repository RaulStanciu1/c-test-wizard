package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;

public class CStructOrUnion implements CElement{
    private String name;
    private final List<CElement> members;
    public CStructOrUnion(CStructOrUnion obj){
        this.name = obj.name;
        this.members = obj.members;
    }
    public CStructOrUnion(String name, List<CElement> members){
        this.name= name;
        this.members = members;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public List<CElement> getMembers(){
        return this.members;
    }

    public String toString(){
        StringBuilder str = new StringBuilder(this.name+"{\n");
        for(CElement el:members){
            str.append("\t").append(el).append("\n");
        }
        str.append("}");
        return str.toString();
    }

}
