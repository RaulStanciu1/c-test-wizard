package com.ctestwizard.model.cparser;

import com.ctestwizard.model.cparser.generated.CLexer;
import com.ctestwizard.model.cparser.generated.CParser;
import com.ctestwizard.model.entity.CStructOrUnion;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;

public class CParserImpl {
    public static List<CStructOrUnion> getStructAndUnions(String filePath) throws IOException {
        CharStream input = CharStreams.fromFileName(filePath);
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        CListenerImpl listener = new CListenerImpl();
        walker.walk(listener, tree);
        return listener.getStructOrUnionList();
    }

    public static void main(String[] args) throws IOException {
        List<CStructOrUnion> list = getStructAndUnions("C:/Users/Raul/Desktop/test.c");
        for(CStructOrUnion structOrUnion : list){
            System.out.println(structOrUnion);
        }
    }
}
