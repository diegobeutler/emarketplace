package br.edu.utfpr.emarketplace.repository.criteria.params;


import br.edu.utfpr.emarketplace.enumeration.Operacao;
import br.edu.utfpr.emarketplace.enumeration.Status;
import br.edu.utfpr.emarketplace.model.Categoria;
import br.edu.utfpr.emarketplace.model.Cidade;
import br.edu.utfpr.emarketplace.model.Estado;
import br.edu.utfpr.emarketplace.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class AnuncioFilter  implements Serializable {

    private Long id;
    private String titulo;
    private String descricao;
    private Operacao operacao;
    private Status status;
    private Usuario autor;
    private Usuario adquirente;
    private String dataPublicacaoMin;
    private String dataPublicacaoMax;
    private Categoria categoria;
    private Estado estado;
    private Cidade cidade;

    public LocalDate getDataPublicacaoMin() {
        return LocalDate.now();
    }

    public LocalDate getDataPublicacaoMax() {
        return LocalDate.now();
    }
}
