package br.edu.utfpr.emarketplace.model;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Audited
@Builder
@Table(name = "PERMISSAO")
public class Permissao implements Serializable,
        GrantedAuthority {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERMISSAO")
    private Long id;

    @Column(name = "NOME", length = 20, nullable = false)
    private String nome;

    @Override
    public String getAuthority() {
        return this.nome;
    }
}
