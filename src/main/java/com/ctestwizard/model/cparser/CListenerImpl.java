package com.ctestwizard.model.cparser;

import com.ctestwizard.model.cparser.generated.CBaseListener;
import com.ctestwizard.model.cparser.generated.CParser;
import com.ctestwizard.model.entity.*;

import java.util.ArrayList;
import java.util.List;

public class CListenerImpl extends CBaseListener {
    private final List<CStructOrUnion> structOrUnionList;
    private final List<CFunction> localFunctions;
    public CListenerImpl(){
        this.structOrUnionList = new ArrayList<>();
        this.localFunctions = new ArrayList<>();
    }

    @Override public void enterDeclaration(CParser.DeclarationContext ctx) {
        //TODO: Implement
    }

    @Override public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        //TODO: Implement
    }

    private CElement getStructOrUnionBodyMember(CParser.StructDeclarationContext ctx){
        CParser.TypeSpecifierContext typeContext = ctx.specifierQualifierList().typeSpecifier();
        CParser.StructDeclaratorListContext nameContext = ctx.structDeclaratorList();
        /*
            Member is a struct or union
        */
        if(typeContext.structOrUnionSpecifier() != null){
            /*
                Instance of a struct or union
            */
            if(typeContext.structOrUnionSpecifier().structDeclarationList() == null){
                CType instanceType = new CType(typeContext.getText(), nameContext.getText());
                String instanceName = ListenerUtil.getVariableName(nameContext.getText());
                return new CVariable(instanceType, instanceName);
            }
            /*
                Anonymous struct or union
             */
            else{
                CElement tmpElement;
                List<CElement> anonymousStructOrUnionMembers = new ArrayList<>();
                String anonymousStructOrUnionName = nameContext.getText();
                List<CParser.StructDeclarationContext> contextList = typeContext
                        .structOrUnionSpecifier().structDeclarationList().structDeclaration();
                for(CParser.StructDeclarationContext context : contextList){
                    String memberType = context.specifierQualifierList().typeSpecifier().getText();
                    String memberName = context.structDeclaratorList().getText();
                    /*
                     * Member has a bitfield, parser treats the member name differently
                     */
                    if(memberName.startsWith(":")){
                        memberName = context.specifierQualifierList().specifierQualifierList().getText();
                    }
                    CElement tmpMember;
                    if(memberType.startsWith("union") || memberType.startsWith("struct") || memberType.startsWith("enum")){
                        tmpMember = getStructOrUnionBodyMember(context);
                    }else{
                        CType memberCType = new CType(memberType,memberName);
                        tmpMember = new CVariable(memberCType,ListenerUtil.getVariableName(memberName));
                    }
                    anonymousStructOrUnionMembers.add(tmpMember);
                }
                return new CStructOrUnion(anonymousStructOrUnionName,anonymousStructOrUnionMembers);
            }
        }
        /*
            Member is an enum
        */
        else{
            /*
                Instance of an enum
             */
            if(typeContext.enumSpecifier().enumeratorList() == null){
                CType enumType = new CType(typeContext.getText(), nameContext.getText());
                String enumName = nameContext.getText();
                return new CVariable(enumType,enumName);
            }
            /*
                Anonymous enum
             */
            else{
                List<String> enumMembers = new ArrayList<>();
                List<CParser.EnumeratorContext> enumContextList = typeContext
                        .enumSpecifier().enumeratorList().enumerator();
                for(CParser.EnumeratorContext enumContext : enumContextList){
                    enumMembers.add(enumContext.enumerationConstant().getText());
                }
                return new CEnum(nameContext.getText(), enumMembers);
            }
        }
    }

    @Override public void enterStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        /*
            Condition to filter out:
            - Instances of a struct or union
            - Anonymous structs or unions(defined in another struct or with typedef)
        */
        if(ctx.structDeclarationList() == null || ctx.Identifier() == null){
            return;
        }
        String structOrUnionName = ctx.Identifier().getText();
        List<CElement> structOrUnionBody = new ArrayList<>();
        List<CParser.StructDeclarationContext> structOrUnionBodyCtx =
                    ctx.structDeclarationList().structDeclaration();
        for(CParser.StructDeclarationContext context : structOrUnionBodyCtx){
            String memberType = context.specifierQualifierList().typeSpecifier().getText();
            String memberName = context.structDeclaratorList().getText();
            /*
             * Member has a bitfield, parser treats the member name differently
             */
            if(memberName.startsWith(":")){
                memberName = context.specifierQualifierList().specifierQualifierList().getText();
            }
            CElement tmpMember;
            if(memberType.startsWith("union") || memberType.startsWith("struct") || memberType.startsWith("enum")){
                tmpMember = getStructOrUnionBodyMember(context);
            }else{
                CType memberCType = new CType(memberType,memberName);
                tmpMember = new CVariable(memberCType,ListenerUtil.getVariableName(memberName));
            }
            structOrUnionBody.add(tmpMember);
        }
        this.structOrUnionList.add(new CStructOrUnion(structOrUnionName,structOrUnionBody));
    }

    public List<CFunction> getLocalFunctions() {
        return localFunctions;
    }

    public List<CStructOrUnion> getStructOrUnionList() {
        return structOrUnionList;
    }
}
