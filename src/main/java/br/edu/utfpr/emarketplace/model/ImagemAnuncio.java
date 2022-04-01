package br.edu.utfpr.emarketplace.model;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(name = "IMAGEM_ANUNCIO")
public class ImagemAnuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_IMAGEM_ANUNCIO")
    private Long id;

    @Column(name = "URL_IMAGEM", nullable = false)
    private String url;
}
