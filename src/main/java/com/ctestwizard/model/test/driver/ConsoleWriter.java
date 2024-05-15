package com.ctestwizard.model.test.driver;

@FunctionalInterface
public interface ConsoleWriter {
    void redirectOutput(Process process);
}
