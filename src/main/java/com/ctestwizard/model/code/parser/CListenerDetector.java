package com.ctestwizard.model.code.parser;

import com.ctestwizard.model.code.parser.generated.CBaseListener;
import com.ctestwizard.model.code.parser.generated.CParser;
import com.ctestwizard.model.code.entity.*;
import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Listener used to detect the different elements in the C source file
 */
public class CListenerDetector extends CBaseListener implements Cloneable {
    private List<CElement> structOrUnionList;
    private List<CFunction> localFunctions;
    private List<CElement> enumList;
    private List<CFunction> externalFunctions;
    private List<CElement> globals;

    /**
     * Constructor for the listener
     */
    public CListenerDetector() {
        this.structOrUnionList = new ArrayList<>();
        this.localFunctions = new ArrayList<>();
        this.enumList = new ArrayList<>();
        this.externalFunctions = new ArrayList<>();
        this.globals = new ArrayList<>();
    }

    /**
     * Function that transforms a function prototype into a CFunction object
     *
     * @param definitionContext The context of the name and parameters
     * @param typeContext       The context of the function return type
     */
    public void handleFunctionPrototype(CParser.DeclaratorContext definitionContext, List<CParser.DeclarationSpecifierContext> typeContext) {
        /*
        Check if the return type contains pointers
         */
        String functionPointers = "";
        if (definitionContext.pointer() != null) {
            functionPointers = definitionContext.pointer().getText();
        }
        String functionName = definitionContext.directDeclarator().directDeclarator().getText();
        List<CElement> functionParameters = new ArrayList<>();
        StringBuilder functionReturnType = new StringBuilder();
        boolean isInline = false;
        CStorageClass functionStorageClass = CStorageClass.NONE;
        /*
        Function has parameters
         */
        if (definitionContext.directDeclarator().parameterTypeList() != null) {
            int index = 1; // Index used for parameters without name
            List<CParser.ParameterDeclarationContext> _ctx = definitionContext
                    .directDeclarator().parameterTypeList().parameterList().parameterDeclaration();
            for (CParser.ParameterDeclarationContext parameterCtx : _ctx) {
                String parameterType;
                String parameterName;
                CVariable parameter;
                /*
                Parameter has a name
                 */
                if (parameterCtx.declarationSpecifiers() != null) {
                    parameterName = parameterCtx.declarator().getText();
                    parameterType = parameterCtx.declarationSpecifiers().declarationSpecifier().stream().map(RuleContext::getText).collect(Collectors.joining(" "));
                    parameter = new CVariable(parameterType, parameterName);
                }
                /*
                Parameter has no name
                 */
                else {
                    parameterType = parameterCtx.declarationSpecifiers2().declarationSpecifier().stream().map(RuleContext::getText).collect(Collectors.joining(" "));
                    /*
                    Parameter has array specifiers or pointers
                     */
                    if (parameterCtx.abstractDeclarator() != null) {
                        parameterName = parameterCtx.abstractDeclarator().getText();
                        /*
                        Better and more readable replacement
                         */
                        parameterName = parameterName.replace("[]", "*");
                        parameterName = parameterName + "param_" + index;
                    } else {
                        parameterName = "param_" + index;
                    }
                    index++;
                    parameter = new CVariable(parameterType, parameterName);
                }
                functionParameters.add(parameter);
            }
        }
        for (CParser.DeclarationSpecifierContext ctx : typeContext) {
            /*
            Extracting only the relevant storage class specifiers
             */
            functionStorageClass = switch (ctx.getText()) {
                case "extern" -> CStorageClass.EXTERN;
                case "static" -> CStorageClass.STATIC;
                default -> functionStorageClass;
            };
            switch (ctx.getText()) {
                /*
                Ignoring keywords unrelated to the return type
                 */
                case "extern", "static", "auto", "_Thread_local":
                    break;
                case "inline":
                    isInline = true;
                    break;
                case "typedef": //Typedef means there is no function prototype, terminate function
                    return;
                default:
                    functionReturnType.append(ctx.getText()).append(" ");
            }
        }
        functionReturnType.append(functionPointers);
        this.externalFunctions.add(
                new CFunction(functionStorageClass, isInline, functionReturnType.toString().strip(), functionName, functionParameters));
    }

    /**
     * Due to the implementation of the C Antlr Grammar, initialized globals are sometimes treated differently in the parser,
     * This method converts the initialized global into a CVariable object
     *
     * @param definitionContext The context of the name of the global
     * @param typeContext       The context of the return type and storage class specifier
     */
    public void handleInitializedGlobals(CParser.DeclaratorContext definitionContext, List<CParser.DeclarationSpecifierContext> typeContext) {
        String globalName = definitionContext.getText();
        StringBuilder globalType = new StringBuilder();
        for (CParser.DeclarationSpecifierContext declarationSpecifierContext : typeContext) {
            /*
            Check if it's a type specifier(e.g. int, float, user-defined type etc.)
             */
            if (declarationSpecifierContext.typeSpecifier() != null) {
                /*
                Check if it's a structure or an enum instance (not definition)
                 */
                if (declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier() != null && declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().structDeclarationList() != null) {
                    globalType = new StringBuilder(declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().structOrUnion().getText() + " " +
                            declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().Identifier().getText());
                } else if (declarationSpecifierContext.typeSpecifier().enumSpecifier() != null && declarationSpecifierContext.typeSpecifier().enumSpecifier().enumeratorList() != null) {
                    globalType = new StringBuilder("enum " + declarationSpecifierContext.typeSpecifier().enumSpecifier().Identifier().getText());
                } else {
                    if(declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier() != null){
                        if(declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().structOrUnion() != null){
                            globalType.append(" ").append(declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().structOrUnion().getText());
                            globalType.append(" ").append(declarationSpecifierContext.typeSpecifier().structOrUnionSpecifier().Identifier().getText());
                        }
                    }else{
                        globalType.append(" ").append(declarationSpecifierContext.typeSpecifier().getText());
                    }
                }
            }
        }
        CElement global = new CVariable(globalType.toString().strip(), globalName);
        this.globals.add(global);

    }

    /**
     * Unimplemented method for function pointers (implementation for future development)
     *
     * @param definitionContext - unused
     * @param typeContext       - unused
     */
    public void handleFunctionPointer(CParser.DeclaratorContext definitionContext, List<CParser.DeclarationSpecifierContext> typeContext) {
        return; // No support for function pointers yet
    }

    /**
     * Method that handles an uninitialized global of a struct or union, converts to a CStructOrUnion object
     *
     * @param ctx          Context of the struct or union type
     * @param nameCtx      Context of the global name
     * @param storageClass The storage class specifier of the global
     */
    public void handleStructOrUnionGlobal(CParser.StructOrUnionSpecifierContext ctx, CParser.DeclarationSpecifierContext nameCtx, CStorageClass storageClass) {
        String type = ctx.structOrUnion().getText() + " " + ctx.Identifier().getText();
        String name = nameCtx.getText();
        CElement global = new CVariable(type.strip(), name);
        this.globals.add(global);
    }

    /**
     * Method to handle enum globals, converts the enum to a CEnum object
     *
     * @param ctx          Context of the enum type
     * @param nameCtx      Context of the global nam
     * @param storageClass Storage class specifier of the global
     */
    public void handleEnumGlobal(CParser.EnumSpecifierContext ctx, CParser.DeclarationSpecifierContext nameCtx, CStorageClass storageClass) {
        String type = "enum " + ctx.Identifier().getText();
        String name = nameCtx.getText();
        CElement global = new CVariable(type.strip(), name);
        this.globals.add(global);
    }

    /**
     * Method to handle globals that are primitive types, and types with typedef, converts the global to a CVariable object
     *
     * @param ctxList The context of the global
     */
    public void handleGlobal(List<CParser.DeclarationSpecifierContext> ctxList) {
        StringBuilder type = new StringBuilder();
        for (int i = 0; i < ctxList.size() - 1; i++) {
            /*
            Check if the current keyword is a type specifier
             */
            if (ctxList.get(i).typeSpecifier() != null) {
                /*
                Guard against possible conflict with the methods that handle struct and enums
                 */
                if (ctxList.get(i).typeSpecifier().structOrUnionSpecifier() != null || ctxList.get(i).typeSpecifier().enumSpecifier() != null) {
                    return;
                }
                type.append(" ").append(ctxList.get(i).typeSpecifier().getText());
            }
        }
        String name = ctxList.get(ctxList.size() - 1).getText();
        CElement globalVariable = new CVariable(type.toString().strip(), name);
        this.globals.add(globalVariable);
    }

    /**
     * Method that checks for uninitialized globals, struct or union definitions, enum definitions and converts them to their respective object
     *
     * @param ctxList The declaration context
     */
    public void handleDeclaration(List<CParser.DeclarationSpecifierContext> ctxList) {
        boolean keywordGuard = false; //Guard for the unsigned and signed keywords for primitive types
        CStorageClass storageClass = CStorageClass.NONE;
        for (CParser.DeclarationSpecifierContext declarationSpecifierContext : ctxList) {
            /*
            Check for storage class specifier
             */
            if (declarationSpecifierContext.storageClassSpecifier() != null) {
                storageClass = CStorageClass.strToStorageClass(declarationSpecifierContext.storageClassSpecifier().getText());
            }
            /*
            Check if it's a type keyword
             */
            else if (declarationSpecifierContext.typeSpecifier() != null) {
                CParser.TypeSpecifierContext ctx = declarationSpecifierContext.typeSpecifier();
                /*
                Check for a struct or union
                 */
                if (ctx.structOrUnionSpecifier() != null) {
                    String typedefName = null;
                    /*
                    If it was defined with typedef use only the typedef name
                     */
                    if (storageClass == CStorageClass.TYPEDEF) {
                        typedefName = ctxList.get(ctxList.size() - 1).getText();
                    }
                    handleStructOrUnionSpecifier(ctx.structOrUnionSpecifier(), typedefName);
                    /*
                    Check if it's just a struct or union instance
                     */
                    if (ctx.structOrUnionSpecifier().structDeclarationList() == null && storageClass != CStorageClass.TYPEDEF) {
                        handleStructOrUnionGlobal(ctx.structOrUnionSpecifier(), ctxList.get(ctxList.size() - 1), storageClass);
                    }
                }
                /*
                Check for an enum
                 */
                else if (ctx.enumSpecifier() != null) {
                    String typedefName = null;
                    /*
                    If it was defined with typedef, then use only the typedef name
                     */
                    if (storageClass == CStorageClass.TYPEDEF) {
                        typedefName = ctxList.get(ctxList.size() - 1).getText();
                    }
                    handleEnumSpecifier(ctx.enumSpecifier(), typedefName);
                    /*
                    Check if it's an instance of an enum
                     */
                    if (ctx.enumSpecifier().enumeratorList() == null && storageClass != CStorageClass.TYPEDEF) {
                        handleEnumGlobal(ctx.enumSpecifier(), ctxList.get(ctxList.size() - 1), storageClass);
                    }
                 /*
                 Check for user-defined type or primitive type
                  */
                } else {
                    if (storageClass != CStorageClass.TYPEDEF && !keywordGuard) {
                        keywordGuard = true;
                        handleGlobal(ctxList);
                    }
                }
            }
        }
    }

    /**
     * Method used to recursively go over every anonymous struct or union inside another structure
     *
     * @param ctx Context of the current member of the outer structure
     * @return The member of the structure converted in its respective class (CStructOrUnion, CVariable, CEnum)
     */
    private CElement getStructOrUnionBodyMember(CParser.StructDeclarationContext ctx) {
        CParser.TypeSpecifierContext typeContext = ctx.specifierQualifierList().typeSpecifier();
        CParser.StructDeclaratorListContext nameContext = ctx.structDeclaratorList();
        /*
            Member is a struct or union
        */
        if (typeContext.structOrUnionSpecifier() != null) {
            /*
                Instance of a struct or union
            */
            if (typeContext.structOrUnionSpecifier().structDeclarationList() == null) {
                String instanceType = typeContext.getText();
                String instanceName = nameContext.getText();
                return new CVariable(instanceType, instanceName);
            }
            /*
                Anonymous struct or union
             */
            else {
                List<CElement> anonymousStructOrUnionMembers = new ArrayList<>();
                String anonymousStructOrUnionName = nameContext.getText();
                List<CParser.StructDeclarationContext> contextList = typeContext
                        .structOrUnionSpecifier().structDeclarationList().structDeclaration();
                for (CParser.StructDeclarationContext context : contextList) {
                    String memberType = context.specifierQualifierList().getText();
                    String memberName = context.structDeclaratorList().getText();
                    /*
                     * Member has a bitfield, parser treats the member name differently
                     */
                    if (memberName.startsWith(":")) {
                        memberName = context.specifierQualifierList().specifierQualifierList().getText();
                    }
                    CElement tmpMember;
                    if (memberType.startsWith("union") || memberType.startsWith("struct") || memberType.startsWith("enum")) {
                        tmpMember = getStructOrUnionBodyMember(context);
                    } else {
                        tmpMember = new CVariable(memberType, memberName);
                    }
                    anonymousStructOrUnionMembers.add(tmpMember);
                }
                return new CStructOrUnion(anonymousStructOrUnionName, anonymousStructOrUnionMembers);
            }
        }
        /*
            Member is an enum
        */
        else {
            /*
                Instance of an enum
             */
            if (typeContext.enumSpecifier().enumeratorList() == null) {
                String enumType = typeContext.getText();
                String enumName = nameContext.getText();
                return new CVariable(enumType, enumName);
            }
            /*
                Anonymous enum
             */
            else {
                List<String> enumMembers = new ArrayList<>();
                Map<String, Integer> symbolMap = new HashMap<>();
                List<CParser.EnumeratorContext> enumContextList = typeContext
                        .enumSpecifier().enumeratorList().enumerator();
                int index = 0;
                for (CParser.EnumeratorContext enumContext : enumContextList) {
                    enumMembers.add(enumContext.enumerationConstant().getText());
                    if(enumContext.constantExpression() != null){
                        symbolMap.put(enumContext.enumerationConstant().getText(), Integer.parseInt(enumContext.constantExpression().getText()));
                        index = Integer.parseInt(enumContext.constantExpression().getText()) + 1;
                    }else{
                        symbolMap.put(enumContext.enumerationConstant().getText(), index);
                        index++;
                    }
                }
                return new CEnum(nameContext.getText(), enumMembers, symbolMap);
            }
        }
    }

    /**
     * Method that converts a struct or union to its respective object (CStructOrUnion)
     *
     * @param ctx         The parser tree
     * @param typedefName A name used in case the struct was defined with the typedef storage class specifier
     */
    private void handleStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx, String typedefName) {
        String structOrUnionName = null;
        if (typedefName != null) {
            structOrUnionName = typedefName;
        } else if (ctx.Identifier() != null) {
            structOrUnionName = ctx.structOrUnion().getText() + " " + ctx.Identifier().getText();
        }
        if(typedefName != null && ctx.Identifier() != null) {
            handleStructOrUnionSpecifier(ctx, null);
        }
        /*
        Check if the structure has a body
         */
        if (ctx.structDeclarationList() != null) {
            List<CElement> structOrUnionBody = new ArrayList<>();
            List<CParser.StructDeclarationContext> structOrUnionBodyCtx =
                    ctx.structDeclarationList().structDeclaration();
            for (CParser.StructDeclarationContext context : structOrUnionBodyCtx) {
                String memberType = context.specifierQualifierList().getText();
                String memberName = context.structDeclaratorList().getText();
                /*
                 * Member has a bitfield, parser treats the member name differently
                 */
                if (memberName.startsWith(":")) {
                    memberName = context.specifierQualifierList().specifierQualifierList().getText();
                }
                CElement tmpMember = null;
                /*
                Check for other structures, or anonymous structure defined locally within the initial structure
                 */
                if (memberType.startsWith("union") || memberType.startsWith("struct") || memberType.startsWith("enum")) {
                    tmpMember = getStructOrUnionBodyMember(context);
                } else {
                    tmpMember = new CVariable(memberType, memberName);
                }
                structOrUnionBody.add(tmpMember);
            }
            this.structOrUnionList.add(new CStructOrUnion(structOrUnionName, structOrUnionBody));
        }
    }

    /**
     * Method that converts an enum to its respective object (CEnum)
     *
     * @param ctx         The parser tree
     * @param typedefName Name used in case the enum was defined with the typedef storage class specifier
     */
    private void handleEnumSpecifier(CParser.EnumSpecifierContext ctx, String typedefName) {
        String enumName = null;
        if (typedefName != null) {
            enumName = typedefName;
        } else if (ctx.Identifier() != null) {
            enumName = ctx.Identifier().getText();
        }
        List<String> enumBody;
        /*
        Check if the enum has a body(it's a definition and not an instance)
         */
        if (ctx.enumeratorList() != null) {
            enumBody = new ArrayList<>();
            HashMap<String, Integer> symbolMap = new HashMap<>();
            int index = 0;
            for (CParser.EnumeratorContext context : ctx.enumeratorList().enumerator()) {
                enumBody.add(context.enumerationConstant().getText());
                if(context.constantExpression() != null){
                    symbolMap.put(context.enumerationConstant().getText(), Integer.parseInt(context.constantExpression().getText()));
                    index = Integer.parseInt(context.constantExpression().getText()) + 1;
                }else{
                    symbolMap.put(context.enumerationConstant().getText(), index);
                    index++;
                }
            }
            this.enumList.add(new CEnum(enumName, enumBody,symbolMap));
        }
    }

    /**
     * Starting point of the parser for every global, function prototype, struct, union and enum definitions
     *
     * @param ctx the parse tree
     */
    @Override
    public void enterExternalDeclaration(CParser.ExternalDeclarationContext ctx) {
        /*
        Skip the case of a local function, treated in enterFunctionDefinition method
         */
        if (ctx.functionDefinition() != null) {
            return;
        }
        CParser.DeclarationContext ctx2 = ctx.declaration();
        /*
            Initialized Global or Function Prototype
         */
        if (ctx2.initDeclaratorList() != null) {
            CParser.DeclaratorContext _ctx2 = ctx2.initDeclaratorList().initDeclarator(0).declarator();
            /*
            Function Prototype
             */
            if (_ctx2.getText().endsWith(")")) {
                if (!_ctx2.getText().startsWith("(")) {
                    handleFunctionPrototype(_ctx2, ctx.declaration().declarationSpecifiers().declarationSpecifier());
                } else {
                    handleFunctionPointer(_ctx2, ctx.declaration().declarationSpecifiers().declarationSpecifier());
                }
            }
            /*
            Initialized Global
             */
            else {
                handleInitializedGlobals(_ctx2, ctx.declaration().declarationSpecifiers().declarationSpecifier());
            }
        }
        /*
        Uninitialized global, structsOrUnion, enum definitions
         */
        else {
            handleDeclaration(ctx.declaration().declarationSpecifiers().declarationSpecifier());
        }
    }

    /**
     * Starting point of every locally defined function
     *
     * @param ctx the parse tree
     */
    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        CStorageClass storageClassSpecifier = CStorageClass.NONE;
        boolean isInline = false;
        StringBuilder type = new StringBuilder();
        String name;
        List<CElement> parameterList = new ArrayList<>();
        for (int i = 0; i < ctx.declarationSpecifiers().declarationSpecifier().size(); i++) {
            /*
            Find the function's storage class specifier(and if it's inline)
             */
            switch (ctx.declarationSpecifiers().declarationSpecifier(i).getText()) {
                case "inline":
                    isInline = true;
                    break;
                case "extern":
                    storageClassSpecifier = CStorageClass.EXTERN;
                    break;
                case "static":
                    storageClassSpecifier = CStorageClass.STATIC;
                    break;
                case "auto":
                    storageClassSpecifier = CStorageClass.AUTO;
                    break;
                default:
                    type.append(ctx.declarationSpecifiers().declarationSpecifier(i).getText());
            }
        }
        if (ctx.declarator().pointer() != null) {
            type.append(" ").append(ctx.declarator().pointer().getText());
        }
        name = ctx.declarator().directDeclarator().directDeclarator().getText();
        /*
        Check if the function has parameters
         */
        if (ctx.declarator().directDeclarator().parameterTypeList() != null) {
            List<CParser.ParameterDeclarationContext> context = ctx
                    .declarator().directDeclarator().parameterTypeList().parameterList().parameterDeclaration();
            for (CParser.ParameterDeclarationContext parameterDeclarationContext : context) {
                String parameterType = parameterDeclarationContext.declarationSpecifiers().declarationSpecifier().stream().map(RuleContext::getText).collect(Collectors.joining(" "));
                String parameterName = parameterDeclarationContext.declarator().getText();
                CElement parameter = new CVariable(parameterType, parameterName);
                parameterList.add(parameter);
            }
        }
        String functionReturnType = type.toString();
        this.localFunctions.add(new CFunction(storageClassSpecifier, isInline, functionReturnType, name, parameterList));
    }

    public List<CFunction> getLocalFunctions() {
        return localFunctions;
    }

    public List<CElement> getStructOrUnionList() {
        return structOrUnionList;
    }

    public List<CElement> getEnumList() {
        return enumList;
    }

    public List<CFunction> getExternalFunctions() {
        return externalFunctions;
    }

    public List<CElement> getGlobals() {
        return globals;
    }

    public void setStructOrUnionList(List<CElement> structOrUnionList) {
        this.structOrUnionList = structOrUnionList;
    }

    public void setLocalFunctions(List<CFunction> localFunctions) {
        this.localFunctions = localFunctions;
    }

    public void setEnumList(List<CElement> enumList) {
        this.enumList = enumList;
    }

    public void setExternalFunctions(List<CFunction> externalFunctions) {
        this.externalFunctions = externalFunctions;
    }

    public void setGlobals(List<CElement> globals) {
        this.globals = globals;
    }

    @Override
    public CListenerDetector clone() {
        try {
            CListenerDetector clone = (CListenerDetector) super.clone();
            clone.globals = new ArrayList<>(this.globals.size());
            for (CElement global : this.globals) {
                clone.globals.add(global.clone());
            }
            clone.enumList = new ArrayList<>(this.enumList.size());
            for (CElement enumEl : this.enumList) {
                clone.enumList.add(enumEl.clone());
            }
            clone.structOrUnionList = new ArrayList<>(this.structOrUnionList.size());
            for (CElement structOrUnion : this.structOrUnionList) {
                clone.structOrUnionList.add(structOrUnion.clone());
            }
            clone.externalFunctions = new ArrayList<>(this.externalFunctions.size());
            for (CFunction externalFunction : this.externalFunctions) {
                clone.externalFunctions.add(externalFunction.clone());
            }
            clone.localFunctions = new ArrayList<>(this.localFunctions.size());
            for (CFunction localFunction : this.localFunctions) {
                clone.localFunctions.add(localFunction.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
