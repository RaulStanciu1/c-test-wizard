package com.ctestwizard.model.code.parser;

import com.ctestwizard.model.code.parser.generated.CLexer;
import com.ctestwizard.model.code.parser.generated.CParser;
import com.ctestwizard.model.code.entity.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Class used to detect the elements in a C source file
 */
public class CParserDetector implements Cloneable {
    private CListenerDetector listener;
    private String filePath;

    /**
     * Constructor for the detector
     * @param filePath The path to the source file
     */
    public CParserDetector(String filePath){
        this.listener = new CListenerDetector();
        this.filePath = filePath;
    }

    /**
     * Method that walks the parse tree and detects the elements in the source file
     * @throws IOException If the source file could not be read
     */
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

    /**
     * Method that clones the detector
     * @return The cloned detector
     */
    public CParserDetector clone(){
        try {
            CParserDetector clone = (CParserDetector) super.clone();
            clone.filePath = this.filePath;
            clone.listener = this.listener.clone();
            return clone;
        }catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Method that returns the list of struct and union definitions
     * @return The list of struct and union definitions
     */
    public List<CElement> getStructAndUnionDefinitions() {
        return this.listener.getStructOrUnionList();
    }

    /**
     * Method that returns the list of enum definitions
     * @return The list of enum definitions
     */
    public List<CElement> getEnumDefinitions() {
        return this.listener.getEnumList();
    }

    /**
     * Method that returns the list of local function definitions
     * @return The list of local function definitions
     */
    public List<CFunction> getLocalFunctionDefinitions(){
        return this.listener.getLocalFunctions();
    }

    /**
     * Method that returns the list of external function definitions
     * @return The list of external function definitions
     */
    public List<CFunction> getExternalFunctionDefinitions(){
        return this.listener.getExternalFunctions();
    }

    /**
     * Method that returns the list of global variables
     * @return The list of global variables
     */
    public List<CElement> getGlobals(){
        return this.listener.getGlobals();
    }

    /**
     * Method that does additional post-processing to ensure that the data is correct
     */
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
                        CElement instanceMember = CParserUtil.convertVariableToStructOrUnionInstance(structMember,this.getStructAndUnionDefinitions());
                        if(instanceMember.getName().endsWith("]")){
                            instanceMember = new CArray(instanceMember);
                        }
                        structOrUnion.setMember(instanceMember,index);
                    }else if(CParserUtil.isEnum(structMember.getType(),this.getEnumDefinitions())) {
                        CElement instanceMember = CParserUtil.convertVariableToEnum(structMember, this.getEnumDefinitions());
                        if(instanceMember.getName().endsWith("]")){
                            instanceMember = new CArray(instanceMember);
                        }
                        structOrUnion.setMember(instanceMember, index);
                    }else if(structMember.getName().endsWith("]")){
                        structOrUnion.setMember(new CArray(structMember),index);
                    }
                }
                index ++;
            }
        }

    }

    /**
     * Method that replaces every instance of a struct, union, enum or array in a global variable to its respective object
     */
    private void finalizeLocalGlobals(){
        int index = 0;
        for(CElement _localGlobal:this.getGlobals()){
            if(_localGlobal instanceof CVariable global){
                if(CParserUtil.isDefinedStructOrUnion(global.getType(),this.getStructAndUnionDefinitions())){
                    CElement instance = CParserUtil.convertVariableToStructOrUnionInstance(global,this.getStructAndUnionDefinitions());
                    if(instance.getName().endsWith("]")){
                        instance = new CArray(instance);
                    }
                    this.getGlobals().set(index,instance);
                }else if(CParserUtil.isEnum(global.getType(),this.getEnumDefinitions())){
                    CElement instance = CParserUtil.convertVariableToEnum(global,this.getEnumDefinitions());
                    if(instance.getName().endsWith("]")){
                        instance = new CArray(instance);
                    }
                    this.getGlobals().set(index,instance);
                }
            }
            index++;
        }
    }

    /**
     * Method that applies the finalization to the external functions
     */
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
            String returnType = externalFunction.getStrType();
            CElement returnTypeEl = null;
            if(CParserUtil.isDefinedStructOrUnion(returnType,this.getStructAndUnionDefinitions())){
                returnTypeEl = CParserUtil.convertTypeToStructOrUnionInstance(externalFunction.getName(),returnType,this.getStructAndUnionDefinitions());
            }else if(CParserUtil.isEnum(returnType,this.getEnumDefinitions())){
                returnTypeEl = CParserUtil.convertTypeToEnumInstance(externalFunction.getName(),returnType,this.getEnumDefinitions());
            }else{
                returnTypeEl = new CVariable(returnType, externalFunction.getName()+"()");
            }
            externalFunction.setRetType(returnTypeEl);
        }
    }

    /**
     * Method that applies the finalization to the local functions
     */
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
            String returnType = localFunction.getStrType();
            CElement returnTypeEl = null;
            if(CParserUtil.isDefinedStructOrUnion(returnType,this.getStructAndUnionDefinitions())){
                returnTypeEl = CParserUtil.convertTypeToStructOrUnionInstance(localFunction.getName(), returnType,this.getStructAndUnionDefinitions());
            }else if(CParserUtil.isEnum(returnType,this.getEnumDefinitions())){
                returnTypeEl = CParserUtil.convertTypeToEnumInstance(localFunction.getName(), returnType,this.getEnumDefinitions());
            }else{
                returnTypeEl = new CVariable(returnType, localFunction.getName()+"()");
            }
            localFunction.setRetType(returnTypeEl);
        }
    }
}
