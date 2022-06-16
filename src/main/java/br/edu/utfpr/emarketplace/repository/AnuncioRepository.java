package br.edu.utfpr.emarketplace.repository;

import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.criteria.AnuncioRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long>, AnuncioRepositoryCustom {

    List<Anuncio> findAllByUsuarioOrigemId(Long id);
}
