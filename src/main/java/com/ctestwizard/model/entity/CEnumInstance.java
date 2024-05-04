package com.ctestwizard.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CEnumInstance implements CElement, Serializable {
    private CEnum enumType;
    private String name;
    private Integer pointers;
    public List<CValue> values = new ArrayList<>();
    public CEnumInstance(CEnum type, String instanceStr){
        this.enumType = type;
        this.pointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        this.name = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
    public CEnumInstance(CEnum type,String name, int pointers){
        this.enumType = type.clone();
        this.name = name;
        this.pointers = pointers;
    }
    @Override
    public CEnumInstance clone(){
        try{
            CEnumInstance clone = (CEnumInstance) super.clone();
            clone.name = this.name;
            clone.enumType = this.enumType.clone();
            clone.pointers = this.pointers;
            clone.values = new ArrayList<>();
            for(int i = 0; i < this.values.size(); i++){
                clone.values.add(this.values.get(i).clone());
            }
            return clone;
        }
        catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getType() {
        return enumType.getName();
    }
    public CEnum getEnumType() {
        return enumType;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getPointers() {
        return pointers;
    }
}
