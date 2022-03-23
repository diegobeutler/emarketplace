package br.edu.utfpr.emarketplace.controller;


import br.edu.utfpr.emarketplace.model.Categoria;
import br.edu.utfpr.emarketplace.model.Estado;
import br.edu.utfpr.emarketplace.service.CategoriaService;
import br.edu.utfpr.emarketplace.service.EstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping("complete-by-descricao")
    private List<Categoria> completeByDescricao(@RequestParam("query") String query) {
        return categoriaService.completeByDescricao(query);
    }
}
