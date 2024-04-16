package com.ctestwizard.model.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CVariable implements CElement {
    private String type;
    private String name;
    private int pointers;

    public List<String> values = new ArrayList<>();

    public CVariable(String type, String name){
        this.type = type;
        this.pointers = (int) name.chars().filter(c -> c == '*').count();
        this.name = name.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    @Override
    public CVariable clone(){
        try{
            CVariable clone = (CVariable) super.clone();
            clone.type = this.type;
            clone.name = this.name;
            clone.values = new ArrayList<>();
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
