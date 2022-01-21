package br.edu.utfpr.emarketplace.repository;


import br.edu.utfpr.emarketplace.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadoRepository extends JpaRepository<Estado, Long> {
    @Query("select distinct(e) from Estado e " +
            "where lower(e.nome) like lower(:query) " +
            "or lower(e.uf) like lower(:query) " +
            "order by e.nome asc")
    List<Estado> completeByNomeOrUf(@Param("query") String query);
}
