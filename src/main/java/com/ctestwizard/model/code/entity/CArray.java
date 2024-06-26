package com.ctestwizard.model.code.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing an array in C
 */
public class CArray implements CElement, Serializable {
    @Serial
    private static final long serialVersionUID = 9L;
    private CElement type;
    private List<Integer> arraySpecifiers;
    private List<CElement> arrayMembers;

    /**
     * Constructor for the array
     * @param type The type of the array
     */
    public CArray(CElement type){
        this.type = type.clone();
        this.arraySpecifiers = new ArrayList<>();
        // Regular expression pattern to match dimensions
        Pattern pattern = Pattern.compile("\\[\\s*(\\d+)\\s*]");
        Matcher matcher = pattern.matcher(this.type.getName());

        while (matcher.find()) {
            arraySpecifiers.add(Integer.parseInt(matcher.group(1)));
        }
        this.arrayMembers = new ArrayList<>();
        int totalElements = arraySpecifiers.stream().reduce(1, (a, b) -> a * b);
        this.type.setName(this.type.getName().replaceAll("\\[\\s*(\\d+)\\s*]", ""));
        switch(arraySpecifiers.size()){
            case 1:
                for (int i = 0; i < totalElements; i++) {
                    CElement newElement = this.type.clone();
                    newElement.setName(this.type.getName() + "[" + i + "]");
                    arrayMembers.add(newElement);
                }
                break;
            case 2:
                for (int i = 0; i < totalElements; i++) {
                    CElement newElement = this.type.clone();
                    newElement.setName(this.type.getName() + "[" + i / arraySpecifiers.get(1) + "][" + i % arraySpecifiers.get(1) + "]");
                    arrayMembers.add(newElement);
                }
                break;
            case 3:
                for (int i = 0; i < totalElements; i++) {
                    CElement newElement = this.type.clone();
                    newElement.setName(this.type.getName() + "[" + i / (arraySpecifiers.get(1) * arraySpecifiers.get(2)) + "][" + (i / arraySpecifiers.get(2)) % arraySpecifiers.get(1) + "][" + i % arraySpecifiers.get(2) + "]");
                    arrayMembers.add(newElement);
                }
                break;
            default:
                throw new IllegalArgumentException("Only 1D, 2D and 3D arrays are supported");
        }
    }

    /**
     * Set the name of the array
     * @param name The name of the array
     */
    @Override
    public void setName(String name) {
        this.type.setName(name);
    }

    /**
     * Get the name of the array
     * @return the name of the array
     */
    public String getName(){
        StringBuilder sb = new StringBuilder(this.type.getName());
        for(int i : arraySpecifiers){
            sb.append("[").append(i).append("]");
        }
        return sb.toString();
    }

    /**
     * Get the type of the array
     * @return The type of the array
     */
    public String getType(){
        return this.type.getType();
    }

    /**
     * Get the array members
     * @return  The array members
     */
    public List<CElement> getArrayMembers() {
        return arrayMembers;
    }

    /**
     * Clone the array
     * @return The cloned array
     */
    @Override
    public CArray clone(){
        try{
            CArray clone = (CArray) super.clone();
            clone.type = this.type.clone();
            clone.arraySpecifiers = new ArrayList<>(this.arraySpecifiers.size());
            clone.arraySpecifiers.addAll(this.arraySpecifiers);
            clone.arrayMembers = new ArrayList<>(this.arrayMembers.size());
            for(CElement member : this.arrayMembers){
                clone.arrayMembers.add(member.clone());
            }
            return clone;
        }
        catch(CloneNotSupportedException e){
            throw new AssertionError();
        }
    }

    /**
     * Set the type of the array
     * @param type The type of the array
     */
    public void setType(CElement type) {
        this.type = type;
    }

    /**
     * Set the array members
     * @param arrayMembers The array members
     */
    public void setArrayMembers(List<CElement> arrayMembers) {
        this.arrayMembers = arrayMembers;
    }
}
