//package br.edu.utfpr.emarketplace.controller;
//
//import br.edu.utfpr.editorartigos.model.Categoria;
//import br.edu.utfpr.editorartigos.service.CategoriaService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("categoria/")
//@RequiredArgsConstructor
//public class CategoriaController {
//
//    private final CategoriaService categoriaService;
//
//    @PostMapping("incluir")
//    public Categoria incluir(@RequestBody Categoria categoria) throws Exception {
//        return categoriaService.cadastrarCategoria(categoria);
//    }
//
//    @PutMapping("atualizar")
//    public Categoria atualizar(@RequestBody Categoria categoria) throws Exception {
//        return categoriaService.cadastrarCategoria(categoria);
//    }
//
//    @DeleteMapping("{id}")
//    public void excluir(@PathVariable("id") Long id) {
//        categoriaService.deletarCategoria(id);
//    }
//
//    @GetMapping("pesquisar-todos")
//    public List<Categoria> pesquisarTodos() {
//        return categoriaService.listarTodos();
//    }
//
//    @GetMapping("{id}")
//    public Categoria findOne(@PathVariable("id") Long id) {
//        return categoriaService.findById(id);
//    }
//}
