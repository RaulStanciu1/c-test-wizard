package com.ctestwizard.model.testentity;

import com.ctestwizard.model.cparser.CParserImpl;
import com.ctestwizard.model.entity.CFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TObject {
    private final CFunction testFunction;
    private TInterface testInterface;
    private List<TCase> testCases;
    public TObject(CFunction testFunction){
        this.testFunction = testFunction;
    }
    public static TObject newTObject(CParserImpl parser, CFunction testFunction)
                                            throws CloneNotSupportedException {
        CParserImpl newParser = parser.clone();
        CFunction newTestFunction = testFunction.clone();
        TObject newTestObject = new TObject(newTestFunction);
        List<CFunction> localFunctions = newParser.getLocalFunctionDefinitions();
        Iterator<CFunction> iterator = localFunctions.iterator();
        while(iterator.hasNext()){
            CFunction localFunction = iterator.next();
            if(localFunction.getName().strip().equals(newTestFunction.getName().strip())){
                iterator.remove();
                break;
            }
        }
        TInterface newTestObjectInterface = new TInterface(newParser.getExternalFunctionDefinitions(),newParser.getLocalFunctionDefinitions(),newParser.getGlobals(),newTestFunction.getParameters(),newTestFunction.getType());
        List<TCase> newTestCases = new ArrayList<>();
        newTestCases.add(TCase.newTCase(newTestObject));
        newTestObject.setTestCases(newTestCases);
        newTestObject.setTestInterface(newTestObjectInterface);
        return newTestObject;
    }

    public CFunction getTestFunction() {
        return testFunction;
    }

    public TInterface getTestInterface() {
        return testInterface;
    }

    public void setTestInterface(TInterface testInterface) {
        this.testInterface = testInterface;
    }

    public List<TCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TCase> testCases) {
        this.testCases = testCases;
    }
}
