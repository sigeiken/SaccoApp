package io.kentec.SaccoMobile.exceptions;

public class RollBackException extends RuntimeException{
    public RollBackException() {
    }

    public RollBackException(String message) {
        super(message);
    }
}
