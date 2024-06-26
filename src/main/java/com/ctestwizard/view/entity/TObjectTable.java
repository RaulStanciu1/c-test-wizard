package com.ctestwizard.view.entity;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.test.entity.TObject;
import javafx.scene.image.Image;

import java.util.Objects;


/**
 * Entity used for displaying the test object in a table format in the GUI
 */
public class TObjectTable {
    private final TObject testObject;
    private Integer rsStatus; /* -1: no status 0: failed 1: passed */
    private Integer covStatus; /* -1: no status 0: not covered 1: covered */

    /**
     * Constructor for the test object table
     * @param testObject The test object
     */
    public TObjectTable(TObject testObject) {
        this.testObject = testObject;
        this.rsStatus = -1;
        this.covStatus = -1;
    }

    /**
     * Get the test object
     * @return The test object
     */
    public TObject getTestObject() {
        return testObject;
    }

    /**
     * Get the result status image
     * @return The result status image
     */
    public Image getResultImage() {
        Image image = null;
        if(getRsStatus() == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(getRsStatus() == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    /**
     * Get the result status
     * @return The result status
     */
    public Integer getRsStatus() {
        return rsStatus;
    }

    /**
     * Set the result status
     * @param status The result status
     */
    public void setRsStatus(Integer status) {
        this.rsStatus = status;
    }

    /**
     * Get the coverage status image
     * @return The coverage status image
     */
    public Image getCoverageImage() {
        Image image = null;
        if(getCovStatus() == 0) {
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/failed.png")));
        }else if(getCovStatus() == 1){
            image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("img/passed.png")));
        }
        return image;
    }

    /**
     * Get the coverage status
     * @return The coverage status
     */
    public Integer getCovStatus() {
        return covStatus;
    }

    /**
     * Set the coverage status
     * @param status The coverage status
     */
    public void setCovStatus(Integer status) {
        this.covStatus = status;
    }


}
