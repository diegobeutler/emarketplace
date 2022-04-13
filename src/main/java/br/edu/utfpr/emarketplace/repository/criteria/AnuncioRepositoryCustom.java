package br.edu.utfpr.emarketplace.repository.criteria;

import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;

import java.util.List;

public interface AnuncioRepositoryCustom {

    List<Anuncio> findAnunciosByFilter(AnuncioFilter anuncioFilter);
}
