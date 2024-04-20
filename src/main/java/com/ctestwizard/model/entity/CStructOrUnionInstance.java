package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CStructOrUnionInstance implements CElement {
    private CStructOrUnion structType;
    private String name;
    private int pointers;
    public List<String> values = new ArrayList<>();

    public CStructOrUnionInstance(CStructOrUnion type, String instanceStr) {
        this.structType = type.clone();
        this.pointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        this.name = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
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
            clone.values.addAll(this.values);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
