package com.ctestwizard.view.entity;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.test.entity.TCase;
import javafx.scene.image.Image;

import java.util.Objects;

public class TCaseTable {
    private final TCase testCase;
    private Integer resultStatus; /* -1: no status 0: failed 1: passed */
    private Integer coverageStatus; /* -1: no status 0: not covered 1: covered */
    public TCaseTable(TCase testCase) {
        this.testCase = testCase;
        this.resultStatus = -1;
        this.coverageStatus = -1;
    }

    public TCase getTestCase() {
        return testCase;
    }

    public Integer getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Integer resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Integer getCoverageStatus() {
        return coverageStatus;
    }

    public void setCoverageStatus(Integer coverageStatus) {
        this.coverageStatus = coverageStatus;
    }

    public Image getResultImage() {
        Image image = null;
        if(resultStatus == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(resultStatus == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    public Image getCoverageImage() {
        Image image = null;
        if(coverageStatus == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(coverageStatus == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

}
