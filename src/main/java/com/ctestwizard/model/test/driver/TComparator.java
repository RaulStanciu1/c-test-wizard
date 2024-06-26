package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Comparator class used to compare the elements of the C language
 */
public class TComparator {
    /**
     * Compare two enum instances
     * @param enumInstance The enum instance
     * @param enumDefinition The enum to compare to
     * @return If the enums are different
     */
    private static boolean compareEnumInstances(CElement enumInstance, CElement enumDefinition) {
        CEnumInstance enumInstance1 = (CEnumInstance) enumInstance;
        CEnumInstance enumDefinition1 = (CEnumInstance) enumDefinition;
        return !enumInstance1.getName().equals(enumDefinition1.getName()) || compareEnums(enumInstance1.getEnumType(), enumDefinition1.getEnumType());
    }

    /**
     * Compare two enums
     * @param enumDefinition The enum definition
     * @param enumType The enum type to compare to
     * @return If the enums are different
     */
    private static boolean compareEnums(CElement enumDefinition, CElement enumType) {
        CEnum enumDef = (CEnum) enumDefinition;
        CEnum enumT = (CEnum) enumType;
        if (enumDef.getMembers().size() != enumT.getMembers().size()) {
            return true;
        }
        for (int i = 0; i < enumDef.getMembers().size(); i++) {
            if (!enumDef.getMembers().get(i).equals(enumT.getMembers().get(i))) {
                return true;
            }
        }
        return !enumDefinition.getName().equals(enumType.getName());
    }

    /**
     * Compare two variables
     * @param variableDefinition The variable definition
     * @param variableType The variable to compare to
     * @return If the variables are different
     */
    private static boolean compareVariables(CElement variableDefinition, CElement variableType) {
        CVariable varDef = (CVariable) variableDefinition;
        CVariable varType = (CVariable) variableType;
        return !varDef.getName().equals(varType.getName()) || !varDef.getType().equals(varType.getType());
    }

    /**
     * Compare two arrays
     * @param arrayDefinition The array definition
     * @param arrayType The array to compare to
     * @return If the arrays are different
     */
    private static boolean compareArrays(CElement arrayDefinition, CElement arrayType) {
        CArray arrayDef = (CArray) arrayDefinition;
        CArray arrayT = (CArray) arrayType;

        boolean nameMatch = !arrayDef.getName().equals(arrayT.getName());
        boolean typeMatch = !arrayDef.getType().equals(arrayT.getType());
        boolean sizeMatch = arrayDef.getArrayMembers().size() != arrayT.getArrayMembers().size();
        return nameMatch || typeMatch || sizeMatch;
    }

    /**
     * Compare two struct or union instances
     * @param structOrUnionDef The struct or union definition
     * @param structOrUnionType The struct or union to compare to
     * @return If the struct or union instances are different
     */
    private static boolean compareStructAndUnionInstances(CElement structOrUnionDef, CElement structOrUnionType) {
        CStructOrUnionInstance structOrUnionDef1 = (CStructOrUnionInstance) structOrUnionDef;
        CStructOrUnionInstance structOrUnionType1 = (CStructOrUnionInstance) structOrUnionType;
        return !structOrUnionDef.getName().equals(structOrUnionType.getName()) || compareStructAndUnions(structOrUnionDef1.getStructType(), structOrUnionType1.getStructType());
    }

    /**
     * Compare two struct or unions
     * @param structOrUnionDef The struct or union definition
     * @param structOrUnionType The struct or union to compare to
     * @return If the struct or unions are different
     */
    private static boolean compareStructAndUnions(CElement structOrUnionDef, CElement structOrUnionType) {
        if (!structOrUnionDef.getName().equals(structOrUnionType.getName())) {
            return true;
        }
        CStructOrUnion structOrUnionDef1 = (CStructOrUnion) structOrUnionDef;
        CStructOrUnion structOrUnionType1 = (CStructOrUnion) structOrUnionType;
        if (structOrUnionDef1.getMembers().size() != structOrUnionType1.getMembers().size()) {
            return true;
        }

        for (int i = 0; i < structOrUnionDef1.getMembers().size(); i++) {
            if (structOrUnionDef1.getMembers().get(i) instanceof CVariable varMemberDef) {
                if (compareVariables(varMemberDef, structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CStructOrUnionInstance) {
                if (compareStructAndUnionInstances(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CEnumInstance) {
                if (compareEnumInstances(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            } else if (structOrUnionDef1.getMembers().get(i) instanceof CArray) {
                if (compareArrays(structOrUnionDef1.getMembers().get(i), structOrUnionType1.getMembers().get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compare two enum lists
     * @param enumDefinitions The enum definitions
     * @param enumTypes The enum types to compare to
     * @return If the enums are different
     */
    public static boolean compareEnumDefinitions(List<CElement> enumDefinitions, List<CElement> enumTypes) {
        if (enumDefinitions.isEmpty() && enumTypes.isEmpty()) {
            return false;
        }
        if (enumDefinitions.size() != enumTypes.size()) {
            return true;
        }
        for (int i = 0; i < enumDefinitions.size(); i++) {
            if (compareEnums(enumDefinitions.get(i), enumTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compare two struct or union lists
     * @param structAndUnionDefinitions The struct or union definitions
     * @param structOrUnionTypes The struct or union types to compare to
     * @return If the struct or unions are different
     */
    public static boolean compareStructAndUnionDefinitions(List<CElement> structAndUnionDefinitions, List<CElement> structOrUnionTypes) {
        if (structAndUnionDefinitions.isEmpty() && structOrUnionTypes.isEmpty()) {
            return false;
        }
        if (structAndUnionDefinitions.size() != structOrUnionTypes.size()) {
            return true;
        }
        for (int i = 0; i < structAndUnionDefinitions.size(); i++) {
            if (compareStructAndUnions(structAndUnionDefinitions.get(i), structOrUnionTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compare two functions
     * @param function The function
     * @param functionDefinition The function to compare to
     * @return If the functions are different
     */
    private static boolean compareFunction(CFunction function, CFunction functionDefinition) {
        if (!function.getName().equals(functionDefinition.getName())) {
            return false;
        }
        if (!function.getStrType().equals(functionDefinition.getStrType())) {
            return false;
        }
        if (function.getParameters().size() != functionDefinition.getParameters().size()) {
            return false;
        }
        for (int i = 0; i < function.getParameters().size(); i++) {
            if (function.getParameters().get(i) instanceof CVariable var && functionDefinition.getParameters().get(i) instanceof CVariable var1) {
                if (compareVariables(var, var1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CArray array && functionDefinition.getParameters().get(i) instanceof CArray array1) {
                if (compareArrays(array, array1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CStructOrUnionInstance structOrUnion && functionDefinition.getParameters().get(i) instanceof CStructOrUnionInstance structOrUnion1) {
                if (compareStructAndUnionInstances(structOrUnion, structOrUnion1)) {
                    return false;
                }
            } else if (function.getParameters().get(i) instanceof CEnumInstance enum1 && functionDefinition.getParameters().get(i) instanceof CEnumInstance enum2) {
                if (compareEnumInstances(enum1, enum2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches if a function is present in a list of functions
     * @param testFunction The function to compare
     * @param localFunctionDefinitions The local function definitions
     * @return If the function is present in the list
     */
    public static boolean compareCFunction(CFunction testFunction, List<CFunction> localFunctionDefinitions) {
        boolean found = false;
        for (CFunction localFunction : localFunctionDefinitions) {
            if (compareFunction(testFunction, localFunction)) {
                found = true;
                break;
            }
        }
        return !found;
    }

    /**
     * Compare two function lists
     * @param testFunction The test function
     * @param localFunctions The local functions
     * @param localFunctionDefinitions The local function to compare to
     * @return If the functions are different
     */
    public static boolean compareCFunctionList(CFunction testFunction, List<CFunction> localFunctions, List<CFunction> localFunctionDefinitions) {
        //Remove the testFunction from the localFunctionDefinitions list copy
        List<CFunction> localFunctionDefsCopy = new ArrayList<>(List.copyOf(localFunctionDefinitions));
        for (CFunction localFunctionDef : localFunctionDefsCopy) {
            if (compareFunction(testFunction, localFunctionDef)) {
                localFunctionDefsCopy.remove(localFunctionDef);
                break;
            }
        }
        //Compare the localFunctions with the localFunctionDefinitions copy
        if (localFunctions.size() != localFunctionDefsCopy.size()) {
            return true;
        }
        for (int i = 0; i < localFunctions.size(); i++) {
            if (!compareFunction(localFunctions.get(i), localFunctionDefsCopy.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compare two element lists
     * @param cElements The elements
     * @param globals The globals to compare to
     * @return If the elements are different
     */
    public static boolean compareCElementList(Set<CElement> cElements, List<CElement> globals) {
        if (cElements.size() != globals.size()) {
            return true;
        }
        for (CElement cElement : cElements) {
            boolean found = false;
            for (CElement global : globals) {
                if (cElement instanceof CVariable var && global instanceof CVariable var1) {
                    if (!compareVariables(var, var1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CArray array && global instanceof CArray array1) {
                    if (!compareArrays(array, array1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CStructOrUnionInstance structOrUnion && global instanceof CStructOrUnionInstance structOrUnion1) {
                    if (!compareStructAndUnionInstances(structOrUnion, structOrUnion1)) {
                        found = true;
                        break;
                    }
                } else if (cElement instanceof CEnumInstance enum1 && global instanceof CEnumInstance enum2) {
                    if (!compareEnumInstances(enum1, enum2)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return true;
            }
        }
        return false;
    }
}
