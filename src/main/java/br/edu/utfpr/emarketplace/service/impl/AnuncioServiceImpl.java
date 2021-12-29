package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.AnuncioRepository;
import br.edu.utfpr.emarketplace.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AnuncioServiceImpl extends CrudServiceImpl<Anuncio, Long> implements AnuncioService {

    private final AnuncioRepository anuncioRepository;
    @Override
    public JpaRepository<Anuncio, Long> getRepository() {
        return anuncioRepository;
    }

    @Override
    public void valid(Anuncio entity) throws Exception {

    }

    @Override
    public Anuncio salvar(Anuncio anuncio) throws Exception {
        if (anuncio.getId() == null) {
//
        }
        return save(anuncio);
    }

    @Override
    public void excluir(Long id) {
        delete(id);
    }

    @Override
    public List<Anuncio> listarTodos() {
        return null;
    }
}
