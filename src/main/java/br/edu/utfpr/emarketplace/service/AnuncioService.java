package br.edu.utfpr.emarketplace.service;

import br.edu.utfpr.emarketplace.exception.RecursoNaoPermitidoException;
import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;

import java.util.List;

public interface AnuncioService extends CrudService<Anuncio, Long> {

    Anuncio salvar(Anuncio anuncio) throws Exception;

    void excluir(Long id);

    List<Anuncio> findAnunciosByFilter(AnuncioFilter anuncioFilter);

    void convidarInstituicao(String emailInstituicao);

    Anuncio updateStatus(Anuncio anuncio);

    Anuncio findAnuncioById(Long id) throws RecursoNaoPermitidoException;
}
