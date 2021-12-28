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
@Table(name = "CIDADE")
public class Cidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CIDADE")
    private Long id;

    @Column(name = "NOME", length = 120)
    @Max(120)
    private String nome;

    @JoinColumn(name = "ID_ESTADO")
    @ManyToOne(optional = false)
    private Estado estado;
}
