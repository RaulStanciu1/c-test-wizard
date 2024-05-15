package com.ctestwizard.model.coverage;

import com.ctestwizard.model.coverage.generated.CBaseListener;
import com.ctestwizard.model.coverage.generated.CParser;
import com.ctestwizard.model.test.entity.TObject;

import java.util.ArrayList;
import java.util.List;

public class CoverageListener extends CBaseListener {
    private final List<Decision> decisions = new ArrayList<>();
    private final TObject testObject;
    private boolean insideFunction = false;
    private boolean insideDecision = false;
    public CoverageListener(TObject testObject){
        this.testObject = testObject;
    }

    @Override public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String functionName = ctx.declarator().directDeclarator().directDeclarator().getText();
        String objectName = testObject.getTestFunction().getName();
        if(functionName.equals(objectName)){
            insideFunction = true;
        }
    }

    @Override public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        insideFunction = false;
    }

    @Override public void enterSelectionStatement(CParser.SelectionStatementContext ctx) {
        if(insideFunction && ctx.If() != null){
            insideDecision = true;
        }
    }

    @Override public void enterCompoundStatement(CParser.CompoundStatementContext ctx) {
        if(insideFunction && insideDecision){
            int line = ctx.getStart().getLine();
            decisions.add(new Decision(decisions.size(), line));
        }
    }

    @Override public void exitSelectionStatement(CParser.SelectionStatementContext ctx) {
        if(insideFunction){
            insideDecision = false;
        }
    }

    public List<Decision> getDecisions() {
        return decisions;
    }
}
