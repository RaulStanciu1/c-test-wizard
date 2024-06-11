package com.ctestwizard.view.entity;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.test.entity.TObject;
import javafx.scene.image.Image;

import java.util.Objects;

public class TObjectTable {
    private final TObject testObject;
    private Integer rsStatus; /* -1: no status 0: failed 1: passed */
    private Integer covStatus; /* -1: no status 0: not covered 1: covered */
    public TObjectTable(TObject testObject) {
        this.testObject = testObject;
        this.rsStatus = -1;
        this.covStatus = -1;
    }

    public TObject getTestObject() {
        return testObject;
    }

    public Image getResultImage() {
        Image image = null;
        if(rsStatus == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(rsStatus == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    public Integer getRsStatus() {
        return rsStatus;
    }

    public void setRsStatus(Integer status) {
        this.rsStatus = status;
    }
    public Image getCoverageImage() {
        Image image = null;
        if(covStatus == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(covStatus == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    public Integer getCovStatus() {
        return covStatus;
    }

    public void setCovStatus(Integer status) {
        this.covStatus = status;
    }


}
