package br.edu.utfpr.emarketplace.service;


import br.edu.utfpr.emarketplace.model.Usuario;

import java.util.List;

public interface UsuarioService extends CrudService<Usuario, Long> {
    Usuario getUsuarioLogado();

    Usuario salvar(Usuario usuario) throws Exception;

    void excluir(Long id);

    List<Usuario> listarTodos();

    void changeUserPassword(Usuario usuario, String newPassword) throws Exception;


}
