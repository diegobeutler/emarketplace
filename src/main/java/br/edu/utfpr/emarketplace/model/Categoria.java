package br.edu.utfpr.emarketplace.model;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Max;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(name = "CATEGORIA")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORIA")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false, length = 50)
    @Max(50)
    private String descricao;

    @JoinColumn(name = "ID_CATEGORIA_PAI")
    @ManyToOne
    private Categoria categoriaPai;

}
