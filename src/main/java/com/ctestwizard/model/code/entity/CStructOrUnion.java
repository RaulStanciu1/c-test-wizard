package com.ctestwizard.model.code.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CStructOrUnion implements CElement, Serializable {
    private String name;
    private List<CElement> members;
    public CStructOrUnion(String name, List<CElement> members){
        this.name= name;
        this.members = members;
    }
    public String getName(){
        return this.name;
    }

    @Override
    public String getType() {
        return name;
    }

    public List<CElement> getMembers(){
        return this.members;
    }
    public void setMember(CElement member, int index){
        this.members.set(index,member);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CStructOrUnion clone(){
        try {
            CStructOrUnion clone = (CStructOrUnion) super.clone();
            clone.members = new ArrayList<>(this.members.size());
            for (CElement member : this.members) {
                clone.members.add(member.clone());
            }
            clone.name = this.name;
            return clone;
        }catch(CloneNotSupportedException e){
            throw new AssertionError();
        }
    }
}
