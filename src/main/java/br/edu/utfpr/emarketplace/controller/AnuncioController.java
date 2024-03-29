package br.edu.utfpr.emarketplace.controller;

import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;
import br.edu.utfpr.emarketplace.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("anuncio")
@RequiredArgsConstructor
public class AnuncioController {
    private final AnuncioService anuncioService;

    @PostMapping
    private Anuncio salvar(@RequestBody Anuncio anuncio) throws Exception {
        return anuncioService.salvar(anuncio);
    }

    @DeleteMapping("{id}")
    private void excluir(@PathVariable("id") Long id) {
        anuncioService.excluir(id);
    }

    @GetMapping("{id}")
    private Anuncio findById(@PathVariable("id") Long id) throws Exception {
        return anuncioService.findAnuncioById(id);
    }

    @PostMapping("/filter")
    private List<Anuncio> findAnunciosByFilter(@RequestBody AnuncioFilter anuncioFilter) {
        return anuncioService.findAnunciosByFilter(anuncioFilter);
    }

    @PostMapping("/convidar-instituicao")
    private void convidarInstituicao(@RequestBody String emailInstituicao) {
        anuncioService.convidarInstituicao(emailInstituicao);
    }

    @PostMapping("/update-status")
    private Anuncio updateStatus(@RequestBody Anuncio anuncio) {
        return anuncioService.updateStatus(anuncio);
    }
}
