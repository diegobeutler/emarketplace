package br.edu.utfpr.emarketplace.controller;


import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;


    @PostMapping
    private Usuario salvar(@RequestBody Usuario usuario) throws Exception {
        return usuarioService.salvar(usuario);
    }

    @DeleteMapping("{id}")
    private void excluir(@PathVariable("id") Long id) {
        usuarioService.excluir(id);
    }

    @GetMapping("pesquisar-todos")
    private List<Usuario> pesquisarTodos() {
        return usuarioService.listarTodos();
    }

//    @GetMapping("{id}")
//    private Usuario findById(@PathVariable("id") Long id) {
//        return usuarioService.findById(id);
//    }

    @GetMapping("logado")
    private Usuario findUsuarioLogado() {
        return usuarioService.getUsuarioLogado();
    }
}
