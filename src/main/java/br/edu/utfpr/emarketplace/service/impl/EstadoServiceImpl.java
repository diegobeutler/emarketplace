package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.model.Estado;
import br.edu.utfpr.emarketplace.repository.EstadoRepository;
import br.edu.utfpr.emarketplace.service.EstadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadoServiceImpl extends CrudServiceImpl<Estado, Long> implements EstadoService {
    private final EstadoRepository estadoRepository;


    @Override
    public JpaRepository<Estado, Long> getRepository() {
        return estadoRepository;
    }

    @Override
    public List<Estado> listarTodos() {
        return findAll();
    }

    @Override
    public List<Estado> completeByNomeOrUf(String query) {
        query = "%" + query + "%";
        return estadoRepository.completeByNomeOrUf(query);
    }
}
