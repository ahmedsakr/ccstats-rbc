package com.rbccca.exceptions;


/**
 *
 * @author Ahmed Sakr
 * @since November 29, 2015
 */
public class InvalidStatementPathException extends Exception {


    /**
     * Default Exception constructor.
     *
     * @param error The error that triggered a InvalidStatementPathException.
     */
    public InvalidStatementPathException(String error) {
        super(error);
    }
}
