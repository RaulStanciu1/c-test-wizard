package com.ctestwizard.model.test.driver;

/**
 * Interface used to redirect the output of a process
 */
@FunctionalInterface
public interface ConsoleWriter {
    void redirectOutput(Process process);
}
