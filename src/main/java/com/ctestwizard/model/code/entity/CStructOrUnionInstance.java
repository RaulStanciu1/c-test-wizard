package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents an instance of a struct or union in C
 */
public class CStructOrUnionInstance implements CElement , Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private CStructOrUnion structType;
    private String name;
    private Integer pointers;
    public List<CValue> values = new ArrayList<>();

    /**
     * Constructor for the struct or union instance
     * @param type The struct or union type
     * @param instanceStr The instance string
     */
    public CStructOrUnionInstance(CStructOrUnion type, String instanceStr) {
        this.structType = type.clone();
        this.pointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        this.name = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    /**
     * Constructor for the struct or union instance
     * @param type The struct or union type
     * @param name The name of the instance
     * @param pointers The number of pointers the instance has
     */
    public CStructOrUnionInstance(CStructOrUnion type, String name, int pointers){
        this.structType = type.clone();
        this.name = name;
        this.pointers = pointers;
    }

    /**
     * Get the type of the struct or union instance
     * @return The type of the struct or union instance
     */
    public String getType() {
        return structType.getName()+"*".repeat(pointers);
    }

    /**
     * Get the struct or union type
     * @return The struct or union type
     */
    public CStructOrUnion getStructType() {
        return structType;
    }

    /**
     * Get the name of the struct or union instance
     * @return The name of the struct or union instance
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the struct or union instance
     * @param name The name of the struct or union instance
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the pointers of the struct or union instance
     * @return The pointers of the struct or union instance
     */
    public int getPointers() {
        return pointers;
    }

    /**
     * Clone the struct or union instance
     * @return The cloned struct or union instance
     */
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
