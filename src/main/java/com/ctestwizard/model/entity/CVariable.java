package com.ctestwizard.model.entity;


public class CVariable implements CElement {
    private CType type;
    private String name;
    private String value;
    public CVariable(CType type, String name){
        this.value = "";
        this.type = type;
        this.name = name;
    }
    public String toString(){
        StringBuilder variable = new StringBuilder(this.getType().getName()+" ");
        variable.append("*".repeat(Math.max(0, this.getType().getNumberOfPointers())));
        if(this.getType().getNumberOfPointers() == 0){
            variable.append(name);
        }
        else{
            variable.append(" ").append(name);
        }

        for(Number num : this.getType().getArraySpecifiers()){
            variable.append('[').append(num.toString()).append(']');
        }
        return variable.toString();
    }

    @Override
    public CVariable clone() throws CloneNotSupportedException {
        CVariable clone = (CVariable) super.clone();
        clone.type = this.type.clone();
        clone.name = this.name;
        return clone;
    }

    public CType getType() {
        return type;
    }

    public void setType(CType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
