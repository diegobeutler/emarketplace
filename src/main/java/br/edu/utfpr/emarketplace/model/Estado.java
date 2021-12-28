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
@EqualsAndHashCode(of = {"id"})
@Table(name = "Estado")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long id;

    @Column(name = "NOME", length = 20)
    @Max(20)
    private String nome;

    @Column(name = "UF", length = 2)
    @Max(2)
    private String uf;

    @JoinColumn(name = "ID_PAIS")
    @ManyToOne(optional = false)
    private Pais pais;

}
