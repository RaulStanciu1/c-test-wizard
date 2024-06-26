package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing an instance of an enum in C
 */
public class CEnumInstance implements CElement, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private CEnum enumType;
    private String name;
    private Integer pointers;
    public List<CValue> values = new ArrayList<>();

    /**
     * Constructor for the enum instance
     * @param type The enum type
     * @param instanceStr The instance string
     */
    public CEnumInstance(CEnum type, String instanceStr){
        this.enumType = type;
        this.pointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        this.name = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    /**
     * Constructor for the enum instance
     * @param type The enum type
     * @param name The name of the instance
     * @param pointers The number of pointers the instance has
     */
    public CEnumInstance(CEnum type,String name, int pointers){
        this.enumType = type.clone();
        this.name = name;
        this.pointers = pointers;
    }

    /**
     * Clone the enum instance
     * @return The cloned enum instance
     */
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

    /**
     * Get the type of the enum instance as string
     * @return The type of the enum instance as string
     */
    public String getType() {
        return enumType.getName();
    }

    /**
     * Get the enum type
     * @return The enum type
     */
    public CEnum getEnumType() {
        return enumType;
    }

    /**
     * Get the name of the enum instance
     * @return The name of the enum instance
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the enum instance
     * @param name The name of the enum instance
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the number of pointers of the enum instance
     * @return The number of pointers of the enum instance
     */
    public int getPointers() {
        return pointers;
    }
}
