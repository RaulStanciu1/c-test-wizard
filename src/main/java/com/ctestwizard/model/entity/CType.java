package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CType implements CElement{
    private String name;
    private int numberOfPointers;
    private List<Number> arraySpecifiers;
    public CType(String typeName, String variableName){
        int numberOfPointers1 = 0;
        String[] keywords = {"union","struct","enum","signed" /*includes unsigned*/};
        StringBuilder tmpTypeName = new StringBuilder(typeName);
        for(String keyword : keywords){
            int index = tmpTypeName.indexOf(keyword);
            if(index != -1){
                int insertIndex = index + keyword.length();
                tmpTypeName.insert(insertIndex," ");
            }
        }
        this.name = tmpTypeName.toString().strip().replaceAll("\\s+"," ");
        if(variableName.startsWith("*")){
            numberOfPointers1 = variableName.lastIndexOf("*")+1;
        }
        this.arraySpecifiers = new ArrayList<>();
        int index  = variableName.indexOf('[');
        if(index != -1){
            String arraysSpecifierStr = variableName.substring(index);
            Pattern pattern = Pattern.compile("\\[(\\d+)]");
            Matcher matcher = pattern.matcher(arraysSpecifierStr);

            while(matcher.find()){
                long num = Integer.parseInt(matcher.group(1));
                arraySpecifiers.add(num);
            }
            if(arraySpecifiers.isEmpty()){
                index = variableName.indexOf("[]");
                while(index != -1){
                    numberOfPointers1++;
                    index = variableName.indexOf("[]",index + 2);
                }
            }
        }
        this.numberOfPointers = numberOfPointers1;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPointers() {
        return numberOfPointers;
    }

    public List<Number> getArraySpecifiers(){
        return arraySpecifiers;
    }

    public String toString(){
        return name + "*".repeat(numberOfPointers);
    }


    @Override
    public CType clone() throws CloneNotSupportedException {
        CType clone = (CType) super.clone();
        clone.name = this.name;
        clone.arraySpecifiers = new ArrayList<>(this.arraySpecifiers.size());
        clone.arraySpecifiers.addAll(this.arraySpecifiers);
        clone.numberOfPointers = this.numberOfPointers;
        return clone;
    }
}
