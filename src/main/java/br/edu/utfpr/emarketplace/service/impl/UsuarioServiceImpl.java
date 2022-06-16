package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.enumeration.Permissao;
import br.edu.utfpr.emarketplace.exception.UsuarioInativoException;
import br.edu.utfpr.emarketplace.exception.UsuarioJaExisteException;
import br.edu.utfpr.emarketplace.exception.UsuarioNaoAutenticadoException;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.repository.PermissaoRepository;
import br.edu.utfpr.emarketplace.repository.UsuarioRepository;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.amazonS3Bucket.AmazonS3BucketService;
import br.edu.utfpr.emarketplace.service.email.EnvioEmailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.util.*;
import java.util.stream.Collectors;

import static br.edu.utfpr.emarketplace.service.utils.ImageUtils.converterDataToByte;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Long> implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder encoder;

    private final PermissaoRepository permissaoRepository;

    private final AmazonS3BucketService amazonS3BucketService;

    private final EnvioEmailService envioEmailService;

    private byte[] imagedata;


    @Override
    public JpaRepository<Usuario, Long> getRepository() {
        return usuarioRepository;
    }

    @Override
    public void valid(Usuario entity) throws UsuarioJaExisteException {
        if (entity.getId() == null)
            if (usuarioRepository.findUsuarioByUsername(entity.getUsername()).isPresent())
                throw new UsuarioJaExisteException("Usuário " + entity.getUsername() + " já existe");
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findUsuarioByUsername(username).orElse(null);
        if (usuario == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else if (!usuario.getAtivo()) {
            log.error("User inative in the database");
            throw new UsuarioInativoException("Usuário inativo.");
        }
        log.info("User found in the database {}", username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        usuario.getPermissoes().forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getNome())));
        return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    @Override
    public void preSave(Usuario usuario) {
        tratarImageUserPreSave(usuario);
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
        var instituicao = findById(id);
        if (instituicao != null) {
            this.envioEmailService.notificarInstituicaoEmail(instituicao.getEmail());
        }
    }

    @SneakyThrows
    @Override
    public Usuario findById(Long id) {
        var usuario = getUsuarioLogado();
        if(Objects.isNull(usuario)){
            throw new UsuarioNaoAutenticadoException("Usuário não autenticado");
        }
        if (usuario.getPermissoes().stream()
                .anyMatch(permissao -> Permissao.ROLE_ADMIN.name().equals(permissao.getNome())) && !usuario.getId().equals(id)) {
            var usuarioAux = ofNullable(super.findById(id));
            if(usuarioAux.isPresent()){
                if(!usuarioAux.get().getAtivo()){
                    usuario = usuarioAux.get();
                    usuario.setVadationInstitution(true);
                }
            }
        }
        return usuario;
    }

    private void tratarImageUserPreSave(Usuario usuario) {
        imagedata = null;
        if (usuario.getImagem().startsWith("data")) {
            String data = usuario.getImagem();
            imagedata = converterDataToByte(data);
            usuario.setImagem(null);
        } else if (usuario.isDeleteImage()) {
            amazonS3BucketService.deleteFileFromBucket(usuario.getId() + ".png", true);
        }
    }

    private void validateEnvioEmailCadastroInstitution(Usuario usuario) {
        if (!usuario.getAtivo()) {
            var usersAdmin = usuarioRepository.findUsuarioByPermissoesContaining(Permissao.ROLE_ADMIN.getPermissao());
            if (usersAdmin.isPresent()) {
                var emails = usersAdmin.get().stream()
                        .map(Usuario::getEmail)
                        .collect(Collectors.toSet());
                envioEmailService.validateInstituicaoEmail("http://localhost:4200/usuario/form?id=" + usuario.getId(), emails);
            }
        }
    }

    private void salvarImageUser(Usuario usuario) {
        if (imagedata != null) {
            try {
                File file = new File(usuario.getId() + ".png");
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                ImageIO.write(bufferedImage, "png", file);
                usuarioRepository.updateImagem(usuario.getId(), amazonS3BucketService.uploadFile(file, true));
            } catch (Exception ex) {
                log.error("Erro salvando imagem do usuário: " + ex.getMessage());
                // todo adicionar exceção personalizada com mensagem....
            }
        }
    }

    public List<Usuario> completeByInstituicaoAndNome(String query) {
        return usuarioRepository.findByPermissoesContainingAndNomeContainingIgnoreCaseAndAtivoOrderByNome(
                Permissao.ROLE_INSTITUTION.getPermissao(), query, true
        );
    }
}
