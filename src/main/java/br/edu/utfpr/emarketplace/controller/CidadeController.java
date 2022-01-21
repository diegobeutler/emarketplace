package br.edu.utfpr.emarketplace.controller;


import br.edu.utfpr.emarketplace.model.Cidade;
import br.edu.utfpr.emarketplace.service.CidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cidade")
@RequiredArgsConstructor
public class CidadeController {
    private final CidadeService cidadeService;

    @GetMapping("complete-by-estado-and-nome")
    private List<Cidade> completeByEstadoAndNome(@RequestParam("idEstado") Long idEstado, @RequestParam("query") String query) {
        return cidadeService.completeByEstadoAndNome(idEstado, query);
    }
}
