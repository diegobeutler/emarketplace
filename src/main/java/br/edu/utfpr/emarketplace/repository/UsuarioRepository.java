package br.edu.utfpr.emarketplace.repository;


import br.edu.utfpr.emarketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findUsuarioByUsername(String username);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Modifying
    @Query("update Usuario set imagem = :pathFile " +
            "where id = :id")
    void updateImagem(@Param("id") Long id, @Param("pathFile") String pathFile);
}
