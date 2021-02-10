package io.kentec.SaccoMobile.exceptions;

public class NonRollBackException extends Exception{

    public NonRollBackException() {
    }

    public NonRollBackException(String message) {
        super(message);
    }
}
