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
@Table(name = "PAIS")
public class Pais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAIS")
    private Long id;

    @Column(name = "SIGLA", length = 2)
    @Max(2)
    private String sigla;

    @Column(name = "NOME", length = 80)
    @Max(80)
    private String nome;



}
