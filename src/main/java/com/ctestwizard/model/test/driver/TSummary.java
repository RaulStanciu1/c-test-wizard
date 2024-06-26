package com.ctestwizard.model.test.driver;

import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Entity used to store the summary of the test results after an execution
 */
public class TSummary {
    private final List<TResults> testResults;
    private Boolean resultsPassed;
    private final Integer coveragePassed; /*-1:no coverage  0: failed coverage  1: passed coverage*/
    private final Integer totalTestCases;
    private final Integer totalTestSteps;
    private final Integer passedTestSteps;

    /**
     * Constructor for the summary
     * @param testObject The test object
     * @param testResults The test results
     * @param coverageEnabled If coverage is enabled
     * @throws Exception If there were issues creating the summary
     */
    public TSummary(TObject testObject, List<TResults> testResults, boolean coverageEnabled) throws Exception{
        this.testResults = testResults;
        this.totalTestCases = testObject.getTestCases().size();
        int totalTestSteps = 0;
        int passedTestSteps = 0;
        double resultSignificance = testObject.getParent().getTestDriver().getResultSignificance();
        double coverageSignificance = testObject.getParent().getTestDriver().getCoverageSignificance();
        for(int i = 0; i < testObject.getTestCases().size();i++){
            int passedStepsCase = 0;
            int totalStepsCase = 0;
            CElement output = testResults.get(i).getOutput();
            List<CElement> outputGlobals = testResults.get(i).getGlobalOutputs();
            for(int j = 0; j < testObject.getTestCases().get(i).getTSteps();j++){
                totalTestSteps++;
                totalStepsCase++;
                if(testStepPassed(output,outputGlobals, j)){
                    passedStepsCase++;
                    passedTestSteps++;
                }
            }
            if(totalStepsCase != 0){
                double casePassedRatio = ((double)passedStepsCase/(double)totalStepsCase) * 100.0;
                if(casePassedRatio >= resultSignificance){
                    testResults.get(i).setResultsPassed(true);
                }
            }else{
                testResults.get(i).setResultsPassed(false);
            }
        }
        this.passedTestSteps = passedTestSteps;
        this.totalTestSteps = totalTestSteps;
        this.resultsPassed = true;
        for(TResults testResult : testResults){
            if(!testResult.getResultsPassed()){
                this.resultsPassed = false;
                break;
            }
        }
        if(coverageEnabled){
            String coverageResultFilePath = testObject.getParent().getTestDriver().getProjectPath()+File.separator+"ctw"+File.separator+"cov_results.txt";
            File coverageResultFile = new File(coverageResultFilePath);
            if(!coverageResultFile.exists()){
                throw new Exception("Coverage results not found");
            }
            List<String> coverageResults = FileUtils.readLines(coverageResultFile, "UTF-8");
            double coverageRatio = Double.parseDouble(coverageResults.get(0));
            if(coverageRatio * 100 >= coverageSignificance) {
                this.coveragePassed = 1;
            }else{
                this.coveragePassed = 0;
            }
        }else{
            this.coveragePassed = -1;
        }
    }

    /**
     * Private method to check if a certain test step is considered passed
     * @param element The element to check
     * @param globalOutputs The global outputs
     * @param testStep The test step
     * @return True if the test step is considered passed, false otherwise
     */
    private boolean testStepPassed(CElement element,List<CElement> globalOutputs, int testStep){
        if(!elementPassed(element,testStep)){
            return false;
        }
        for(CElement globalOutput : globalOutputs){
            if(!elementPassed(globalOutput,testStep)){
                return false;
            }
        }
        return true;
    }

    /**
     * Helper Private method to check if a certain element is considered passed
     * @param element The element to check
     * @param testStep The test step
     * @return True if the element is considered passed, false otherwise
     */
    private boolean elementPassed(CElement element, int testStep){
        if(element instanceof CVariable variable){
            return variable.values.get(testStep).valueStatus != -1;
        }else if(element instanceof CEnumInstance enumInstance) {
            return enumInstance.values.get(testStep).valueStatus != -1;
        }else if(element instanceof CStructOrUnionInstance structOrUnionInstance) {
            if(structOrUnionInstance.getPointers() ==0){
                for(CElement structElement : structOrUnionInstance.getStructType().getMembers()){
                    if(!elementPassed(structElement,testStep)){
                        return false;
                    }
                }
            }else{
                return structOrUnionInstance.values.get(testStep).valueStatus != -1;
            }
        }else if(element instanceof CArray array){
            for(CElement arrayElement : array.getArrayMembers()){
                if(!elementPassed(arrayElement,testStep)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the number of passed test steps
     * @return The number of passed test steps
     */
    public Integer getPassedTestSteps() {
        return passedTestSteps;
    }

    /**
     * Get the total number of test steps
     * @return The total number of test steps
     */
    public Integer getTotalTestSteps() {
        return totalTestSteps;
    }

    /**
     * Get the total number of test cases
     * @return The total number of test cases
     */
    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    /**
     * Get the coverage passed
     * @return The coverage passed
     */
    public Integer getCoveragePassed() {
        return coveragePassed;
    }

    /**
     * Get the results passed
     * @return The results passed
     */
    public Boolean getResultsPassed() {
        return resultsPassed;
    }

    /**
     * Get the test results
     * @return The test results
     */
    public List<TResults> getTestResults() {
        return testResults;
    }
}
