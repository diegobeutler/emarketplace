package br.edu.utfpr.emarketplace.exception;

import lombok.Getter;

@Getter
public class UsuarioJaExisteException extends Exception {

    private final String message;

    public UsuarioJaExisteException(String message) {
        this.message = message;
    }
}
