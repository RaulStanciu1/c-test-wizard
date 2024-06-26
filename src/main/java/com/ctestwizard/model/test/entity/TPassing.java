package com.ctestwizard.model.test.entity;

import java.io.Serializable;

/**
 * Enum used for representing the relevancy(passing) of a global variable in the test object
 */
public enum TPassing implements Serializable {
    IN,OUT,INOUT,NONE;
    private static final long serialVersionUID = 4L;
}
