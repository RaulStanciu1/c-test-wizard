package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CEnumInstance implements CElement{
    private CEnum type;
    private String name;
    private List<Integer> arraySpecifiers;
    private int numberOfPointers;
    private String value;
    public CEnumInstance(CEnum type, String instanceStr){
        this.value = "";
        String tmpName;
        this.type = type;
        this.numberOfPointers = (int) instanceStr.chars().filter(c -> c == '*').count();

        String modifiedString = instanceStr.chars()
                .filter(c -> c != '*')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());

        // Define the pattern for square brackets with numbers inside
        Pattern pattern = Pattern.compile("\\[(\\d+)]");

        // Use a Matcher to find and extract the numbers in square brackets
        Matcher matcher = pattern.matcher(modifiedString);
        List<Integer> extractedNumbers = matcher.results()
                .map(m -> Integer.parseInt(m.group(1)))
                .toList();

        // Remove square brackets with numbers from the input string
        tmpName = matcher.replaceAll("");
        this.name = tmpName.replaceAll(type.getName(),"");

        this.arraySpecifiers = extractedNumbers;
    }

    public String toString(){
        return this.type + " "+"*".repeat(numberOfPointers)+" "+name;
    }


    @Override
    public CEnumInstance clone() throws CloneNotSupportedException{
        CEnumInstance clone = (CEnumInstance)super.clone();
        clone.name = this.name;
        clone.type = this.type.clone();
        clone.numberOfPointers = this.numberOfPointers;
        clone.arraySpecifiers = new ArrayList<>(this.arraySpecifiers.size());
        clone.arraySpecifiers.addAll(this.arraySpecifiers);
        return clone;
    }

    public CEnum getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getArraySpecifiers() {
        return arraySpecifiers;
    }

    public int getNumberOfPointers() {
        return numberOfPointers;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
