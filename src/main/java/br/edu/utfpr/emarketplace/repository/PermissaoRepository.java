package br.edu.utfpr.emarketplace.repository;


import br.edu.utfpr.emarketplace.model.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    Permissao findByNome(String nome);
}
