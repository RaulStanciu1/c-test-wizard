package com.ctestwizard.model.cparser;

import com.ctestwizard.model.cparser.generated.CLexer;
import com.ctestwizard.model.cparser.generated.CParser;
import com.ctestwizard.model.entity.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CParserImpl implements Cloneable {
    private CListenerImpl listener;
    private String filePath;

    public CParserImpl(String filePath){
        this.listener = new CListenerImpl();
        this.filePath = filePath;
    }

    public void walkParseTree() throws IOException{
        CharStream input = CharStreams.fromFileName(filePath);
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this.listener, tree);

        finalizeData();
    }

    public CParserImpl clone() throws CloneNotSupportedException {
        CParserImpl clone = (CParserImpl) super.clone();
        clone.filePath = this.filePath;
        clone.listener = this.listener.clone();
        return clone;
    }

    public List<CElement> getStructAndUnionDefinitions() {
        return this.listener.getStructOrUnionList();
    }

    public List<CElement> getEnumDefinitions() {
        return this.listener.getEnumList();
    }
    public List<CFunction> getLocalFunctionDefinitions(){
        return this.listener.getLocalFunctions();
    }

    public List<CFunction> getExternalFunctionDefinitions(){
        return this.listener.getExternalFunctions();
    }
    public List<CElement> getGlobals(){
        return this.listener.getGlobals();
    }

    private void finalizeData(){
        finalizeStructOrUnions();
        finalizeLocalGlobals();
        finalizeExternalFunctions();
        finalizeLocalFunctions();
    }

    /**
     *  Method that replaces every instance of a struct in another struct to its respective object
     */
    private void finalizeStructOrUnions(){
        for(CElement _structOrUnion : this.getStructAndUnionDefinitions()){
            CStructOrUnion structOrUnion = (CStructOrUnion)_structOrUnion;
            int index = 0;
            for(CElement member : structOrUnion.getMembers()){
                if(member instanceof CVariable structMember) {
                    if(CParserUtil.isDefinedStructOrUnion(structMember.getType(),this.getStructAndUnionDefinitions())){
                        structOrUnion.setMember(CParserUtil.convertVariableToStructOrUnionInstance(structMember,this.getStructAndUnionDefinitions()),index);
                    }else if(CParserUtil.isEnum(structMember.getType(),this.getEnumDefinitions())){
                        structOrUnion.setMember(CParserUtil.convertVariableToEnum(structMember,this.getEnumDefinitions()),index);
                    }
                }
                index ++;
            }
        }
    }

    private void finalizeLocalGlobals(){
        int index = 0;
        for(CElement _localGlobal:this.getGlobals()){
            if(_localGlobal instanceof CVariable global){
                if(CParserUtil.isDefinedStructOrUnion(global.getType(),this.getStructAndUnionDefinitions())){
                    this.getGlobals().set(index,CParserUtil.convertVariableToStructOrUnionInstance(global,this.getStructAndUnionDefinitions()));
                }else if(CParserUtil.isEnum(global.getType(),this.getEnumDefinitions())){
                    this.getGlobals().set(index,CParserUtil.convertVariableToEnum(global,this.getEnumDefinitions()));
                }
            }
            index++;
        }
    }

    private void finalizeExternalFunctions(){
        //Step 1: Remove any external function that's also in the localFunction list
        Iterator<CFunction> iterator = this.getExternalFunctionDefinitions().iterator();
        while (iterator.hasNext()) {
            CFunction obj1 = iterator.next();
            for (CFunction obj2 : this.getLocalFunctionDefinitions()) {
                if (obj1.getName().stripIndent().equals(obj2.getName().stripIndent())) {
                    iterator.remove();
                    break;
                }
            }
        }

        //Step 2: Convert any struct, union or enum parameter to its respective object
        int externFunctionIndex = 0;
        for(CFunction externalFunction : this.getExternalFunctionDefinitions()){
            List<CElement> parameterList = externalFunction.getParameters();
            int parameterIndex = 0;
            for(CElement _parameter : parameterList){
                if(_parameter instanceof CVariable parameter){
                    if(CParserUtil.isDefinedStructOrUnion(parameter.getType(),this.getStructAndUnionDefinitions())){
                        parameterList.set(parameterIndex,CParserUtil.convertVariableToStructOrUnionInstance(parameter,this.getStructAndUnionDefinitions()));
                    }
                    else if (CParserUtil.isEnum(parameter.getType(),this.getEnumDefinitions())){
                        parameterList.set(parameterIndex,CParserUtil.convertVariableToEnum(parameter,this.getEnumDefinitions()));
                    }
                }

                parameterIndex++;
            }
            externalFunction.setParameters(parameterList);
            this.getExternalFunctionDefinitions().set(externFunctionIndex,externalFunction);
            externFunctionIndex++;
        }
        //Step 3: Convert the return type to its respective object
        for(CFunction externalFunction : this.getExternalFunctionDefinitions()){
            CElement returnType = externalFunction.getType();
            if(CParserUtil.isDefinedStructOrUnion((CType)returnType,this.getStructAndUnionDefinitions())){
                returnType = CParserUtil.convertTypeToStructOrUnion((CType)returnType,this.getStructAndUnionDefinitions());
            }else if(CParserUtil.isEnum((CType)returnType,this.getEnumDefinitions())){
                returnType = CParserUtil.convertTypeToEnum((CType)returnType,this.getEnumDefinitions());
            }
            externalFunction.setType(returnType);
        }
    }

    private void finalizeLocalFunctions(){
        int index = 0;
        for(CFunction localFunction : this.getLocalFunctionDefinitions()){
            List<CElement> parameterList = localFunction.getParameters();
            int parameterIndex = 0;
            for(CElement _parameter : parameterList){
                if(_parameter instanceof CVariable parameter){
                    if(CParserUtil.isDefinedStructOrUnion(parameter.getType(),this.getStructAndUnionDefinitions())){
                        parameterList.set(parameterIndex,CParserUtil.convertVariableToStructOrUnionInstance(parameter,this.getStructAndUnionDefinitions()));
                    }
                    else if (CParserUtil.isEnum(parameter.getType(),this.getEnumDefinitions())){
                        parameterList.set(parameterIndex,CParserUtil.convertVariableToEnum(parameter,this.getEnumDefinitions()));
                    }
                }

                parameterIndex++;
            }
            localFunction.setParameters(parameterList);
            this.getLocalFunctionDefinitions().set(index,localFunction);
            index++;

        }

        for(CFunction localFunction : this.getLocalFunctionDefinitions()){
            CElement returnType = localFunction.getType();
            if(CParserUtil.isDefinedStructOrUnion((CType)returnType,this.getStructAndUnionDefinitions())){
                returnType = CParserUtil.convertTypeToStructOrUnion((CType)returnType,this.getStructAndUnionDefinitions());
            }else if(CParserUtil.isEnum((CType)returnType,this.getEnumDefinitions())){
                returnType = CParserUtil.convertTypeToEnum((CType)returnType,this.getEnumDefinitions());
            }
            localFunction.setType(returnType);
        }
    }
}
