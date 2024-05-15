package com.ctestwizard.model.code.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CVariable implements CElement, Serializable {
    private String type;
    private String name;
    private Integer pointers;

    public List<CValue> values;

    public CVariable(String type, String name){
        this.type = type;
        this.pointers = (int) name.chars().filter(c -> c == '*').count();
        this.name = name.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
        this.values = new ArrayList<>();
    }

    public CVariable(String type, String name, int pointers){
        this.type = type;
        this.name = name;
        this.pointers = pointers;
        this.values = new ArrayList<>();
    }

    @Override
    public CVariable clone(){
        try{
            CVariable clone = (CVariable) super.clone();
            clone.type = this.type;
            clone.name = this.name;
            clone.values = new ArrayList<>();
            for(int i = 0; i < this.values.size(); i++){
                clone.values.add(this.values.get(i).clone());
            }
            return clone;
        }catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type + "*".repeat(pointers);
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPointers() {
        return pointers;
    }

    public void setPointers(int pointers) {
        this.pointers = pointers;
    }
}
