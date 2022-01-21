package br.edu.utfpr.emarketplace.service.impl;


import br.edu.utfpr.emarketplace.exception.UsuarioJaExisteException;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.repository.PermissaoRepository;
import br.edu.utfpr.emarketplace.repository.UsuarioRepository;
import br.edu.utfpr.emarketplace.service.AmazonS3Bucket.AmazonS3BucketServiceImpl;
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
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

    private final AmazonS3BucketServiceImpl amazonS3BucketService;

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
        } else {
            log.info("User found in the database {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        usuario.getPermissoes().forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getNome())));
        return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    @Override
    public Usuario salvar(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            usuario.setPermissoes(Set.of(permissaoRepository.findByNome("ROLE_USER")));
        }
        return save(usuario);
    }

    @Override
    public void preSave(Usuario usuario){
        if (usuario.isUpdateImage()) {
            String str = usuario.getImagem();
            imagedata = java.util.Base64.getDecoder().decode(str.substring(str.indexOf(",") + 1));
            usuario.setImagem(null);
        } else if(usuario.isDeleteImage()){
            amazonS3BucketService.deleteFileFromBucket(usuario.getId() +".png");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postSave(Usuario usuario) throws IOException {
        if (imagedata!=null) {
            File file = new File(usuario.getId() +".png");
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
            ImageIO.write(bufferedImage, "png", new File(usuario.getId() +".png"));
            usuario.setImagem(amazonS3BucketService.uploadFile(file));
            imagedata = null;
        }
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
}
