package br.edu.utfpr.emarketplace.service;


import br.edu.utfpr.emarketplace.model.Usuario;

import java.util.List;

public interface UsuarioService extends CrudService<Usuario, Long> {
    Usuario getUsuarioLogado();

    Usuario cadastrarUsuario(Usuario usuario) throws Exception;

    void deletarUsuario(Long id);

    List<Usuario> listarTodos();


}
