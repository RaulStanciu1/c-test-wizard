package com.ctestwizard.model.exception;

/**
 * Special exception used when new and significant changes to the source file have been detected
 */
public class InterfaceChangedException extends Exception{
    /**
     * Constructor for the exception
     * @param message The message of the exception
     */
    public InterfaceChangedException(String message){
        super(message);
    }
}
