package br.edu.utfpr.emarketplace.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {
    DISPONIVEL("Disponível"),
    EM_NEGOCIACAO("Em Negociação"),
    FINALIZADO("Finalizado");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

}
