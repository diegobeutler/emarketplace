package br.edu.utfpr.emarketplace.controller;


import br.edu.utfpr.emarketplace.model.Estado;
import br.edu.utfpr.emarketplace.service.EstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("estado")
@RequiredArgsConstructor
public class EstadoController {
    private final EstadoService estadoService;

    @GetMapping("complete-by-nome-or-uf")
    private List<Estado> completeByNomeOrUf(@RequestParam("query") String query) {
        return estadoService.completeByNomeOrUf(query);
    }
}
