package com.ctestwizard.model.coverage;


import com.ctestwizard.model.coverage.generated.CLexer;
import com.ctestwizard.model.coverage.generated.CParser;
import com.ctestwizard.model.test.entity.TObject;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CoverageInstrumenter {
    public static void instrumentObject(TObject testObject) throws Exception{
        //Create a copy of the preprocessed source file
        File sourceFile = new File(testObject.getParent().getTestDriver().getProjectPath()+File.separator+"ctw"+File.separator+"ctw_src_pre.c");
        File instrumentedFile = new File(testObject.getParent().getTestDriver().getProjectPath()+File.separator+"ctw"+File.separator+"ctw_src_pre_cov.c");
        if(!sourceFile.exists()){
            throw new Exception("Preprocessed source file not found");
        }
        if(!instrumentedFile.exists()) {
            if (!instrumentedFile.createNewFile()) {
                throw new Exception("Could not create instrumented file");
            }
        }

        FileUtils.copyFile(sourceFile, instrumentedFile);

        //Find the decisions in the source file
        CoverageListener listener = new CoverageListener(testObject);
        CharStream input = CharStreams.fromFileName(instrumentedFile.getAbsolutePath());
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        List<Decision> decisions = listener.getDecisions();

        //Instrument the source file with the decisions
        addDecisionsToFile(instrumentedFile, decisions);

        //Add the coverage library to the project
        addCoverageLibrary(testObject,decisions.size());
    }

    private static void addDecisionsToFile(File instrumentedFile, List<Decision> decisions) throws IOException {
        List<String> fileContent = FileUtils.readLines(instrumentedFile, "UTF-8");
        for(Decision decision : decisions){
            String decisionBlock = fileContent.get(decision.line()-1);
            decisionBlock = decisionBlock+"__CTW__DECISION__("+decision.id()+");";
            fileContent.set(decision.line()-1, decisionBlock);
        }

        FileUtils.writeLines(instrumentedFile, fileContent);
    }

    private static void addCoverageLibrary(TObject testObject,int decisions) throws Exception{
        String coverageLibraryPath = testObject.getParent().getTestDriver().getProjectPath()+File.separator+"ctw"
                +File.separator+"ctw_cov.c";
        File coverageLibrary = new File(coverageLibraryPath);
        if(!coverageLibrary.exists()){
            if(!coverageLibrary.createNewFile()){
                throw new Exception("Could not create coverage library file");
            }
        }
        String coverageLibraryContent = "#define __CTW__DECISIONS_SIZE__ "+decisions+"\n"+"""
                #ifndef __CTW__U8__
                #define __CTW__U8__ unsigned char
                #endif
                
                
                __CTW__U8__ __ctw__decisions__arr[__CTW__DECISIONS_SIZE__] = {0};
                
                #ifndef __CTW__DECISION__
                #define __CTW__DECISION__(id) __ctw__decisions__arr[id] = 1
                #endif
                
                #ifndef __CTW__GET__DECISION__
                #define __CTW__GET__DECISION__(id) (__ctw__decisions__arr[id])
                #endif
                
                double __ctw__coverage__percentage__()
                {
                    int i;
                    int percentage = 0;
                    for(i = 0; i < __CTW__DECISIONS_SIZE__; i++)
                    {
                        percentage += __CTW__GET__DECISION__(i);
                    }
                    if(__CTW__DECISIONS_SIZE__ == 0)
                    {
                        return 1.0;
                    }
                    return (double)percentage / __CTW__DECISIONS_SIZE__;
                }
                """ ;
        FileUtils.write(coverageLibrary, coverageLibraryContent, "UTF-8");
    }

}
