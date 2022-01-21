package br.edu.utfpr.emarketplace.repository;


import br.edu.utfpr.emarketplace.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstado_IdAndNomeContainingIgnoreCaseOrderByNome(Long idEstado, String nome);
}
