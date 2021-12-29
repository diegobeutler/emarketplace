package br.edu.utfpr.emarketplace.repository;

import br.edu.utfpr.emarketplace.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
