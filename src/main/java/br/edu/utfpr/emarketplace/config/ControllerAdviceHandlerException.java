package br.edu.utfpr.emarketplace.config;

import br.edu.utfpr.emarketplace.exception.InvalidTokenException;
import br.edu.utfpr.emarketplace.exception.UsuarioJaExisteException;
import io.micrometer.core.lang.NonNullApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdviceHandlerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UsuarioJaExisteException.class)
    public ResponseEntity<ErrorResponse> usuarioJaExiste(UsuarioJaExisteException exception) {
        var errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>((errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidToke(InvalidTokenException exception) {
        var errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>((errorResponse), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        var errorResponse = new ErrorResponse("Erro interno no servidor");
        return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
}
