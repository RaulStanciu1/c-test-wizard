package com.ctestwizard.model.testentity;

import com.ctestwizard.model.entity.CElement;
import com.ctestwizard.model.entity.CFunction;

import java.util.Map;

public class TInterface {
    private Map<CElement, Passing> externalVariables;
    private Map<CElement, Passing> globalVariables;
    private Map<CElement, FunctionStatus> externalFunctions;
    private Map<CElement, FunctionStatus> localFunctions;
}
