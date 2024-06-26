package com.ctestwizard.view.entity;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.test.entity.TCase;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Entity used to display the test case in a table format in the GUI
 */
public class TCaseTable {
    private final TCase testCase;
    private Integer resultStatus; /* -1: no status 0: failed 1: passed */
    private Integer coverageStatus; /* -1: no status 0: not covered 1: covered */

    /**
     * Constructor for the test case table
     * @param testCase The test case
     */
    public TCaseTable(TCase testCase) {
        this.testCase = testCase;
        this.resultStatus = -1;
        this.coverageStatus = -1;
    }

    /**
     * Get the test case
     * @return The test case
     */
    public TCase getTestCase() {
        return testCase;
    }

    /**
     * Get the result status image
     * @return The result status image
     */
    public Integer getResultStatus() {
        return resultStatus;
    }

    /**
     * Set the result status
     * @param resultStatus The result status
     */
    public void setResultStatus(Integer resultStatus) {
        this.resultStatus = resultStatus;
    }

    /**
     * Get the coverage status
     * @return The coverage status
     */
    public Integer getCoverageStatus() {
        return coverageStatus;
    }

    /**
     * Set the coverage status
     * @param coverageStatus The coverage status
     */
    public void setCoverageStatus(Integer coverageStatus) {
        this.coverageStatus = coverageStatus;
    }

    /**
     * Get the result image
     * @return The result image
     */
    public Image getResultImage() {
        Image image = null;
        if(getResultStatus() == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(getResultStatus() == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    /**
     * Get the coverage image
     * @return The coverage image
     */
    public Image getCoverageImage() {
        Image image = null;
        if(getCoverageStatus() == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(getCoverageStatus() == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

}
