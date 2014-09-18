package org.hubiquitus.hapi.transport.exception;

/**
 * Transport exception
 *
 * @author teabow
 */
public class TransportException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 23853866382567757L;

    /**
     * Constructor
     *
     * @param message the exception message
     */
    public TransportException(String message) {
        super(message);
    }

}
