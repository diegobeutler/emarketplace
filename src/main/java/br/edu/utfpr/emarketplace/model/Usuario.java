package br.edu.utfpr.emarketplace.model;

import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Audited
@Table(name = "USUARIO")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotNull
    @Column(name = "APELIDO", nullable = false)
    private String apelido;

    @NotNull
    @Column(name = "EMAIL", nullable = false)
    @Email
    private String email;// todo talvez ser o username

    @NotNull
    @Column(name = "TELEFONE", nullable = false)
    private String telefone;

    @Column(name = "URL_IMAGEM")
    private String imagem;

    @Column(name = "USERNAME", length = 100, nullable = false)// todo pode dar problema
//    @Max(100)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @ManyToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private Set<Permissao> permissoes = new HashSet<>();

    @NotAudited
    @JoinColumn(name = "ID_CIDADE")
    @ManyToOne
    private Cidade cidade;

    @Transient
    private transient boolean deleteImage;

   public boolean isUpdateImage() {
        return imagem.startsWith("data");
    }
}
