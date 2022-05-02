package org.tyto.square.exception;

public class SquareAuthorizationException extends Exception {
    public SquareAuthorizationException() {
        super("The application is not authorized.");
    }
}
