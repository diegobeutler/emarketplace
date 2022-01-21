package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.model.Cidade;
import br.edu.utfpr.emarketplace.repository.CidadeRepository;
import br.edu.utfpr.emarketplace.service.CidadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CidadeServiceImpl extends CrudServiceImpl<Cidade, Long> implements CidadeService {
    private final CidadeRepository cidadeRepository;


    @Override
    public JpaRepository<Cidade, Long> getRepository() {
        return cidadeRepository;
    }

    @Override
    public List<Cidade> completeByEstadoAndNome(Long idEstado, String query) {
//        query = "%" + query + "%";
        return cidadeRepository.findByEstado_IdAndNomeContainingIgnoreCaseOrderByNome(idEstado, query);
    }
}
