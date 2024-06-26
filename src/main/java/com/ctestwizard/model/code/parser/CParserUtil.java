package com.ctestwizard.model.code.parser;


import com.ctestwizard.model.code.entity.*;

import java.util.List;

/**
 * Utility class for the parser
 */
public class CParserUtil {
    /**
     * Check if the type is a defined struct or union
     * @param type The type to check
     * @param structOrUnionList The list of struct or union definitions
     * @return True if the type is a defined struct or union
     */
    public static boolean isDefinedStructOrUnion(String type, List<CElement> structOrUnionList){
        for(CElement _structOrUnion : structOrUnionList){
            CStructOrUnion structOrUnion = (CStructOrUnion) _structOrUnion;
            if(structOrUnion.getName().stripIndent().equals(type)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the type is a defined enum
     * @param var The variable to check
     * @param definitionList The list of enum definitions
     * @return True if the type is a defined enum
     */
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

    /**
     * Check if the type is a defined enum
     * @param var The variable to check
     * @param enumList The list of enum definitions
     * @return True if the type is a defined enum
     */
    public static boolean isEnum(String var, List<CElement> enumList){
        for(CElement enumEl : enumList){
            if(var.equals((enumEl).getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a variable to an enum instance
     * @param var The variable to convert
     * @param enumList The list of enum definitions
     * @return The enum instance
     */
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

    /**
     * Convert a variable to a struct or union instance
     * @param name The name of the variable
     * @param type The type of the variable
     * @param structOrUnionList The list of struct or union definitions
     * @return The struct or union instance
     */
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

    /**
     * Convert a type to an enum instance
     * @param name The name of the variable
     * @param returnType The return type
     * @param enumDefinitions The list of enum definitions
     * @return The enum instance
     */
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
