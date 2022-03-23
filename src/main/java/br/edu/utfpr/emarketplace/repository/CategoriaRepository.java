package br.edu.utfpr.emarketplace.repository;

import br.edu.utfpr.emarketplace.model.Categoria;
import br.edu.utfpr.emarketplace.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByDescricaoContainingIgnoreCaseOrderByDescricao(String query);
}
