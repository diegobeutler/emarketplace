package br.edu.utfpr.emarketplace.exception;

import lombok.Getter;

@Getter
public class RecursoNaoPermitidoException extends Exception {

    private final String message;

    public RecursoNaoPermitidoException(String message) {
        super(message);
        this.message = message;
    }
}
