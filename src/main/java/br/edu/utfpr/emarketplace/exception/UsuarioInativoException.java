package br.edu.utfpr.emarketplace.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class UsuarioInativoException extends AuthenticationException {

    private final String message;

    public UsuarioInativoException(String message) {
        super(message);
        this.message = message;
    }
}
