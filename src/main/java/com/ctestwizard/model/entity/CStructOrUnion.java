package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;

public class CStructOrUnion implements CElement{
    private String name;
    private List<CElement> members;
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
    public void setMember(CElement member, int index){
        this.members.set(index,member);
    }

    public String toString(){
        StringBuilder str = new StringBuilder(this.name+"{\n");
        for(CElement el:members){
            str.append("\t").append(el).append("\n");
        }
        str.append("}");
        return str.toString();
    }

    @Override
    public CStructOrUnion clone() throws CloneNotSupportedException {
        CStructOrUnion clone = (CStructOrUnion) super.clone();
        clone.members = new ArrayList<>(this.members.size());
        for(CElement member: this.members){
            clone.members.add(member.clone());
        }
        clone.name = this.name;
        return clone;
    }
}
