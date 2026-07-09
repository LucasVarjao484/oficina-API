package com.example.oficina.exception;

public class AcaoNaoPermitidaException extends RuntimeException {
    public AcaoNaoPermitidaException(String message) {
        super(message);
    }
}
