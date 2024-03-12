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
import java.util.List;

public class CParserImpl {
    private final CListenerImpl listener;
    private final String filePath;

    public CParserImpl(String filePath) throws IOException {
        this.filePath = filePath;
        this.listener = new CListenerImpl();
        CharStream input = CharStreams.fromFileName(filePath);
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this.listener, tree);
    }

    public String getFilePath() {
        return filePath;
    }

    public List<CStructOrUnion> getStructAndUnionDefinitions() {
        return this.listener.getStructOrUnionList();
    }

    public List<CEnum> getEnumDefinitions() {
        return this.listener.getEnumList();
    }
    public List<CFunction> getLocalFunctionDefinitions(){
        return this.listener.getLocalFunctions();
    }

    public List<CFunction> getExternalFunctionDefinitions(){
        return this.listener.getExternalFunctions();
    }
    public List<CVariable> getGlobals(){
        return this.listener.getGlobals();
    }

    public List<CVariable> getExternalGlobals(){
        return this.listener.getExternalGlobals();
    }
    public static void main(String[] args) throws IOException {
        CParserImpl parser = new CParserImpl("C:\\Users\\Raul\\Desktop\\test.c");
        System.out.println("LOCAL GLOBALS");
        System.out.println(parser.getGlobals());
        System.out.println("EXTERNAL GLOBALS");
        System.out.println(parser.getExternalGlobals());
        System.out.println("EXTERNAL FUNCTIONS");
        System.out.println(parser.getExternalFunctionDefinitions());
        System.out.println("LOCAL FUNCTIONS");
        System.out.println(parser.getLocalFunctionDefinitions());
        System.out.println("ENUM DEFINITIONS");
        System.out.println(parser.getEnumDefinitions());
        System.out.println("STRUCT OR UNION DEFINITIONS");
        System.out.println(parser.getStructAndUnionDefinitions());
    }


}
