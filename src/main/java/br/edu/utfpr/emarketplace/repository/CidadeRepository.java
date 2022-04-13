package br.edu.utfpr.emarketplace.repository;


import br.edu.utfpr.emarketplace.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstadoIdAndNomeContainingIgnoreCaseOrderByNome(@Param("id") Long id, String nome);
}
