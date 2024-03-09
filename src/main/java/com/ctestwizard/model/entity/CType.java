package com.ctestwizard.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CType implements CElement{
    private final String name;
    private final int numberOfPointers;
    private final List<Number> arraySpecifiers;
    public CType(String typeName, String variableName){
        String[] keywords = {"union","struct","enum"};
        StringBuilder tmpTypeName = new StringBuilder(typeName);
        for(String keyword : keywords){
            int index = tmpTypeName.indexOf(keyword);
            if(index != -1){
                int insertIndex = index + keyword.length();
                tmpTypeName.insert(insertIndex,' ');
            }
        }
        this.name = tmpTypeName.toString();
        this.numberOfPointers = variableName.lastIndexOf("*")+1;
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
        }
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

}
