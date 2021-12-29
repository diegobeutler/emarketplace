package br.edu.utfpr.emarketplace.service;

import br.edu.utfpr.emarketplace.model.Categoria;

import java.util.List;

public interface CategoriaService extends CrudService<Categoria, Long> {
    Categoria salvar(Categoria anuncio) throws Exception;

    void excluir(Long id);

    List<Categoria> listarTodos();

}
