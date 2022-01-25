package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.model.Categoria;
import br.edu.utfpr.emarketplace.repository.CategoriaRepository;
import br.edu.utfpr.emarketplace.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriaServiceImpl extends CrudServiceImpl<Categoria, Long> implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public JpaRepository<Categoria, Long> getRepository() {
        return categoriaRepository;
    }

    @Override
    public void valid(Categoria entity) throws Exception {

    }

    @Override
    public Categoria salvar(Categoria anuncio) throws Exception {
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
    public List<Categoria> listarTodos() {
        return null;
    }
}
