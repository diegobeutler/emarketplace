package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.exception.UsuarioJaExisteException;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.repository.PermissaoRepository;
import br.edu.utfpr.emarketplace.repository.UsuarioRepository;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Long> implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder encoder;

    private final PermissaoRepository permissaoRepository;


    @Override
    public JpaRepository<Usuario, Long> getRepository() {
        return usuarioRepository;
    }

    @Override
    public void valid(Usuario entity) throws UsuarioJaExisteException {
        if(entity.getId() == null)
            if (usuarioRepository.findUsuarioByUsername(entity.getUsername()).isPresent())
                throw new UsuarioJaExisteException("Usuario " + entity.getUsername() + " já existe");

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findUsuarioByUsername(username).orElse(null);
        if (usuario == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        usuario.getPermissoes().forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getNome())));
        return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    @Override
    public Usuario cadastrarUsuario(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            usuario.setPermissoes(Set.of(permissaoRepository.findByNome("ROLE_USER")));
        }
        return save(usuario);
    }

    @Override
    public void deletarUsuario(Long id) {
        delete(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return findAll();
    }

    @Override
    public Usuario getUsuarioLogado() {
        return usuarioRepository.findUsuarioByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElse(null);
    }
}
