package br.edu.utfpr.emarketplace.enumeration;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Operacao {
    DOACAO_PRODUTO("Doação do Produto"),
    DOACAO_VALOR("Doação do Valor"),
    EMPRESTIMO("Empréstimo"),
    TROCA("Troca"),
    VENDA("Venda");

    private String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
