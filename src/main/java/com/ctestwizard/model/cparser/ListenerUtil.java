package com.ctestwizard.model.cparser;


public class ListenerUtil {
    public static String getVariableName(String variable){
        int index;
        if(variable.startsWith("*")){
            index = variable.lastIndexOf('*');
            if(index != -1){
                variable = variable.substring(index+1);
            }
        }
        index = variable.indexOf('[');
        if(index != -1){
            variable = variable.substring(0,index);
        }
        return variable;
    }

}
