package br.edu.utfpr.emarketplace.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends Exception {

    private final String message;

    public InvalidTokenException(String message) {
        this.message = message;
    }
}
