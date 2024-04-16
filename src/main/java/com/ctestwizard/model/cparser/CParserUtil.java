package com.ctestwizard.model.cparser;


import com.ctestwizard.model.entity.*;

import java.util.List;

public class CParserUtil {

    public static boolean isDefinedStructOrUnion(String type, List<CElement> structOrUnionList){
        for(CElement _structOrUnion : structOrUnionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(structOrUnion.getName().stripIndent().equals(type)){
                return true;
            }
        }
        return false;
    }

    public static CStructOrUnionInstance convertVariableToStructOrUnionInstance(CVariable var, List<CElement> definitionList){
        CStructOrUnion variableType = null;
        for(CElement _structOrUnion : definitionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(structOrUnion.getName().stripIndent().equals(var.getType())){
                 variableType = structOrUnion;
            }
        }
        assert variableType != null;
        return new CStructOrUnionInstance(variableType,var.getName());
    }

    public static boolean isEnum(String var, List<CElement> enumList){
        for(CElement enumEl : enumList){
            if(var.equals((enumEl).getName())){
                return true;
            }
        }
        return false;
    }

    public static CEnumInstance convertVariableToEnum(CVariable var, List<CElement> enumList){
        CEnum variableType = null;
        for(CElement _enumEl : enumList){
            CEnum enumEl = (CEnum)_enumEl;
            if(enumEl.getName().stripIndent().equals(var.getType())){
                variableType = enumEl;
            }
        }

        assert variableType != null;
        return new CEnumInstance(variableType,var.getName());
    }

    public static CStructOrUnionInstance convertTypeToStructOrUnionInstance(String name,String type,List<CElement>structOrUnionList){
        CStructOrUnion variableType = null;
        for(CElement _structOrUnion:structOrUnionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(type.equals(structOrUnion.getName().strip())){
                variableType = structOrUnion;
            }
        }
        assert variableType != null;
        return new CStructOrUnionInstance(variableType,name+"()");
    }


    public static CElement convertTypeToEnumInstance(String name,String returnType, List<CElement> enumDefinitions) {
        CEnum variableType = null;
        for(CElement _enum : enumDefinitions){
            CEnum enumEl = (CEnum)_enum;
            if(returnType.equals(enumEl.getName().strip())){
                variableType = enumEl;
            }
        }
        assert variableType != null;
        return new CEnumInstance(variableType,name+"()");
    }
}
