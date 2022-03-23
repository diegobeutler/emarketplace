package br.edu.utfpr.emarketplace.service;

import br.edu.utfpr.emarketplace.model.Categoria;

import java.util.List;

public interface CategoriaService extends CrudService<Categoria, Long> {
    List<Categoria> completeByDescricao(String query);

}
