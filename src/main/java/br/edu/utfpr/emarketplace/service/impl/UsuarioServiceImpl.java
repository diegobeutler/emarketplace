package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.enumeration.Permissao;
import br.edu.utfpr.emarketplace.exception.UsuarioJaExisteException;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.repository.PermissaoRepository;
import br.edu.utfpr.emarketplace.repository.UsuarioRepository;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.amazonS3Bucket.AmazonS3BucketServiceImpl;
import br.edu.utfpr.emarketplace.service.email.EnvioEmailServiceImpl;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Long> implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder encoder;

    private final PermissaoRepository permissaoRepository;

    private final AmazonS3BucketServiceImpl amazonS3BucketService;

    private final EnvioEmailServiceImpl envioEmailServiceImpl;

    private byte[] imagedata;


    @Override
    public JpaRepository<Usuario, Long> getRepository() {
        return usuarioRepository;
    }

    @Override
    public void valid(Usuario entity) throws UsuarioJaExisteException {
        if (entity.getId() == null)
            if (usuarioRepository.findUsuarioByUsername(entity.getUsername()).isPresent())
                throw new UsuarioJaExisteException("Usuario " + entity.getUsername() + " já existe");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findUsuarioByUsername(username).orElse(null);
        if (usuario == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else if (!usuario.getAtivo()) {
            log.error("User inative in the database");
            throw new UsernameNotFoundException("User inative in the database");
        }
        log.info("User found in the database {}", username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        usuario.getPermissoes().forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getNome())));
        return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Usuario salvar(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            usuario.setPermissoes(Set.of(
                    permissaoRepository.findByNome(usuario.isEhInstitution() ? Permissao.ROLE_INSTITUTION.name() : Permissao.ROLE_USER.name())
            ));
            usuario.setAtivo(!usuario.isEhInstitution());
        }
        return save(usuario);
    }

    @Override
    public void preSave(Usuario usuario) {
        tratarImageUserPreSave(usuario);
    }

    @Override
    public void postSave(Usuario usuario) {
        salvarImageUser(usuario);
        validateEnvioEmailCadastroInstitution(usuario);
    }

    @Override
    public void excluir(Long id) {
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

    @Override
    public void changeUserPassword(Usuario usuario, String newPassword) throws Exception {
        usuario.setPassword(encoder.encode(newPassword));
        salvar(usuario);
    }

    @Override
    public void ativaCadastroInstitution(Long id) {
        usuarioRepository.updateAtivo(id);
    }

    @Override
    public Usuario findById(Long id) {
        var usuario = getUsuarioLogado();
        if (usuario != null && usuario.getPermissoes().stream()
                .anyMatch(permissao -> Permissao.ROLE_ADMIN.name().equals(permissao.getNome())) && !usuario.getId().equals(id)) {
            usuario = super.findById(id);
            usuario.setVadationInstitution(true);
        }
        return usuario;
    }

    private void tratarImageUserPreSave(Usuario usuario) {
        imagedata = null;
        if (usuario.getImagem().startsWith("data")) {
            String str = usuario.getImagem();
            imagedata = java.util.Base64.getDecoder().decode(str.substring(str.indexOf(",") + 1));
            usuario.setImagem(null);
        } else if (usuario.isDeleteImage()) {
            amazonS3BucketService.deleteFileFromBucket(usuario.getId() + ".png");
        }
    }

    private void validateEnvioEmailCadastroInstitution(Usuario usuario) {
        if (!usuario.getAtivo()) {
            var usersAdmin = usuarioRepository.findUsuarioByPermissoesContaining(Permissao.ROLE_ADMIN.getPermissao());
            if (usersAdmin.isPresent()) {
                var emails = usersAdmin.get().stream()
                        .map(Usuario::getEmail)
                        .collect(Collectors.toSet());
                envioEmailServiceImpl.validateInstituicaoEmail("http://localhost:4200/usuario/form?id=" + usuario.getId(), emails);
            }
        }
    }

    private void salvarImageUser(Usuario usuario) {
        if (imagedata != null) {
            try {
                File file = new File(usuario.getId() + ".png");
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                ImageIO.write(bufferedImage, "png", new File(usuario.getId() + ".png"));
                usuarioRepository.updateImagem(usuario.getId(), amazonS3BucketService.uploadFile(file));
            } catch (Exception ex) {
                log.error("Erro salvando imagem do usuário: " + ex.getMessage());
                // todo adicionar exceção personalizada com mensagem....
            }
        }
    }
}
