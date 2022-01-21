package br.edu.utfpr.emarketplace.service;


import br.edu.utfpr.emarketplace.model.Cidade;

import java.util.List;

public interface CidadeService extends CrudService<Cidade, Long> {
    List<Cidade> completeByEstadoAndNome(Long idEstado, String query);
}
