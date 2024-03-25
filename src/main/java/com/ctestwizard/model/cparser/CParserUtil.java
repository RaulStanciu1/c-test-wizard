package com.ctestwizard.model.cparser;


import com.ctestwizard.model.entity.*;

import java.util.List;

public class CParserUtil {
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

    public static boolean isDefinedStructOrUnion(CType type, List<CElement> structOrUnionList){
        for(CElement _structOrUnion : structOrUnionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(structOrUnion.getName().stripIndent().equals(type.getName().stripIndent())){
                return true;
            }
        }
        return false;
    }

    public static CStructOrUnionInstance convertVariableToStructOrUnionInstance(CVariable var, List<CElement> definitionList){
        CStructOrUnion variableType = null;
        for(CElement _structOrUnion : definitionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(structOrUnion.getName().stripIndent().equals(var.getType().getName().stripIndent())){
                 variableType = structOrUnion;
            }
        }
        assert variableType != null;
        return new CStructOrUnionInstance(variableType,var.toString());
    }

    public static boolean isEnum(CType var, List<CElement> enumList){
        for(CElement enumEl : enumList){
            if(var.getName().stripIndent().equals(((CEnum)enumEl).getName().stripIndent())){
                return true;
            }
        }
        return false;
    }

    public static CEnumInstance convertVariableToEnum(CVariable var, List<CElement> enumList){
        CEnum variableType = null;
        for(CElement _enumEl : enumList){
            CEnum enumEl = (CEnum)_enumEl;
            if(enumEl.getName().stripIndent().equals(var.getType().getName().stripIndent())){
                variableType = enumEl;
            }
        }

        assert variableType != null;
        return new CEnumInstance(variableType,var.toString());
    }

    public static CStructOrUnion convertTypeToStructOrUnion(CType type,List<CElement>structOrUnionList){
        CStructOrUnion variableType = null;
        for(CElement _structOrUnion:structOrUnionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(type.getName().strip().equals(structOrUnion.getName().strip())){
                variableType = structOrUnion;
            }
        }
        assert variableType != null;
        return variableType;
    }


    public static CElement convertTypeToEnum(CType returnType, List<CElement> enumDefinitions) {
        CEnum variableType = null;
        for(CElement _enum : enumDefinitions){
            CEnum enumEl = (CEnum)_enum;
            if(returnType.getName().strip().equals(enumEl.getName().strip())){
                variableType = enumEl;
            }
        }
        assert variableType != null;
        return variableType;
    }
}
