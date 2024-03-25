package com.ctestwizard.model.testentity;

import java.util.ArrayList;
import java.util.List;

public class TCase {
    private final TObject parent;
    private String title;
    private String description;
    private final List<TStep> tSteps;
    public TCase(TObject parent) throws CloneNotSupportedException {
        this.parent = parent;
        this.title="";
        this.description="";
        this.tSteps = new ArrayList<>();
        this.tSteps.add(TStep.newTStep(this));
    }

    public static TCase newTCase(TObject parent){
        try{
            return new TCase(parent);
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public TObject getParent() {
        return parent;
    }

    public List<TStep> getTSteps() {
        return tSteps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
