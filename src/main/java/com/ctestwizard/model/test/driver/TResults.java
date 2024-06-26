package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.CElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity used to store all the results generated from a test after execution(actual results)
 */
public class TResults implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private final CElement output;
    private final List<CElement> globalOutputs;
    private Boolean resultsPassed;
    private final Integer testSteps;

    /**
     * Constructor for the results
     * @param output The output of the test
     * @param globalOutputs The global outputs of the test
     * @param testSteps The number of test steps
     */
    public TResults(CElement output, List<CElement> globalOutputs, Integer testSteps){
        this.output = output;
        this.globalOutputs = globalOutputs;
        resultsPassed = false;
        this.testSteps = testSteps;
    }

    /**
     * Get the output of the test
     * @return The output of the test
     */
    public CElement getOutput() {
        return output;
    }

    /**
     * Get the global outputs of the test
     * @return The global outputs of the test
     */
    public List<CElement> getGlobalOutputs() {
        return globalOutputs;
    }

    /**
     * Get if the results passed
     * @return If the results passed
     */
    public Boolean getResultsPassed() {
        return resultsPassed;
    }

    /**
     * Set if the results passed
     * @param resultsPassed If the results passed
     */
    public void setResultsPassed(Boolean resultsPassed) {
        this.resultsPassed = resultsPassed;
    }

    /**
     * Get the number of test steps
     * @return The number of test steps
     */
    public Integer getTestSteps() {
        return testSteps;
    }
}
