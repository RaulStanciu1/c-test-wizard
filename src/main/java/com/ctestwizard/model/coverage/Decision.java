package com.ctestwizard.model.coverage;

public record Decision(int id, int line) {

    public String toString() {
        return "Decision " + id + " at line " + line;
    }
}
