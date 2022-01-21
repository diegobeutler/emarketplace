package br.edu.utfpr.emarketplace.model;

import br.edu.utfpr.emarketplace.enumeration.Operacao;
import br.edu.utfpr.emarketplace.enumeration.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(name = "ANUNCIO")
@TypeDef(
        name = "jsonb-node",
        typeClass = JsonNodeBinaryType.class
)
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ANUNCIO")
    private Long id;

    @Column(name = "TITULO", nullable = false)
    @Min(3)
    @Max(100)
    private String titulo;

    @Column(name = "DESCRICAO", length = 500)
    private String descricao;

    @Type(type = "jsonb-node")
    @Column(name = "CARACTERISTICAS", nullable = false, columnDefinition = "jsonb")
    private JsonNode caracteristicas;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPERACAO", length = 10, nullable = false)
    private Operacao operacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 13, nullable = false)
    private Status status;

    @Column(name = "VALOR", precision = 5, scale = 2)
    private BigDecimal valor;

    @Column(name = "DATA_PUBLICACAO", nullable = false)
    private LocalDate dataPublicacao;

    @Column(name = "DATA_DEVOLOCAO")
    private LocalDate dataDevolocao;

    @Column(name = "PRODUTOS_TROCA", length = 400)
    private String produtosTroca;

    @JoinColumn(name = "ID_USUARIO_ORIGEM")
    @ManyToOne(optional = false)
    private Usuario usuarioOrigem;

    @JoinColumn(name = "ID_USUARIO_DESTINO")
    @ManyToOne
    private Usuario usuarioDestino;

    @JoinColumn(name = "ID_USUARIO_INSTITUICAO")
    @ManyToOne
    private Usuario usuarioInstituicao;

    @JoinColumn(name = "ID_CATEGORIA")
    @ManyToOne(optional = false)
    private Categoria categoria;


}
