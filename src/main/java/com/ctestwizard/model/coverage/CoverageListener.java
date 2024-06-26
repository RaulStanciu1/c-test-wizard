package com.ctestwizard.model.coverage;

import com.ctestwizard.model.coverage.generated.CBaseListener;
import com.ctestwizard.model.coverage.generated.CParser;
import com.ctestwizard.model.test.entity.TObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener used to extract the decisions from the source file
 */
public class CoverageListener extends CBaseListener {
    private final List<Decision> decisions = new ArrayList<>();
    private final TObject testObject;
    private boolean insideFunction = false;
    private boolean insideDecision = false;

    /**
     * Constructor for the listener
     * @param testObject The test object
     */
    public CoverageListener(TObject testObject){
        this.testObject = testObject;
    }

    /**
     * {@inheritDoc}
     * @param ctx the parse tree
     */
    @Override public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String functionName = ctx.declarator().directDeclarator().directDeclarator().getText();
        String objectName = testObject.getTestFunction().getName();
        if(functionName.equals(objectName)){
            insideFunction = true;
        }
    }

    /**
     * {@inheritDoc}
     * @param ctx the parse tree
     */
    @Override public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        insideFunction = false;
    }

    /**
     * {@inheritDoc}
     * @param ctx the parse tree
     */
    @Override public void enterSelectionStatement(CParser.SelectionStatementContext ctx) {
        if(insideFunction && ctx.If() != null){
            insideDecision = true;
        }
    }

    /**
     * {@inheritDoc}
     * @param ctx the parse tree
     */
    @Override public void enterCompoundStatement(CParser.CompoundStatementContext ctx) {
        if(insideFunction && insideDecision){
            int line = ctx.getStart().getLine();
            decisions.add(new Decision(decisions.size(), line));
        }
    }

    /**
     * {@inheritDoc}
     * @param ctx the parse tree
     */
    @Override public void exitSelectionStatement(CParser.SelectionStatementContext ctx) {
        if(insideFunction){
            insideDecision = false;
        }
    }

    /**
     * Get the decisions found in the source file
     * @return The decisions found
     */
    public List<Decision> getDecisions() {
        return decisions;
    }
}
