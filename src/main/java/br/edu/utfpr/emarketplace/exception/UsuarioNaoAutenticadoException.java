package br.edu.utfpr.emarketplace.exception;

import lombok.Getter;

@Getter
public class UsuarioNaoAutenticadoException extends Exception {

    private final String message;

    public UsuarioNaoAutenticadoException(String message) {
        super(message);
        this.message = message;
    }
}
