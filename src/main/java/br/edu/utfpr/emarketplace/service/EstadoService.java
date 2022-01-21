package br.edu.utfpr.emarketplace.service;


import br.edu.utfpr.emarketplace.model.Estado;

import java.util.List;

public interface EstadoService extends CrudService<Estado, Long> {
    List<Estado> listarTodos();

    List<Estado> completeByNomeOrUf(String query);
}
