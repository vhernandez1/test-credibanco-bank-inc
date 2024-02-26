package com.credibanco.testbankinc.exception;

public class CardRuntimeException extends RuntimeException{

    public CardRuntimeException() {
        super();
    }

    public CardRuntimeException(String message) {
        super(message);
    }

    public CardRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardRuntimeException(Throwable cause) {
        super(cause);
    }
}
