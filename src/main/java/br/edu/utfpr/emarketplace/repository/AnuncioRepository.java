package br.edu.utfpr.emarketplace.repository;

import br.edu.utfpr.emarketplace.model.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
}
