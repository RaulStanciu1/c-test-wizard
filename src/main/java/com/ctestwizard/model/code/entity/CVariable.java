package com.ctestwizard.model.code.entity;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a standard variable in C
 */
public class CVariable implements CElement, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String type;
    private String name;
    private Integer pointers;

    public List<CValue> values;

    /**
     * Constructor for the variable
     * @param type The type of the variable
     * @param name The name of the variable
     */
    public CVariable(String type, String name){
        switch (type) {
            case "unsignedshort" -> type = "unsigned short";
            case "unsignedint" -> type = "unsigned int";
            case "unsignedlong" -> type = "unsigned long";
            case "unsignedchar" -> type = "unsigned char";
            case "signedshort" -> type = "signed short";
            case "signedint" -> type = "signed int";
            case "signedlong" -> type = "signed long";
            case "signedchar" -> type = "signed char";
        }
        this.type = type;
        this.pointers = (int) name.chars().filter(c -> c == '*').count();
        this.name = name.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
        this.values = new ArrayList<>();
    }

    /**
     * Constructor for the variable
     * @param type The type of the variable
     * @param name The name of the variable
     * @param pointers The number of pointers the variable has
     */
    public CVariable(String type, String name, int pointers){
        this.type = type;
        this.name = name;
        this.pointers = pointers;
        this.values = new ArrayList<>();
    }

    /**
     * Clone the variable
     * @return The cloned variable
     */
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

    /**
     * Get the name of the variable
     * @return The name of the variable
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the variable
     * @param name The name of the variable
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the variable
     * @return The type of the variable
     */
    public String getType() {
        return type + "*".repeat(pointers);
    }

    /**
     * Set the type of the variable
     * @param type The type of the variable
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the number of pointers the variable has
     * @return The number of pointers the variable has
     */
    public int getPointers() {
        return pointers;
    }

    /**
     * Set the number of pointers the variable has
     * @param pointers The number of pointers the variable has
     */
    public void setPointers(int pointers) {
        this.pointers = pointers;
    }
}
