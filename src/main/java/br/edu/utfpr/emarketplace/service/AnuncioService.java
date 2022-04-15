package br.edu.utfpr.emarketplace.service;

import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;

import java.util.List;

public interface AnuncioService extends CrudService<Anuncio, Long> {
    Anuncio salvar(Anuncio anuncio) throws Exception;

    void excluir(Long id);

    List<Anuncio> listarTodos();

    List<Anuncio> findAnunciosByFilter(AnuncioFilter anuncioFilter);

    void convidarInstituicao(String emailInstituicao);
}
