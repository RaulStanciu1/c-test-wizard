package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a struct or a union in C
 */
public class CStructOrUnion implements CElement, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private List<CElement> members;

    /**
     * Constructor for the struct or union
     * @param name The name of the struct or union
     * @param members The members of the struct or union
     */
    public CStructOrUnion(String name, List<CElement> members){
        this.name= name;
        this.members = members;
    }

    /**
     * Get the name of the struct or union
     * @return The name of the struct or union
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get the type of the struct or union
     * @return The type of the struct or union
     */
    @Override
    public String getType() {
        return name;
    }

    /**
     * Get the members of the struct or union
     * @return The members of the struct or union
     */
    public List<CElement> getMembers(){
        return this.members;
    }

    /**
     * Set the members of the struct or union
     * @param member the members of the struct or union
     * @param index  the index of the member
     */
    public void setMember(CElement member, int index){
        this.members.set(index,member);
    }

    /**
     * Set the name of the struct or union
     * @param name The name of the struct or union
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Clone the struct or union
     * @return The cloned struct or union
     */
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
