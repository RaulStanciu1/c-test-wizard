package com.ctestwizard.model.coverage;

/**
 * Entity used to store a decision in the source file
 * @param id decision id
 * @param line line number of the decision in the source file
 */
public record Decision(int id, int line) {

    public String toString() {
        return "Decision " + id + " at line " + line;
    }
}
