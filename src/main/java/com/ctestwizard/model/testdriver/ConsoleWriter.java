package com.ctestwizard.model.testdriver;

@FunctionalInterface
public interface ConsoleWriter {
    void redirectOutput(Process process);
}
