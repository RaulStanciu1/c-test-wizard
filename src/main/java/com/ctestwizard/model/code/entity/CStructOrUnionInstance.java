package com.ctestwizard.model.code.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CStructOrUnionInstance implements CElement , Serializable {
    private CStructOrUnion structType;
    private String name;
    private Integer pointers;
    public List<CValue> values = new ArrayList<>();

    public CStructOrUnionInstance(CStructOrUnion type, String instanceStr) {
        this.structType = type.clone();
        this.pointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        this.name = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
    public CStructOrUnionInstance(CStructOrUnion type, String name, int pointers){
        this.structType = type.clone();
        this.name = name;
        this.pointers = pointers;
    }

    public String getType() {
        return structType.getName()+"*".repeat(pointers);
    }

    public CStructOrUnion getStructType() {
        return structType;
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


    @Override
    public CStructOrUnionInstance clone() {
        try {
            CStructOrUnionInstance clone = (CStructOrUnionInstance) super.clone();
            clone.name = this.name;
            clone.pointers = this.pointers;
            clone.structType = this.structType.clone();
            clone.values = new ArrayList<>();
            for (int i = 0; i < this.values.size(); i++) {
                clone.values.add(this.values.get(i).clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
